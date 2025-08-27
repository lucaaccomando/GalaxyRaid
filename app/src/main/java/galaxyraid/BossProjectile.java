package galaxyraid;

import java.awt.Color;
import java.awt.Graphics;

public class BossProjectile {
    private int x, y;
    private int width = 12, height = 24;
    private int speed = 10;
    private boolean exploded = false;
    private GameWindow window;

    public BossProjectile(int x, int y, GameWindow window) {
        this.x = x;
        this.y = y;
        this.window = window;
    }
    
    

    public void update(Player player) {
        if (!exploded) {
            y += speed;

            // Correct explosion logic: halfway between boss and player on Y axis
            int halfwayY = (player.getY() + window.getBoss().getY()) / 2;
            if (y >= halfwayY) {
                explodeToward(player);
                exploded = true;
            }
        }
    }

    public void draw(Graphics g) {
        if (!exploded) {
            g.setColor(Color.ORANGE);
            g.fillOval(x, y, width, height);
        }
    }
    

    private void explodeToward(Player player) {
        int px = player.getX();
        int py = player.getY();

        for (int i = -2; i <= 2; i++) {
            int dx = i * 2;
            int dy = 5;
            window.getEnemyProjectiles().add(new SplinterProjectile(x, y, dx, dy));
        }

        SoundPlayer.play("invaderkilled.wav");
    }

    public boolean isExploded() {
        return exploded;
    }
}
