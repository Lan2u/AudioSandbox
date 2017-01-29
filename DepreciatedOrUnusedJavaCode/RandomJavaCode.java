package RandomJavaCode;

import audio.files.AudioFile;
import calculate.FreqCalculator;

import java.awt.*;
import java.util.*;

/**
 * Created by Paul Lancaster on 13/12/2016
 */
public class RandomJavaCode {
    private int[] scaleArray(int[] array, int limit){
        // Get max frequency
        int maxVal = Integer.MIN_VALUE;
        for (int n : array) {
            if (n > maxVal) {
                maxVal = n;
            }
        }
        // Bring all values within range of the limit
        int[] yValues = new int[array.length];
        
        for (int i = 0; i < yValues.length; i++) {
            yValues[i] = HEIGHT - 1 - (int) Math.round((array[i] / ((double) maxVal)) * (HEIGHT));
        }
        
        return yValues;
    }
    
    private void averageOutArray(int[] array) {
        int AVERAGE_PASSES = 5; // Average out waveform this many times
        for (int run = 0; run < AVERAGE_PASSES; run++) {
            for (int k = 0; k < AVERAGE_PASSES; k++) {
                int lastFreq = array[0];
                for (int i = 1; i < array.length; i++) {
                    array[i] = (int) ((lastFreq + array[i]) / 2.0);
                    lastFreq = array[i];
                }
            }
        }
    }
    
    private int[] getFrequencies(AudioFile file, int CHUNK_SIZE /* Size of each chunk in samples */) {
        double NUMBER_OF_CHUNKS = Math.ceil(file.getNumberOfSamples() / CHUNK_SIZE);
        
        int[] frequencies = new int[(int) NUMBER_OF_CHUNKS];
        
        int CHANNEL = 1;
        
        for (int i = 0; i < frequencies.length; i++){
            short[] chunk = file.getChunk(CHUNK_SIZE,CHANNEL);
            frequencies[i] = (int) Math.round(getFreqOfChunk(chunk, file.getSampleRate()));
            if (frequencies[i] < 0) frequencies[i] = Math.abs(frequencies[i]); // positive negative values
            // TODO understand why we get negative values and how to handle them
        }
        return frequencies;
    }
    
    @Deprecated
    private void calcDiameter(int[] chunk, int sampleRate) {
        int freq = FreqCalculator.getPrimaryFreqOfChunk(chunk,sampleRate);
        int freqLog10 = (int) Math.round( Math.log10(freq) );
    }
    
    public void collectionsExamples(){
        ArrayList<Integer> arrayList;
        List simpleList = new List();
        Vector<Integer> vector; // Old array from early java
        LinkedList linkedList;
        
        Set<Integer> mySet = new TreeSet<>();
        
        Map<String,Integer> myMap = new HashMap<>();
        myMap.put("Key",20);
    }
    
    private static int getMinValue(int[] array){
        int MIN = Integer.MAX_VALUE;
        for(int n: array){
            if (n < MIN) MIN = n;
        }
        return MIN;
    }
    
    private static int getMaxValue(int[] array) {
        int MAX = Integer.MIN_VALUE;
        for(int n: array){
            if (n > MAX) MAX = n;
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
