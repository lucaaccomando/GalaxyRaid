package galaxyraid;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameWindow extends JFrame {
    

    private static final long serialVersionUID = 1L;
    private BufferedImage redSprite, yellowSprite, greenSprite;


    private GamePanel gamePanel;
    private Player player;
    private List<EnemyShip> enemies;
    private List<Projectile> playerProjectiles;
    private List<BossProjectile> bossProjectiles = new ArrayList<>();

    private List<Projectile> enemyProjectiles;
    private BossEnemy boss;

    private long bossStartTime = 0;
    private final int BOSS_TIME_LIMIT = 120; // seconds
    private boolean bossTimerActive = false;

    private Timer gameTimer;
    private int score;
    private boolean gameEnded = false;
    private boolean paused = false;
    private boolean settingsMode = false;
    private int waveNumber = 4;

    private double enemySpeed = 6.0;
    private boolean movingRight = true;

    private long lastEnemyShotTime = 0;
    private long enemyShotCooldown = 1000;

    private Rectangle continueButton;
    private Rectangle exitButton;
    private Rectangle settingsButton;
    private Rectangle backButton;

    private List<ResolutionOption> resolutionOptions;
    private List<FloatingText> floatingTexts = new ArrayList<>();
    private int lives = 3;
    private static final int MAX_LIVES = 3;

    private int currentResolutionIndex = 0;
    private BufferedImage bossSprite;

    
  
   

    public GameWindow() {
        
        bossSprite = SpriteLoader.load("boss.png");
        setUndecorated(true); // Remove window decorations for fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(this); // Set window to fullscreen

        gamePanel = new GamePanel(this); // Initialize the game panel
        setContentPane(gamePanel);

        playerProjectiles = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();
        enemies = new ArrayList<>();
        score = 0;

        setFocusable(true);
        requestFocusInWindow(); // Request focus for key events

        // Key listener to handle player movement and actions
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (settingsMode) {
                        settingsMode = false;
                        gamePanel.repaint();
                    } else {
                        togglePause(); // Toggle pause on ESC key press
                    }
                }
                if (!paused) {
                    player.handleKeyPress(e); // Handle key press for movement
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!paused) {
                    player.handleKeyRelease(e); // Handle key release for movement
                }
            }
        });

        // Mouse listener for settings menu and pause menu
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();

                if (settingsMode) {
                    if (backButton != null && backButton.contains(p)) {
                        settingsMode = false;
                        gamePanel.repaint();
                        return;
                    }

                    // Check if any resolution option is clicked
                    for (ResolutionOption option : resolutionOptions) {
                        if (option.bounds.contains(p)) {
                            applyResolution(option);
                            settingsMode = false;
                            togglePause();
                            return;
                        }
                    }
                } else if (paused) {
                    // Handle clicks in the pause menu
                    if (continueButton.contains(p)) {
                        togglePause(); // Continue the game
                    } else if (exitButton.contains(p)) {
                        System.exit(0); // Exit the game
                    } else if (settingsButton.contains(p)) {
                        settingsMode = true; // Open settings menu
                        gamePanel.repaint();
                    }
                }
            }
        });

        // Handle resizing of the window
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                initializeGameObjects(); // Re-initialize game objects on resize
            }
        });

        initializeGameObjects();
        redSprite = SpriteLoader.load("red.png");
        yellowSprite = SpriteLoader.load("yellow.png");
        greenSprite = SpriteLoader.load("green.png");

        SoundPlayer.playLoop("spaceinvaders1.wav");

        gameTimer = new Timer(16, e -> gameLoop()); // Game loop timer
        gameTimer.start();

        // Repaint the screen every 33ms (about 30 frames per second) for paused/settings mode
        new Timer(1000 / 30, e -> {
            if (paused || settingsMode) {
                gamePanel.repaint();
            }
        }).start();
    }

    // Toggle the game between paused and unpaused states
    private void togglePause() {
        paused = !paused;

        if (paused) {
            setupResolutionOptions(); // Set up resolution options when paused
            SoundPlayer.stopLoop(); 
        } else {
            SoundPlayer.playLoop("spaceinvaders1.wav");
        }

        settingsMode = false;

        // Change cursor appearance based on pause state
        setCursor(paused
            ? Cursor.getDefaultCursor()
            : getToolkit().createCustomCursor(
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                new Point(),
                "invisible"
            )
        );

        updatePauseButtons();
        gamePanel.repaint();
    }

    // Update buttons for the pause menu
    private void updatePauseButtons() {
        int w = gamePanel.getWidth();
        int h = gamePanel.getHeight();
        int bw = 200, bh = 50;
        continueButton = new Rectangle(w / 2 - bw / 2, h / 2 - 100, bw, bh);
        settingsButton = new Rectangle(w / 2 - bw / 2, h / 2 - 40, bw, bh);
        exitButton = new Rectangle(w / 2 - bw / 2, h / 2 + 20, bw, bh);
    }

    // Set up the resolution options menu when the game is paused
    private void setupResolutionOptions() {
        int w = gamePanel.getWidth();
        int h = gamePanel.getHeight();
        int bw = 250, bh = 50;
        int startY = h / 2 - 140;

        resolutionOptions = new ArrayList<>();
        resolutionOptions.add(new ResolutionOption("1280 x 720", 1280, 720, new Rectangle(w / 2 - bw / 2, startY, bw, bh)));
        resolutionOptions.add(new ResolutionOption("1600 x 900", 1600, 900, new Rectangle(w / 2 - bw / 2, startY + 60, bw, bh)));
        resolutionOptions.add(new ResolutionOption("1920 x 1080", 1920, 1080, new Rectangle(w / 2 - bw / 2, startY + 120, bw, bh)));
        resolutionOptions.add(new ResolutionOption("Fullscreen", -1, -1, new Rectangle(w / 2 - bw / 2, startY + 180, bw, bh)));
        backButton = new Rectangle(w / 2 - bw / 2, startY + 260, bw, bh);
    }

    // Apply the selected resolution option
    private void applyResolution(ResolutionOption option) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        boolean isAlreadyFullscreen = option.width == -1 && option.height == -1 &&
                getWidth() == gd.getDisplayMode().getWidth() &&
                getHeight() == gd.getDisplayMode().getHeight();

        if (isAlreadyFullscreen) return; // Avoid reapplying fullscreen if already in fullscreen mode

        dispose();

        if (option.width == -1 || option.height == -1) {
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            gd.setFullScreenWindow(this);
        } else {
            gd.setFullScreenWindow(null);
            setUndecorated(false);
            setSize(option.width, option.height);
            setLocationRelativeTo(null);
        }

        setVisible(true);
        revalidate();
        repaint();

        initializeGameObjects();
        updatePauseButtons();
    }

    // Initialize game objects (player, enemies) when the game starts or is reset
    private void initializeGameObjects() {
        int panelWidth = gamePanel.getWidth();
        int panelHeight = gamePanel.getHeight();
        int playerX = panelWidth / 2 - 20;
        int playerY = panelHeight - 80;
        player = new Player(playerX, playerY, this); // Initialize the player
        initializeEnemies(3, 7, 40); // Initialize a grid of enemies
    }

    // Initialize the enemies in a grid formation
    private void initializeEnemies(int numRows, int numColumns, int spacing) {
        int panelWidth = gamePanel.getWidth();
        int enemyWidth = 40, enemyHeight = 40;
        int totalWidth = numColumns * (enemyWidth + spacing) - spacing;
        int startX = (panelWidth - totalWidth) / 2;
        int startY = 80;
    
        enemies.clear();
    
        for (int row = 0; row < numRows; row++) {
            // Assign a sprite to the entire row randomly
            BufferedImage rowSprite;
            int rand = (int)(Math.random() * 3);
            if (rand == 0) {
                rowSprite = redSprite;
            } else if (rand == 1) {
                rowSprite = yellowSprite;
            } else {
                rowSprite = greenSprite;
            }
    
            for (int col = 0; col < numColumns; col++) {
                int x = startX + col * (enemyWidth + spacing);
                int y = startY + row * (enemyHeight + spacing);
    
                //Make sure to pass the sprite into the constructor
                enemies.add(new EnemyShip(x, y, enemySpeed, rowSprite));
            }
        }
    }
    

    // The main game loop
    private void gameLoop() {
        // Update boss projectiles
        Iterator<BossProjectile> bossIter = bossProjectiles.iterator();
        while (bossIter.hasNext()) {
            BossProjectile bp = bossIter.next();
            bp.update(player);
        
            if (bp.isExploded()) {
                bossIter.remove();
            }
        }
        
        
        if (!paused && boss != null && !boss.isDefeated()) {
            boss.update(player);
        }
        if (boss != null && boss.isDefeated() && bossTimerActive) {
            int elapsedSeconds = (int) ((System.currentTimeMillis() - bossStartTime) / 1000);
            int remaining = Math.max(BOSS_TIME_LIMIT - elapsedSeconds, 0);
            int bonus = remaining * 10;
            score += bonus;
            floatingTexts.add(new FloatingText("+" + bonus + " Bonus!", boss.getBounds().x, boss.getBounds().y, Color.CYAN));
        
            bossTimerActive = false;
            boss = null;
            initializeEnemies(5, 11, 10);
        }
        
        

        if (gameEnded || paused) return; // Stop the loop if the game has ended or is paused

        player.update(); // Update player movement
        moveEnemies(); // Move enemies
        handleEnemyShooting(); // Handle enemy shooting

        enemies.forEach(enemy -> enemy.update(this)); // Update enemies
        playerProjectiles.forEach(Projectile::update); // Update player projectiles
        enemyProjectiles.forEach(Projectile::update); // Update enemy projectiles

        checkCollisions(); // Check for collisions
        gamePanel.repaint(); // Repaint the game panel
        floatingTexts.forEach(FloatingText::update);
        floatingTexts.removeIf(FloatingText::isExpired);
    }

    // Move enemies based on the current direction and reverse when they hit the edge
    private void moveEnemies() {
        int dx = movingRight ? (int) enemySpeed : -(int) enemySpeed;
        boolean shouldReverse = false;
        int margin = getWidth() / 16;

        for (EnemyShip enemy : enemies) {
            int nextX = enemy.getX() + dx;
            if (nextX <= margin || nextX + EnemyShip.getWidthValue() >= gamePanel.getWidth() - margin) {
                shouldReverse = true; // Reverse direction if the edge is hit
                break;
            }
        }

        if (shouldReverse) {
            enemies.forEach(enemy -> enemy.move(0, 20)); // Move enemies downwards
            movingRight = !movingRight; // Reverse movement direction
        } else {
            enemies.forEach(enemy -> enemy.move(dx, 0)); // Move enemies horizontally
        }
    }


    // Handle enemy shooting
    private void handleEnemyShooting() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEnemyShotTime >= enemyShotCooldown && !enemies.isEmpty()) {
            // Only front-line enemies (lowest Y in each column) can shoot
int spacing = 10; // match the spacing used in initializeEnemies
int columnWidth = EnemyShip.getWidthValue() + spacing;

Map<Integer, EnemyShip> frontLine = new HashMap<>();

for (EnemyShip enemy : enemies) {
    if (enemy.isExploding()) continue;

    int col = enemy.getX() / columnWidth;
    EnemyShip currentLowest = frontLine.get(col);

    if (currentLowest == null || enemy.getY() > currentLowest.getY()) {
        frontLine.put(col, enemy);
    }
}

List<EnemyShip> shooters = new ArrayList<>(frontLine.values());

if (!shooters.isEmpty() && Math.random() < 0.7) { // optional: ~50% chance to shoot
    EnemyShip shooter = shooters.get((int)(Math.random() * shooters.size()));
    shootEnemyProjectile(shooter);
    lastEnemyShotTime = System.currentTimeMillis();
}

            lastEnemyShotTime = currentTime;
        }
    }

    // Check for collisions between projectiles and enemies
    private void checkCollisions() {
        // Handle player projectile hitting enemies
        Iterator<Projectile> projectileIterator = playerProjectiles.iterator();
        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            for (EnemyShip enemy : enemies) {
                if (projectile.getBounds().intersects(enemy.getBounds()) && !enemy.isExploding()) {
                    enemy.explode();
                    SoundPlayer.play("invaderkilled.wav");
                    projectileIterator.remove();
                    score += 10;
                    player.refillBoost(10);
                    floatingTexts.add(new FloatingText("+10", enemy.getX(), enemy.getY(), Color.YELLOW));
                    break; // Only hit one enemy per projectile
                }
            }
        }
    
        // Remove enemies that finished exploding
        enemies.removeIf(enemy -> enemy.update(this));
    
        // Check for wave completion AFTER all removals
        if (enemies.isEmpty() && (boss == null || boss.isDefeated())) {
            waveNumber++;
            enemySpeed *= 1.05;
    
            if (lives < MAX_LIVES) {
                lives++;
            }
    
            if (waveNumber % 5 == 0) {
                boss = new BossEnemy(300, 50, bossSprite, this);
                bossStartTime = System.currentTimeMillis(); // ✅ Start the timer
                bossTimerActive = true;
            }
             else {
                initializeEnemies(5, 11, 10); // Regular enemy wave
            }
        }
    
        // Handle collisions: enemies touching player or reaching bottom
        for (EnemyShip enemy : enemies) {
            if (enemy.getBounds().intersects(player.getBounds()) ||
                enemy.getY() + 50 >= gamePanel.getHeight()) {
                lives--;
                if (lives <= 0) {
                    gameEnded = true;
                    showGameOver("GAME OVER! Score: " + score);
                }
                return;
            }
        }
    
        // Handle enemy projectiles hitting player
        enemyProjectiles.removeIf(projectile -> {
            if (projectile.getBounds().intersects(player.getBounds())) {
                lives--;
                if (lives <= 0) {
                    gameEnded = true;
                    showGameOver("GAME OVER! Score: " + score);
                }
                return true;
            }
            return false;
        });
        // Player projectiles hitting the boss
if (boss != null && !boss.isDefeated()) {
    Iterator<Projectile> bossHitCheck = playerProjectiles.iterator();
    while (bossHitCheck.hasNext()) {
        Projectile p = bossHitCheck.next();
        if (p.getBounds().intersects(boss.getBounds())) {
            boss.takeDamage(1);
            bossHitCheck.remove();
            floatingTexts.add(new FloatingText("-1", boss.getBounds().x, boss.getBounds().y, Color.RED));
        }
    }
}

    }
    

    // Display the game over screen and allow the player to either restart or exit
    private void showGameOver(String message) {
        gameTimer.stop();
        String[] options = {"Play Again", "Exit"};
        int choice = JOptionPane.showOptionDialog(
            this,
            message + "\nWhat would you like to do?",
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                dispose();
                new GameWindow(); // Restart the game
            });
        } else {
            System.exit(0); // Exit the game
        }
    }

    // Getters for various game objects and states
    public boolean isBossTimerActive() {
        return bossTimerActive;
    }
    
    public int getBossTimeRemaining() {
        if (!bossTimerActive) return 0;
        int elapsed = (int) ((System.currentTimeMillis() - bossStartTime) / 1000);
        return Math.max(BOSS_TIME_LIMIT - elapsed, 0);
    }
    
    public List<BossProjectile> getBossProjectiles() {
        return bossProjectiles;
    }
    public BossEnemy getBoss() {
        return boss;
    }
    
    
    public List<Projectile> getPlayerProjectiles() { return playerProjectiles; }
    public void shootPlayerProjectile() { shootPlayerProjectileFrom(player.getX() + 20, player.getY()); }
    public void shootPlayerProjectileFrom(int x, int y) { playerProjectiles.add(new Projectile(x, y, -1)); }
    public void shootEnemyProjectile(EnemyShip enemy) { enemyProjectiles.add(new Projectile(enemy.getX() + 20, enemy.getY(), 1)); }
    public int getWaveNumber() { return waveNumber; }
    public Player getPlayer() { return player; }
    public List<EnemyShip> getEnemies() { return enemies; }
    public List<Projectile> getEnemyProjectiles() { return enemyProjectiles; }
    public int getScore() { return score; }
    public GamePanel getGamePanel() { return gamePanel; }
    public boolean isPaused() { return paused; }
    public boolean isSettingsMode() { return settingsMode; }
    public Rectangle getContinueButton() { return continueButton; }
    public Rectangle getExitButton() { return exitButton; }
    public Rectangle getSettingsButton() { return settingsButton; }
    public Rectangle getBackButton() { return backButton; }
    public List<ResolutionOption> getResolutionOptions() { return resolutionOptions; }
    public List<FloatingText> getFloatingTexts() {
        return floatingTexts;
    }
    

    // ResolutionOption inner class to store resolution details
    public static class ResolutionOption {
        public String label;
        public int width, height;
        public Rectangle bounds;

        public ResolutionOption(String label, int width, int height, Rectangle bounds) {
            this.label = label;
            this.width = width;
            this.height = height;
            this.bounds = bounds;
        }
    }

public int getLives() {
    return lives;
}
}

