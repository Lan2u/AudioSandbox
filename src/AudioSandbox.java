import audio.files.WaveFile;
import audio.files.loaded.VisualEffect;
import display.AudioDisplay;

import java.io.File;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 30/10/2016
 */

    
public class AudioSandbox {
    private static final String FILE_PATH = "resources/high_risk.wav";

    public static void main(String[] args) throws IOException {
        WaveFile waveFile = new WaveFile(new File(FILE_PATH));
        
        System.out.println(waveFile);
        AudioDisplay display = new AudioDisplay();
        display.queueFile(waveFile, VisualEffect.Frequency_Distribution);
        display.play();
    }
}