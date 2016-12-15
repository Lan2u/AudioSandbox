package calculate;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Paul Lancaster on 14/12/2016
 */

/* Calculate package tests
    - Calculate frequency when
        - 0 amplitude (Failed 15/12/2016)
        - All one amplitude
        - Clean sin signal
        - Real signal
 */
public class Test {
    private static final String FILE_PATH = "resources/440Hz.wav";
    private static final String ERR_OUT_PATH = "errOut.txt";
    private static final String STD_OUT_PATH = "stdOut.txt";
    
    public static void main(String[] args){
        try(PrintStream errOut = new PrintStream(ERR_OUT_PATH);
            PrintStream stdOut = new PrintStream(STD_OUT_PATH);
        ){
            System.setOut(stdOut);
            System.setErr(errOut);
            test();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Perform tests
    private static void test() throws IOException {
        final int SAMPLE_RATE = 44100;
        
        short[] chunk = {0,0,0,0,0,0,0,0,0,0,0};
        printArray(chunk, "chunk");
        System.out.println(FreqCalculator.getFreqOfChunk(chunk,SAMPLE_RATE) + "Hz");
        
        
    }
    private static void printArray(short[] array, String identifier){
        for (int i = 0; i < array.length; i++) {
            System.out.println(identifier+"["+i+"] : " +array[i]);
        }
    }
}
