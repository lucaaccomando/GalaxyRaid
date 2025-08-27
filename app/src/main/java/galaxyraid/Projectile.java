package galaxyraid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Projectile {
    protected  int x, y; // X and Y position of the projectile
    private int speed; // Speed of the projectile

    protected  static final int WIDTH = 4;  // Width of the projectile
    protected  static final int HEIGHT = 10; // Height of the projectile

    /**
     * Constructor to initialize the projectile.
     * 
     * @param x Initial X coordinate (centered at tip of player/enemy)
     * @param y Initial Y coordinate
     * @param direction -1 for upward (player), 1 for downward (enemy)
     */
    public Projectile(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.speed = 10 * direction; // Direction encoded via sign
    }

    // Updates the position of the projectile based on its speed
    public void update() {
        y += speed;
    }

    // Draws the projectile on the screen
    public void draw(Graphics g) {
        // Player = red, enemy = orange
        g.setColor(Color.WHITE);
        g.fillRect(x, y, WIDTH, HEIGHT); // Draw the rectangle representing the projectile
    }

    // Returns the bounding box of the projectile for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    // Getters for position (used for collision detection, etc.)
    public int getX() { return x; }
    public int getY() { return y; }
}
