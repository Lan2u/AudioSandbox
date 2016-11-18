package audio.transforms;

import org.jtransforms.fft.DoubleFFT_1D;


/**
 * Created by Paul Lancaster on 15/11/2016
 * This class is for doing any calculations on audio data
 */

public class AudioDataTransformation {
    
    public Boolean[] posNegativeModulation(int[] dataPoints){
        Boolean[] positive = new Boolean[dataPoints.length];
        for (int i = 0; i < dataPoints.length; i++) {
            if (dataPoints[i] > 0){
                positive[i] = true;
            }else if (dataPoints[i] == 0){
                positive[i] = null;
            } else{
                positive[i] = false;
            }
        }
        return positive;
    }
    
    public static void modulate(int[] yValues, int value) {
        for (int i = 0; i < yValues.length; i++) {
            if (yValues[i] > 0){
                yValues[i] = value;
            }else if (yValues[i] == 0){
                yValues[i] = 0;
            } else{
                yValues[i] = -value;
            }
        }
    }
    
    private static double[] applyWindow(double[] window, double[] floatSamples, int size) {
        for (int i = 0; i < floatSamples.length; i++) {
            floatSamples[i] = floatSamples[i] * window[i];
        }
        return floatSamples;
    }
    
    private static double[] buildHannWindow(int size) {
        double[] window = new double[size];
        for (int i = 0; i <size; i++) {
            window[i] = 0.5 * (1 - Math.cos(2 * Math.PI * i/ (size - 1.0)));
        }
        return window;
    }
    
    public static double getFrequencyOfChunk(double[] sampleChunk, int sampleRate) {
        // Average out the samples in the chunk
        sampleChunk = averageFilterSamples(sampleChunk);
        
        // Apply a Hann window to the chunk to reduce sidelobes
        double[] window = buildHannWindow(sampleChunk.length);
        sampleChunk = applyWindow(window, sampleChunk, sampleChunk.length);
        
        //binSize = sampleRate/N so as bin size increases accuracy of the frequency increases (accurate to a smaller value)
        
        // Perform fft
        double[] fftOut = new double[sampleChunk.length * 2];
        System.arraycopy(sampleChunk,0,fftOut,0,sampleChunk.length);
        DoubleFFT_1D fft = new DoubleFFT_1D(sampleChunk.length);
        fft.realForwardFull(fftOut);
        
        double[] real = new double[sampleChunk.length];
        double[] imaginary = new double[sampleChunk.length];
        
        for (int k = 0; k < (fftOut.length/2); k++) {
            real[k] = fftOut[k*2];
            imaginary[k] = fftOut[k*2 + 1];
        }
        
        double[] magnitude = new double[sampleChunk.length/2];
        for (int i = 0; i < (sampleChunk.length / 2.0); i++) {
            magnitude[i] = Math.sqrt(Math.pow(real[i], 2) + Math.pow(imaginary[i], 2));
        }
        
        int maxMagnitudeIndex = -1;
        double maxMagnitude = Double.MIN_VALUE;
        for (int i = 0; i < magnitude.length; i++) {
            if (magnitude[i] > maxMagnitude){
                maxMagnitude = magnitude[i];
                maxMagnitudeIndex = i;
            }
        }
        
        double binSize = (sampleRate/sampleChunk.length);
        
        return maxMagnitudeIndex * binSize;
    }
    
    // Low-Pass filtering : Average out the frequency this will smooth out large peaks or troughs in the data
    // http://blog.bjornroche.com/2012/07/frequency-detection-using-fft-aka-pitch.html
    private static double[] averageFilterSamples(double[] floatSamples) {
        double[] filterOutput = new double[floatSamples.length];
        lastSample = floatSamples[0];
        filterOutput[0] = lastSample;
        for (int i = 1; i < floatSamples.length; i++) {
            filterOutput[i] = stepAverageFilter(floatSamples[i]);
        }
        return filterOutput;
    }
    private static double lastSample;
    private static double stepAverageFilter(double sample){
        double output = ((sample + lastSample)/2.0);
        lastSample = output;
        return output;
    }
    
    
    /* Webpages used
    http://blog.bjornroche.com/2012/07/frequency-detection-using-fft-aka-pitch.html
    http://stackoverflow.com/questions/2704139/how-to-get-audio-frequency-data-from-a-wave-file
    https://en.wikipedia.org/wiki/Digital_signal_processing
    https://en.wikipedia.org/wiki/Fast_Fourier_transform
    https://en.wikipedia.org/wiki/Quantization_(signal_processing)
    https://en.wikipedia.org/wiki/Sampling_(signal_processing)
     */
    
    
}
