package galaxyraid;

import java.awt.Color;
import java.awt.Graphics;

public class SplinterProjectile extends Projectile {
    private int dx, dy;

    public SplinterProjectile(int x, int y, int dx, int dy) {
        super(x, y, 0);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void update() {
        x += dx;
        y += dy;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, 4, 8);
    }
}
