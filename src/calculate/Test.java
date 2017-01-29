package calculate;

import audio.file.WaveFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

/**
 * Created by Paul Lancaster on 14/12/2016
 */

public class Test {
    private static final String STD_OUT_PATH = "stdOut.txt";
    
    public static void main(String[] args){
        try(
            PrintStream stdOut = new PrintStream(STD_OUT_PATH)
        ){
            System.setOut(stdOut);
            test();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Perform tests
    private static void test() throws IOException {
        System.out.println("Test began " + Calendar.getInstance().getTime());
        
        final String FILE_PATH = "resources" + File.pathSeparator + "440Hz.wav";
        final int SAMPLE_RATE = 44100;
        final int N = 1024; // FFT size
        int channel = 1;
        
        WaveFile file = new WaveFile(new File(FILE_PATH));
        System.out.println(file);
        
        //int[] samples = file.getAllSamples(channel);
        file.resetPos();
        int[] chunk = file.getChunk(N, channel);
        
        System.out.println("Calculated primary frequency of chunk: " + FreqCalculator.getPrimaryFreqOfChunk(chunk, SAMPLE_RATE) + "Hz");
        System.out.println("Test finished " + Calendar.getInstance().getTime());
    }
}
