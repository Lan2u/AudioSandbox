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
            PrintStream stdOut = new PrintStream(STD_OUT_PATH);
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
        
        final String FILE_PATH = "resources/440Hz.wav";
        final int SAMPLE_RATE = 44100;
        final int N = 1024; // FFT size
        int channel = 1;
        
        WaveFile file = new WaveFile(new File(FILE_PATH));
        System.out.println(file);
        
        int[] samples = file.getAllSamples(1);
        file.resetPos();
        int[] chunk = file.getChunk(N, 1);
        
        System.out.println("Calculated primary frequency of chunk: " + oldFreqCalculator.getPrimaryFreqOfChunk(chunk, SAMPLE_RATE) + "Hz");
        System.out.println("Test finished " + Calendar.getInstance().getTime());
    }
    
    private static int getMin(int[] samples){
        int MIN = Integer.MAX_VALUE;
        for(int s: samples){
            if (s < MIN) MIN = s;
        }
        return MIN;
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
}
