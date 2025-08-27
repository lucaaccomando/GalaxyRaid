package galaxyraid;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Safely run the game window on the Swing event thread
        SwingUtilities.invokeLater(() -> new GameWindow());
    }
}
