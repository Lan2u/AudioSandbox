package RandomJavaCode;

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
    
    
}
