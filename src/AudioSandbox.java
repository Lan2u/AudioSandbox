import org.jtransforms.fft.DoubleFFT_1D;
import sound.files.WaveFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Paul Lancaster on 30/10/2016
 */

    
public class AudioSandbox {
    private static final String FILE_PATH = "resources/audiocheck.net_sin_1000Hz_-3dBFS_2s.wav";

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
    
        // Get the average of the 2 channels
        int SAMPLES_LENGTH = 1024;
        double[] samples = new double[SAMPLES_LENGTH];
        for (int i = 0; i < SAMPLES_LENGTH; i++) {
            samples[i] = waveFile.getSample();
        }
    
        double[] sampleAvg = getAmplitudeAverage(samples, waveFile.getChannels());
        
        double[] fftOut = new double[sampleAvg.length * 2];
        System.arraycopy(sampleAvg,0,fftOut,0,sampleAvg.length);
        
        DoubleFFT_1D fft = new DoubleFFT_1D(sampleAvg.length);
        fft.realForwardFull(fftOut);
        
        double[] real = new double[sampleAvg.length+1];
        double[] imaginary = new double[sampleAvg.length+1];
        
        for (int k = 0; k < (fftOut.length/2); k++) {
            real[k] = fftOut[k*2];
            imaginary[k] = fftOut[k*2 + 1];
        }
        
        double[] freq = fftOutToFrequency(real, imaginary, waveFile.getSampleRate());
        for (int i = 0; i < (freq.length/2); i++) {
            System.out.println(freq[i]);
        }
        System.out.println("");
        System.out.println("HALF WAY THORUGH");
        System.out.println();
        for (int i = freq.length/2; i < freq.length; i++){
            System.out.println(freq[i]);
        }
    
        
        // Turning the interface off while testing FFT
        //UserInterface gui = new UserInterface(WIDTH,HEIGHT);
        //gui.setSize(WIDTH,HEIGHT);
        
        //gui.startDisplaying(waveFile, 5);
        out.close();
    }
    
    // TODO make this actually work (currently just messing around cause I really have no idea what I am doing)
    private static double[] fftOutToFrequency(double[] real, double[] imaginary, int sampleRate) {
        // TODO try having the imaginary array set to just 0
        double[] magnitudes = new double[real.length];
        for (int i = 0; i < magnitudes.length; i++) {
            magnitudes[i] = Math.sqrt(Math.pow(real[i], 2)+ Math.pow(imaginary[i], 2));
        
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