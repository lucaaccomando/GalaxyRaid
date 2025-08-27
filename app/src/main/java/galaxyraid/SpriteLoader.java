package galaxyraid;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteLoader {
    public static BufferedImage load(String filename) {
        try {
            return ImageIO.read(SpriteLoader.class.getResource("/sprites/" + filename));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
    