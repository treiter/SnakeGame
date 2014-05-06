import java.net.URL;
import javax.sound.sampled.*;
public enum SoundEffect
{
    EAT("burp.wav"),
    CRASH("bomb.wav"),
    MUSIC("GameMusic.wav");

    private Clip clip;

    SoundEffect(String soundFileName) {
        try {
            URL url = this.getClass().getClassLoader().getResource(soundFileName);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        //if(clip.isRunning()) {
        //    clip.stop();
        //}
        //FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        //volume.setValue(volume.getMaximum());
        clip.setFramePosition(0);
        clip.start();
    }

    public void loop() {
        if(clip.isRunning()) {
            clip.stop();
        }
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(-volume.getMaximum());
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void init() {
        values();
    }
}
