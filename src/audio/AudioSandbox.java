package audio;

import audio.files.WaveFile;
import display.AudioDisplay;
import display.VisualEffect;

import java.io.File;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 30/10/2016
 */

    
public class AudioSandbox {
    private static final String FILE_PATH = "resources/audiocheck.net_sweep_10Hz_20000Hz_-3dBFS_4s.wav";

    public static void main(String[] args) throws IOException {
        WaveFile waveFile = new WaveFile(new File(FILE_PATH));
        
        System.out.println(waveFile);
        AudioDisplay display = new AudioDisplay();
        display.play(waveFile, VisualEffect.Frequency_Distribution);
    }
}