package calculate;

import audio.file.WaveFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

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
@SuppressWarnings("ALL")
public class Test {
    
   // private static final String ERR_OUT_PATH = "errOut.txt";
    private static final String STD_OUT_PATH = "stdOut.txt";
    
    public static void main(String[] args){
        try(//PrintStream errOut = new PrintStream(ERR_OUT_PATH);
            PrintStream stdOut = new PrintStream(STD_OUT_PATH);
        ){
            System.setOut(stdOut);
        //    System.setErr(errOut);
            test();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Perform tests
    private static void test() throws IOException {
        
        final String FILE_PATH = "resources/8600Hz.wav";
        
        System.out.println("Test began " + Calendar.getInstance().getTime());
        final int SAMPLE_RATE = 44100;
        final int N = 1024; // FFT size
        int channel = 1;
        
        WaveFile file = new WaveFile(new File(FILE_PATH));
        System.out.println(file);
        
        int[] samples = file.getAllSamples(1);
        System.out.println("Max sample value = " + getMax(samples));
        
        System.out.println("Test finished " + Calendar.getInstance().getTime());
    }
    
    private static int getMax(int[] samples) {
        int MAX = Integer.MIN_VALUE;
        for(int s: samples){
            if (s > MAX) MAX = s;
        }
        return MAX;
    }
    
    public static void printArray(int[] array, String identifier){
        for (int i = 0; i < array.length; i++) {
            System.out.println(identifier+"["+i+"] : " +array[i]);
        }
    }
    
    public static void printArray(short[] array, String identifier){
        for (int i = 0; i < array.length; i++) {
            System.out.println(identifier+"["+i+"] : " +array[i]);
        }
    }
    
    public static void printArray(double[] array, String identifier){
        for (int i = 0; i < array.length; i++) {
            System.out.println(identifier+"["+i+"] : " +array[i]);
        }
    }
    /*
    public static short[] intToShort(int[] array){
        short[] shortArray = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] > Short.MAX_VALUE){
                shortArray[i] = Short.MAX_VALUE;
            }else if(array[i] < Short.MIN_VALUE){
                shortArray[i] = Short.MIN_VALUE;
            }else{
                shortArray[i] = (short) array[i];
            }
        }
        return shortArray;
    }
    */
}
