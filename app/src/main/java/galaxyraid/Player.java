package galaxyraid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;


public class Player {
    private BufferedImage sprite;
    private int x, y; // Player's position
    private double speed; // Speed at which the player moves
    private GameWindow gameWindow; // Reference to the game window
    private boolean moveLeft, moveRight; // Directional movement flags
    private boolean boosting = false; // Whether the player is boosting
    private boolean canFire = true; // Whether the player can fire a projectile

    // Controls projectile fire rate
    private final long cooldownNanos = 150_000_000L; // 150ms cooldown for firing
    private long lastShotTime = System.nanoTime() - cooldownNanos; // Time of last shot

    // Boost system variables
    private final int maxBoost = 100; // Max boost amount
    private double boostAmount = maxBoost; // Current boost amount
    private final double boostSpeedMultiplier = 1.75; // Speed multiplier when boosting
    private final double boostDepletionRate = 0.5; // Depletion rate of boost

    /**
     * Constructor to initialize the player with position and reference to game window.
     * 
     * @param x Initial X position of the player
     * @param y Initial Y position of the player
     * @param gameWindow Reference to the game window
     */
    public Player(int x, int y, GameWindow gameWindow) {
        sprite = SpriteLoader.load("player.png");

        this.x = x;
        this.y = y;
        this.gameWindow = gameWindow;
        this.speed = getDynamicSpeed(); // Initialize player speed based on screen width
    }

    // Update the player's movement
    public void update() {
        double currentSpeed = getDynamicSpeed(); // Get speed based on resolution

        // If boosting and we have fuel left, use the boost multiplier
        if (boosting && boostAmount > 0) {
            currentSpeed *= boostSpeedMultiplier;
            boostAmount -= boostDepletionRate;
            if (boostAmount < 0) boostAmount = 0; // Ensure boost doesn't go below 0
        }

        // Apply directional movement based on flags
        if (moveLeft) x -= currentSpeed;
        if (moveRight) x += currentSpeed;

        // Clamp player position within screen bounds
        int width = 40; // Width of the player's spaceship
        if (x < 0) x = 0; // Don't allow moving off the left edge
        if (x > gameWindow.getWidth() - width) x = gameWindow.getWidth() - width; // Don't allow moving off the right edge
    }

    // Check if the player can shoot based on cooldown
    private boolean canShoot(long now) {
        return now - lastShotTime >= cooldownNanos; // Return true if enough time has passed
    }

    // Handle shooting action
    private void shoot(long now) {
        // Shoot from the tip of the player's spaceship
        SoundPlayer.play("shoot.wav");

        int tipX = x + 20;
        int tipY = y;
        gameWindow.shootPlayerProjectileFrom(tipX, tipY); // Call game window to create projectile
        lastShotTime = now; // Update last shot time
    }

    // Handle key press events for player movement and actions
    public void handleKeyPress(KeyEvent e) {
        int code = e.getKeyCode();
        long now = System.nanoTime();

        switch (code) {
            case KeyEvent.VK_LEFT:
                moveLeft = true; // Start moving left
                break;
            case KeyEvent.VK_RIGHT:
                moveRight = true; // Start moving right
                break;
            case KeyEvent.VK_SPACE:
                // Only fire if allowed and cooldown has passed
                if (canFire && canShoot(now)) {
                    shoot(now);
                    canFire = false; // Block holding down space
                }
                break;
            case KeyEvent.VK_SHIFT:
                boosting = true; // Start boosting
                break;
        }
    }

    // Handle key release events for player movement and actions
    public void handleKeyRelease(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_LEFT:
                moveLeft = false; // Stop moving left
                break;
            case KeyEvent.VK_RIGHT:
                moveRight = false; // Stop moving right
                break;
            case KeyEvent.VK_SPACE:
                canFire = true; // Allow firing again
                break;
            case KeyEvent.VK_SHIFT:
                boosting = false; // Stop boosting
                break;
        }
    }

    // Draw the player (spaceship) on the screen as a triangle
    public void draw(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, 30, 60, null); // Width: 30, Height: 60
        }
    }
    
    // Get the player's X position
    public int getX() {
        return x;
    }

    // Get the player's Y position
    public int getY() {
        return y;
    }

    // Get the player's bounding box for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, 30, 60);
    }
    
    // Speed adapts based on screen resolution
    private double getDynamicSpeed() {
        return gameWindow.getWidth() / 130.0; // Dynamically adjust speed based on screen width
    }

    // Used by GamePanel to display the boost amount
    public int getBoostAmount() {
        return (int) boostAmount;
    }

    // Get the maximum boost value
    public int getMaxBoost() {
        return maxBoost;
    }

    // Refill boost when an enemy is killed
    public void refillBoost(double amount) {
        boostAmount += amount;
        if (boostAmount > maxBoost) boostAmount = maxBoost; // Ensure boost doesn't exceed max value
    }
}
