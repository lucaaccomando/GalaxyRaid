package galaxyraid;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class FloatingText {
    private int x, y;
    private int lifetime = 30; // How long it stays on screen (frames)
    private String text;
    private Color color;

    public FloatingText(String text, int x, int y, Color color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void update() {
        y -= 1;         // Float upward
        lifetime -= 1;  // Reduce lifetime
    }

    public boolean isExpired() {
        return lifetime <= 0;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(text, x, y);
    }
}
