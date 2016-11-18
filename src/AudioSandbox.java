import org.jtransforms.fft.DoubleFFT_1D;
import sound.files.WaveFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Paul Lancaster on 30/10/2016
 */

    
public class AudioSandbox {
    private static final String FILE_PATH = "resources/audiocheck.net_sin_10000Hz_-3dBFS_10s.wav";

    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream("log.txt");
        System.setOut(out);
        
        WaveFile waveFile = new WaveFile(new File(FILE_PATH));
        System.out.println(waveFile.toString());
        
        // FIXME, doesn't display the images at the right size
        // Theses numbers refer to the value of WIDTH below
        // Runs fine with width >= 600
        // Didn't display correctly with width = 500
        // Reran at 500 and worked
        // Ran at 400 fine
        // Re-ran at 400, didn't display correctly
        // Ran again at 400 displayed correctly
        
        int WIDTH = 1000;
        int HEIGHT = 800;
        int CHUNK_SIZE = 1024;
        //int SAMPLES_LENGTH = 1024;
        int SAMPLE_RATE = waveFile.getSampleRate();
        
        // Get a chunk of samples
        double[] samples = new double[CHUNK_SIZE];
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < CHUNK_SIZE; i++) {
            samples[i] = waveFile.getSample();
            if (samples[i] > max){
                max = samples[i];
            }
        }
        
       /*
        int[] samples = waveFile.getAllSamples();
        int max = Integer.MIN_VALUE;
        for (int sample:samples){
            if (sample > max){
                max = sample;
            }
        }*/
        
       // Turn the samples into floats ranging between -1 and 1 with 1 being the highest value and -1 the lowest (most negative)
        double[] floatSamples = new double[samples.length];
        for (int i = 0; i < floatSamples.length; i++) {
            floatSamples[i]  = (((double)samples[i])/((double)max));
        }
        
        // Print the samples for debugging
        for (double s: floatSamples){
            System.out.println(s);
        }
        System.out.println("");
        System.out.println("SAMPLES OVER");
        System.out.println("");
        
        // Average out the samples in the chunk
        floatSamples = averageFilterSamples(floatSamples);
        
        // Apply a Hann window to the chunk to reduce sidelobes
        double[] window = buildHannWindow(floatSamples.length);
        floatSamples = applyWindow(window, floatSamples, CHUNK_SIZE);
        
       //binSize = sampleRate/N so as bin size increases accuracy of the frequency increases (accurate to a smaller value)
        
        // Perform fft
        double[] fftOut = new double[floatSamples.length * 2];
        System.arraycopy(floatSamples,0,fftOut,0,floatSamples.length);
        DoubleFFT_1D fft = new DoubleFFT_1D(floatSamples.length);
        fft.realForwardFull(fftOut);
        
        double[] real = new double[floatSamples.length];
        double[] imaginary = new double[floatSamples.length];
        
        for (int k = 0; k < (fftOut.length/2); k++) {
            real[k] = fftOut[k*2];
            imaginary[k] = fftOut[k*2 + 1];
        }
        
        double[] magnitude = new double[floatSamples.length/2];
        for (int i = 0; i < (floatSamples.length / 2.0); i++) {
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
        
        double binSize = (waveFile.getSampleRate()/CHUNK_SIZE);
        
        double frequency = maxMagnitudeIndex * binSize;
        System.out.println("");
        System.out.println("Frequency " + frequency + "Hz");
        
        // Turning the interface off while testing FFT
        //UserInterface gui = new UserInterface(WIDTH,HEIGHT);
        //gui.setSize(WIDTH,HEIGHT);
        
        //gui.startDisplaying(waveFile, 5);
        out.close();
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
    
    private static void printFreq(double[] freq) {
        for (int i = 0; i < (freq.length/2); i++) {
            System.out.println(freq[i]);
        }
        System.out.println("");
        System.out.println("HALF WAY THOROUGH");
        System.out.println();
        for (int i = freq.length/2; i < freq.length; i++){
            System.out.println(freq[i]);
        }
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

    // TODO make this actually work (currently just messing around cause I really have no idea what I am doing)
    private static double[] fftOutToFrequency(double[] real, double[] imaginary, int sampleRate) {
        // TODO try having the imaginary array set to just 0
        double[] magnitudes = new double[real.length];
        for (int i = 0; i < magnitudes.length; i++) {


        
        }
        for (int i = 1; i < magnitudes.length; i++) {
            magnitudes[i] = sampleRate * 2 * Math.sin(magnitudes[i] / magnitudes[0]);
        }
        return magnitudes;
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
    
    /** Get the amplitude data representation of the wave file
     * @param waveFile The wavefile to convert to amplitude data
     * @return The amplitude data as an int array with a max value of TODO add max value here (just in comment)
     */
    private static int[] getAmplitudeData(WaveFile waveFile) {
        return waveFile.getSamples(waveFile.getLength());
    }
    
    /**
     * @param bytes
     */
    private static void printByteArray(byte[] bytes){
        for(byte b: bytes){
            System.out.print(b + " ");
        }
    }

}