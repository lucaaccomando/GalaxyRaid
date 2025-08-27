package galaxyraid;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class BossEnemy {
    private int x, y;
    private int width = 100, height = 60;
    private int health = 50;
    private int dx = 5;

    private BufferedImage sprite;
    private GameWindow window;

    private long lastShotTime = 0;
    private long shotCooldown = 4000; // 4 seconds

    public BossEnemy(int x, int y, BufferedImage sprite, GameWindow window) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.window = window;
    }

    public void update(Player player) {
        // Horizontal movement
        x += dx;
        if (x < 0 || x + width > window.getWidth()) {
            dx *= -1;
        }
    
        // Shooting logic
        long now = System.currentTimeMillis();
        if (now - lastShotTime > shotCooldown) {
            window.getBossProjectiles().add(new BossProjectile(x + width / 2, y + height, window));
            lastShotTime = now;
        }
    }
    public int getY() {
        return y;
    }
    

    public void draw(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            g.setColor(java.awt.Color.MAGENTA);
            g.fillRect(x, y, width, height);
        }

        // Health bar
        g.setColor(java.awt.Color.RED);
        g.fillRect(x, y - 10, width, 6);
        g.setColor(java.awt.Color.GREEN);
        g.fillRect(x, y - 10, (int) (width * (health / 50.0)), 6);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public boolean isDefeated() {
        return health <= 0;
    }
}
