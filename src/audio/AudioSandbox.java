package audio;

import audio.transforms.AudioDataTransformation;
import audio.files.WaveFile;
import graphic.display.UserInterface;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Paul Lancaster on 30/10/2016
 */

    
public class AudioSandbox {
    private static final String FILE_PATH = "resources/audiocheck.net_sweep_10Hz_20000Hz_-3dBFS_4s.wav";

    public static void main(String[] args) throws IOException {
       // PrintStream out = new PrintStream("log.txt");
       // System.setOut(out);
        WaveFile waveFile = new WaveFile(new File("resources/audiocheck.net_sweep_10Hz_20000Hz_-3dBFS_4s.wav"));
        double[] freq = AudioDataTransformation.getFrequencies(waveFile, 1024);
        
        System.out.println(System.nanoTime());
    
        UserInterface gui = new UserInterface(1000,800,waveFile);
        gui.startDisplaying();
        
    //    out.close();
    }
}