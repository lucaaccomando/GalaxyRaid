package galaxyraid;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class SoundPlayer {
    private static Clip backgroundClip;
    private static final HashMap<String, Clip> soundCache = new HashMap<>();

    // Play short sound effects
    public static void play(String filename) {
        try {
            URL soundURL = SoundPlayer.class.getResource("/sounds/" + filename);
            if (soundURL == null) {
                System.err.println("Sound file not found: " + filename);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Start looping background music
    public static void playLoop(String filename) {
        try {
            URL soundURL = SoundPlayer.class.getResource("/sounds/" + filename);
            if (soundURL == null) {
                System.err.println("Background music not found: " + filename);
                return;
            }

            if (backgroundClip != null && backgroundClip.isRunning()) {
                backgroundClip.stop();
                backgroundClip.close();
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioIn);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Stop background music
    public static void stopLoop() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }
}
