package galaxyraid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class EnemyShip {
    private int x, y; // Position of the enemy ship
    private BufferedImage sprite;

    private double speed; // Speed at which the enemy ship moves

    private boolean exploding = false; // Whether the ship is in an explosion state
    private int explosionFrame = 0; // Tracks the current frame of the explosion animation

    private static final int WIDTH = 32;
    private static final int HEIGHT = 40;

    private static final int MAX_EXPLOSION_FRAMES = 20; // Total frames for the explosion animation

    /**
     * Constructor to initialize the enemy ship with position and speed
     * 
     * @param x Initial X position of the enemy ship
     * @param y Initial Y position of the enemy ship
     * @param speed Speed at which the enemy ship moves
     */
    public EnemyShip(int x, int y, double speed, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.sprite = sprite;
    }
    

    // Called every frame to update the enemy ship's behavior
    // Returns true when the enemy ship's explosion is complete and can be removed
    public boolean update(GameWindow window) {
        if (!exploding) {
            return exploding && ++explosionFrame > MAX_EXPLOSION_FRAMES;

        } else {
            explosionFrame++; // Increase explosion frame
            return explosionFrame > MAX_EXPLOSION_FRAMES; // Return true if the explosion is over
        }
    }

    // Draws the enemy ship or explosion animation on the screen
    public void draw(Graphics g) {
        if (exploding) {
            // (keep explosion logic as is)
        } else {
            if (!exploding && sprite != null) {
                g.drawImage(sprite, x, y, WIDTH, HEIGHT, null);
            }
            
        }
        
    }

    // Move the enemy ship by a given amount in both x and y directions
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    // Explodes the enemy ship, triggering the explosion animation
    public void explode() {
        exploding = true;
        explosionFrame = 0; // Reset explosion frame to start the animation
    }

    // Returns true if the enemy ship is currently exploding
    public boolean isExploding() {
        return exploding;
    }

    // Returns the bounding box of the enemy ship for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
    
    // Getters for the x and y position of the enemy ship
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Used by GameWindow to detect when the enemy ship reaches the edge of the screen
    public static int getWidthValue() {
        return WIDTH;
    }
}
