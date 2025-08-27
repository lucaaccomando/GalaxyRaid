package galaxyraid;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private final GameWindow window;
    private final List<Star> stars;

    public GamePanel(GameWindow window) {
        this.window = window;
        this.stars = new ArrayList<>();
        setBackground(Color.BLACK); // Set the background color of the game panel to black
    }

    // Generates random stars for the background
    private void generateStars() {
        stars.clear(); // Clear any existing stars
        int count = getWidth() * getHeight() / 8000; // Number of stars based on screen size
        for (int i = 0; i < count; i++) {
            int x = (int) (Math.random() * getWidth());
            int y = (int) (Math.random() * getHeight());
            int size = (int) (Math.random() * 2) + 1; // Star size between 1 and 2 pixels
            stars.add(new Star(x, y, size));
        }
    }

    // Paints the game scene (called every frame)
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g); // Call the superclass method to handle background clearing
        g.setFont(new Font("Arial", Font.BOLD, 20)); // ⬅️ Bigger and bold font

        for (BossProjectile bp : window.getBossProjectiles()) {
            bp.draw(g);
        }
        if (window.getBoss() != null) {
            window.getBoss().draw(g);
        }
        
        

        if (stars.isEmpty()) {
            generateStars(); // Generate stars if they haven't been created yet
        }

        g.setColor(Color.WHITE); // Set the color to white for drawing stars
        for (Star s : stars) {
            g.fillRect(s.x, s.y, s.size, s.size); // Draw each star
        }

        // Draw player, enemies, and projectiles
        window.getPlayer().draw(g);

        for (EnemyShip enemy : window.getEnemies()) {
            enemy.draw(g); // Draw each enemy ship
        }

        for (Projectile p : window.getPlayerProjectiles()) {
            p.draw(g); // Draw each player's projectile
        }

        for (Projectile p : window.getEnemyProjectiles()) {
            p.draw(g); // Draw each enemy's projectile
        }

        // Draw score and wave number
        g.setColor(Color.GREEN);
        g.drawString("Score: " + window.getScore(), 10, 20);
        g.drawString("Press ESC to pause", 10, 40);
        g.drawString("Wave: " + window.getWaveNumber(), getWidth() - 100, 20);
        // Top-right boss timer
        if (window.isBossTimerActive()) {
            int remaining = window.getBossTimeRemaining();
            int minutes = remaining / 60;
            int seconds = remaining % 60;
            String time = String.format("%02d:%02d", minutes, seconds);
            g.setColor(Color.WHITE);
            g.drawString("Boss Timer: " + time, getWidth() - 150, 40); // Adjust X position if needed
        }
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        StringBuilder hearts = new StringBuilder("Lives: ");
        for (int i = 0; i < window.getLives(); i++) {
            hearts.append("❤️ "); // Add a heart for each life
            }
        int margin = 20;
        int heartY = getHeight() - 55; // Raise it slightly above the boost bar
        g.drawString(hearts.toString(), margin, heartY);
            
        g.setFont(new Font("Arial", Font.BOLD, 20)); // ⬅️ Bigger and bold font


        // Draw the boost bar
        drawBoostBar(g);

        // Draw settings or pause menus if applicable
        if (window.isSettingsMode()) {
            drawSettingsMenu(g);
        } else if (window.isPaused()) {
            drawPauseMenu(g);
        }
        for (FloatingText ft : window.getFloatingTexts()) {
            ft.draw(g);
        }
        
    }

    // Draws the boost bar at the bottom of the screen
    private void drawBoostBar(Graphics g) {
        int margin = 20;
        int barWidth = 200;
        int barHeight = 12;

        int boost = window.getPlayer().getBoostAmount();
        int maxBoost = window.getPlayer().getMaxBoost();

        g.setColor(Color.YELLOW);
        g.drawString("Boost: " + boost, margin, getHeight() - barHeight - 25);

        g.drawRect(margin, getHeight() - barHeight - 10, barWidth, barHeight);

        // Fill the boost bar to show current boost level
        g.setColor(Color.YELLOW);
        g.fillRect(margin + 1, getHeight() - barHeight - 9,
                (int) ((barWidth - 1) * (boost / (double) maxBoost)),
                barHeight - 1);
    }

    // Draws the pause menu when the game is paused
    private void drawPauseMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black background
        g.fillRect(0, 0, getWidth(), getHeight());

        drawButton(g, window.getContinueButton(), "Continue");
        drawButton(g, window.getSettingsButton(), "Settings");
        drawButton(g, window.getExitButton(), "Exit Game");
    }

    // Draws the settings menu when settings mode is active
    private void drawSettingsMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black background
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw resolution options for the user to select
        for (GameWindow.ResolutionOption option : window.getResolutionOptions()) {
            drawButton(g, option.bounds, option.label);
        }

        drawButton(g, window.getBackButton(), "Back");
    }

    // Helper method to draw a button with text
    private void drawButton(Graphics g, Rectangle bounds, String text) {
        g.setColor(Color.WHITE);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Center the text inside the button
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int textX = bounds.x + (bounds.width - textWidth) / 2;
        int textY = bounds.y + (bounds.height + textHeight) / 2 - 4;

        g.drawString(text, textX, textY);
    }

    // Inner class representing a star for the background
    private static class Star {
        int x, y, size;

        public Star(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }
}

 