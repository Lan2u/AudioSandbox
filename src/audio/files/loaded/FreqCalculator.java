package audio.files.loaded;

import org.jtransforms.fft.DoubleFFT_1D;

/**
 * Created by Paul Lancaster on 14/12/2016
 */
public abstract class FreqCalculator {
    // Get the frequency of a chunk
    public static double getFreqOfChunk(short[] chunk, int sampleRate) {
        // Average out the samples in the chunk
        //   chunk = averageFilterSamples(chunk); // TUrned off because the frequency data was more spread out and spiky not less
        
        // Apply a Hann window to the chunk to reduce sidelobes
        double[] window = buildHannWindow(chunk.length);
        chunk = applyWindow(window, chunk);
        
        // binSize = sampleRate/N so as bin size increases accuracy of the frequency
        // increases (accurate to a smaller value)
        
        // Perform fft
        short[] fftOut = new short[chunk.length * 2];
        System.arraycopy(chunk, 0, fftOut, 0, chunk.length);
        DoubleFFT_1D fft = new DoubleFFT_1D(fftOut.length / 2); // FIXME check that the right length is being passed here and that it isn't too big
        fft.realForwardFull(shortToDouble(fftOut));
        
        double[] real = new double[chunk.length];
        double[] imaginary = new double[chunk.length];
        
        for (int k = 0; k < chunk.length; k++) {
            real[k] = fftOut[k * 2];
            imaginary[k] = fftOut[k * 2 + 1];
        }
        
        // THIS IS NOT AN ARRAY OF FREQUENCIES IT IS AN ARRAY OF MAGNITUDES
        double[] magnitude = new double[chunk.length]; // FIXME array length might be able to be halfed, testing needed
        for (int i = 0; i < magnitude.length; i++) {
            magnitude[i] = Math.sqrt(Math.pow(real[i], 2) + Math.pow(imaginary[i], 2));
        }
        
        int maxMagnitudeIndex = -1;
        double maxMagnitude = Double.MIN_VALUE;
        for (int i = 0; i < magnitude.length; i++) {
            if (magnitude[i] > maxMagnitude) {
                maxMagnitude = magnitude[i];
                maxMagnitudeIndex = i;
            }
        }
        double binSize = (sampleRate / chunk.length);
        return Math.abs(maxMagnitudeIndex * binSize);
    }
    
    
    private static double[] shortToDouble(short[] array) {
        double[] temp = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            temp[i] = array[i];
        }
        return temp;
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
    private static short[] applyWindow(double[] window, short[] samples) {
        for (int i = 0; i < samples.length; i++) {
            samples[i] = (short)Math.round(samples[i] * window[i]);
        }
        return samples;
    }
    
}
