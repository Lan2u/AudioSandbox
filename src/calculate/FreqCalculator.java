package calculate;

import org.jetbrains.annotations.Contract;
import org.jtransforms.fft.DoubleFFT_1D;

/**
 * Created by Paul Lancaster on 14/12/2016
 */
/*
http://stackoverflow.com/questions/3058236/how-to-extract-frequency-information-from-samples-from-portaudio-using-fftw-in-c

 */
    
/*
    Same as old freq calculator except doesn't store the real or imaginary parts of the FFT and just the magnitude
 */

public abstract class FreqCalculator {
    /**
     * Perform FFT on the chunk of amplitudes passed in and return an array of the FFT output (magnitude)
     * Magnitude = sqrt(real * real + im * im)
     *
     * @param chunk The input chunk of amplitudes
     */
    private static double[] performFFT(int[] chunk) {
        double[] decimalChunk = intToDoubleNoScale(chunk);
        
        // Build and apply window
        // buildHannWindow(chunk.length);
        // decimalChunk = applyWindow(decimalChunk,decimalChunk);
        
        new DoubleFFT_1D(chunk.length).realForward(decimalChunk);
        return getMagnitudes(decimalChunk);
    }
    
    /**
     * Return the magnitude of output of the FFT
     * (with the output being passed in as a decimal chunk of the structure below)
     * If the chunk is even length the real values are at even index's (and 0) with last value located at 0
     * If the chunk is odd length then the real values are
     *
     * @param decimalChunk The output from the FFT
     * @return An array representing the magnitude output of the FFT
     */
    private static double[] getMagnitudes(double[] decimalChunk) {
        int N = decimalChunk.length; // Used to improve readability
        double[] real;
        double[] im;
        
        if (isEven(N)) { // TODO condense this to minimize array accesses
            real = new double[N / 2];
            im = new double[N / 2];
            for (int k = 0; k < (N / 2); k++) { // Real
                real[k] = decimalChunk[2 * k];
            }
            for (int k = 1; k < N / 2; k++) { // Imaginary
                im[k] = decimalChunk[2 * k + 1];
            }
            real[N / 2 - 1] = decimalChunk[1];
        } else { // Odd
            real = new double[(N + 1) / 2];
            im = new double[(N - 1) / 2 + 1];
            for (int k = 0; k < (N + 1) / 2; k++) { // Real
                real[k] = decimalChunk[2 * k];
            }
            for (int k = 0; k < (N - 1) / 2; k++) { // Imaginary
                im[k] = decimalChunk[2 * k + 1];
            }
            im[(N - 1) / 2] = decimalChunk[1];
        }
        return getMagnitudeArray(real, im);
    }
    
    /**
     * Get the magnitude of 2 arrays
     * Magnitude[k] = sqrt(array1[k]^2 + array2[k]^2)
     *
     * @param array1 The first array must have same length as array 2
     * @param array2 The second array must have same length as array 1
     * @return The magnitude values of the 2 arrays
     */
    private static double[] getMagnitudeArray(double[] array1, double[] array2) {
        if (array1.length != array2.length) {
            throw new IllegalArgumentException(
                    "Length of array 1(" + array1.length + ") isn't equal to length of array 2(" + array2.length + ")");
        }
        double[] mag = new double[array1.length];
        for (int i = 0; i < mag.length; i++) {
            mag[i] = Math.sqrt(array1[i] * array1[i] + array2[i] * array2[i]);
        }
        return mag;
    }
    
    private static boolean isEven(int n) {
        return n % 2 == 0;
    }
    
    // Chunk should be of length N (FFT SIZE)
    public static int getPrimaryFreqOfChunk(int[] chunk, int sampleRate) {
        double[] magnitude = performFFT(chunk);
        return calcPrimaryFreq(magnitude, sampleRate);
    }
    
    /**
     * Calculate the primary frequency of a FFT magnitude output
     *
     * @param magnitude  The FFT magnitude output
     * @param sampleRate The sampleRate of the audio file that was used with the FFT
     * @return The primary frequency rounded to the nearest integer
     */
    private static int calcPrimaryFreq(double[] magnitude, int sampleRate) {
        int index = getMaxIndex(magnitude);
        double binSize = sampleRate / magnitude.length;
        return (int) Math.round(index * binSize);
    }
    
    public static double nanoToSeconds(long nano) {
        return (nano / 1000000000.0);
    }
    
    private static int getMaxIndex(double[] array) {
        double max = array[0];
        int maxI = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
                maxI = i;
            }
        }
        return maxI;
    }
    
    /**
     * Turn an int array into a double array maintaining the int values
     *
     * @param array The int array to convert
     * @return The double conversion
     */
    @Contract(pure = true)
    private static double[] intToDoubleNoScale(int[] array) {
        double[] temp = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            temp[i] = array[i];
        }
        return temp;
    }
    
    public static double[] log10Array(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.log10(array[i]);
        }
        return array;
    }
    
    public static double[] getMagPowerSpectrum(int[] chunk) {
        return performFFT(chunk);
    }
    
    public static long secondsToNano(double seconds) {
        return Math.round(seconds * 1000000000.0);
    }
}