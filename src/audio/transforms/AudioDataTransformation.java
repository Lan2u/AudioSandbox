package audio.transforms;

import audio.files.WaveFile;
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
    
    public static double[] getFrequencies(WaveFile waveFile, int CHUNK_SIZE){
        double numberOfChunks = Math.ceil(waveFile.getNumberOfSamples()/CHUNK_SIZE);
        double[] frequencies = new double[waveFile.getNumberOfSamples()/CHUNK_SIZE];
        for (int chunkCount = 0; chunkCount < numberOfChunks; chunkCount++) {
            // Get a chunk of samples and record the max amplitude of the chunk
            double[] chunkSamples = new double[CHUNK_SIZE];
            double max = Integer.MIN_VALUE;
            for (int i = 0; i < CHUNK_SIZE; i++) {
                chunkSamples[i] = waveFile.getSample();
                if (chunkSamples[i] > max) {
                    max = chunkSamples[i];
                }
            }
        
            // Turn the samples into floats ranging between -1 and 1 with 1 being the highest value and -1 the lowest (most negative)
            double[] floatSamples = getRatioOfMax(chunkSamples, max);
        
            frequencies[chunkCount] = AudioDataTransformation.getFrequencyOfChunk(floatSamples, waveFile.getSampleRate());
        }
        return frequencies;
    }
    
    /**
     * @param samples The array of samples to get the ratio to the max of
     * @param max The max value
     * @return A double array which contains the ratios of each of the samples to the max value
     * (so max value = 1.0, -max value = -1 and half max value = 0.5)
     */
    private static double[] getRatioOfMax(double[] samples, double max) {
        double[] floatSamples = new double[samples.length];
        for (int i = 0; i < floatSamples.length; i++) {
            floatSamples[i]  = (((double)samples[i])/((double)max));
        }
        return floatSamples;
    }
    
    private static double[] getAmplitudeAverage(double[] samples, int numberOfChannels) {
        if (numberOfChannels > 1) {
            double[][] channels = splitChannels(samples, numberOfChannels);
            double[] channel1 = new double[samples.length/2];
            double[] channel2 = new double[samples.length/2];
            for (int i = 0; i < channels[0].length; i++) {
                channel1[i]  = channels[0][i];
                channel2[i] = channels[1][i];
            }
            return averageChannels(channel1,channel2);
        }else{
            return samples;
        }
    }
    
    /**
     * @param samples The array of audio samples to be separated
     * @param channels The number of channels
     */
    private static double[][] splitChannels(double[] samples, int channels) {
        double[][] channelData = new double[channels][];
        for (int k = 0; k < (samples.length/channels); k++){
            channelData[0][k] = samples[k*2];
            channelData[1][k] = samples[k*2 + 1];
        }
        return channelData;
    }
    
    /**
     * @param channel1 First data channel
     * @param channel2 Second data channel
     * @return The average of each channel as a double array (output[n] = (channel1[n] + channel2[n]) / 2
     */
    private static double[] averageChannels(double[] channel1, double[] channel2){
        if (channel1.length == 0){
            return channel2; // The first channel is empty so the average is just the second channel
        } else if (channel2.length == 0){
            return channel1; // The second channel is empty so the average is just the first channel
        }
        System.out.println(" Channel 1 length " + channel1.length + " channel 2 length " + channel2.length);
        double[] averagedChannels = new double[channel1.length];
        for (int i = 0; i < channel1.length; i++) {
            averagedChannels[i] = (channel1[i] + channel2[i])/2.0;
        }
        return averagedChannels;
    }
    
    private static float[] getFloats(int[] samples) {
        float[] floats = new float[samples.length];
        for (int i = 0; i < floats.length; i++) floats[i] = samples[i];
        return floats;
    }
    
    private static double[] getDoubles(int[] samples) {
        double[] doubles = new double[samples.length];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = samples[i];
        }
        return doubles;
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
