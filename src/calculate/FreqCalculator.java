package calculate;

import org.jtransforms.fft.DoubleFFT_1D;

/**
 * Created by Paul Lancaster on 14/12/2016
 */
/*
http://stackoverflow.com/questions/3058236/how-to-extract-frequency-information-from-samples-from-portaudio-using-fftw-in-c

 */



//TODO test this, this algorithm isn't working correctly

public abstract class FreqCalculator {
    
    // Chunk should be of length N (FFT SIZE)
    public static double getFreqOfChunk(short[] chunk, int sampleRate){
        double[] decimalChunk = shortToDouble(chunk);
        
        // Build and apply window
        buildHannWindow(chunk.length);
        decimalChunk = applyWindow(decimalChunk,decimalChunk);
    
        DoubleFFT_1D dFF = new DoubleFFT_1D(chunk.length);
        dFF.realForward(decimalChunk);
        
        int N = decimalChunk.length;
        
        final int ARRAY_LENGTH = N/2 + 1;
        
        double[] real = new double[ARRAY_LENGTH];
        double[] im = new double[ARRAY_LENGTH];
        
        if (decimalChunk.length % 2 == 0){ // Even
            for (int k = 0; k < (N/2); k++) { // Real
                real[k] = decimalChunk[2*k];
            }
            for (int k = 1; k < N/2; k++) { // Imaginary
                im[k] = decimalChunk[2*k + 1];
            }
            real[N/2] = decimalChunk[1];
            
        }else{ // Odd
            for (int k = 0; k < (N+1)/2; k++) { // Real
                real[k] = decimalChunk[2*k];
            }
            for (int k = 0; k < (N-1)/2; k++) { // Imaginary
                im[k] = decimalChunk[2*k +1];
            }
            im[(N-1)/2] = decimalChunk[1];
        }
    
        double[] magnitude = new double[ARRAY_LENGTH];
        for (int i = 0; i < ARRAY_LENGTH; i++) {
            magnitude[i] = Math.sqrt(real[i] * real[i] + im[i] * im[i]);
        }
    
        for (int i = 0; i < ARRAY_LENGTH; i++) {
            System.out.println(real[i] + " + " + im[i] +"i - Magnitude : " + magnitude[i]);
        }
        
        return 0.0;
    }
    
    private static double[] shortToDouble(short[] array) {
        double[] temp = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0){
                temp[i] = 0;
            } else {
                temp[i] = (Short.MAX_VALUE*2 / array[i]);
            }
            
            if(temp[i] > 1) System.out.println("ERROR DOUBLE VALUE GREATER THAN 1 : " + temp[i]); // Means that the amplitude was out of range
        }
        return temp;
    }
    
    /* Old classes used (other implementation used before) */
    private static final int MIN_DATA_CHUNK_SIZE = 2;
    
    private static void PerformFFT(ComplexNumber[] data, int N){
        
    }
    
    public static ComplexNumber DDT(short[] data, int sampleRate){
        // https://en.wikipedia.org/wiki/Discrete_Fourier_transform
        // http://www.librow.com/articles/article-10
        int N = data.length;
        double totalSum = 0.0;
        for (int n = 0; n <= N-1; n++){
            int xn = data[n];
            for (int k =0; k <= sampleRate/2; k++){
                // k = frequency being considered
                // See the wikipedia page
            }
        }
        return null;
    }
    
    //TODO testing I don't think this works properly
    private static short[] averageFilterSamples(short[] chunkSamples) {
        // Low-Pass filtering : Average out the frequency this will smooth out large peaks or troughs in the data
        // http://blog.bjornroche.com/2012/07/frequency-detection-using-fft-aka-pitch.html
        
        short[] filterOutput = new short[chunkSamples.length];
        short lastSample = chunkSamples[0];
        filterOutput[0] = chunkSamples[0];
        for (int i = 1; i < chunkSamples.length; i++) {
            filterOutput[i] = (short)((chunkSamples[i] + lastSample)/2);
            lastSample = filterOutput[i];
        }
        return filterOutput;
    }
    
    //TODO testing
    private static double[] buildHannWindow(int size) {
        double[] window = new double[size];
        for (int i = 0; i <size; i++) {
            window[i] = 0.5 * (1 - Math.cos(2 * Math.PI * i/ (size - 1.0)));
        }
        return window;
    }

    //TODO testing
    private static double[] applyWindow(double[] window, double[] samples) {
        for (int i = 0; i < samples.length; i++) {
            samples[i] = Math.round(samples[i] * window[i]);
        }
        return samples;
    }
    
}
