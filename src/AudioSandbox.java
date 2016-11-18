import audio.transforms.AudioDataTransformation;
import audio.files.WaveFile;

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
        System.out.println(waveFile); // Print file header
        
        int CHUNK_SIZE = 1024;
        
        // Get a chunk of samples and record the max amplitude of the chunk
        double[] chunkSamples = new double[CHUNK_SIZE];
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < CHUNK_SIZE; i++) {
            chunkSamples[i] = waveFile.getSample();
            if (chunkSamples[i] > max){
                max = chunkSamples[i];
            }
        }
        
       // Turn the samples into floats ranging between -1 and 1 with 1 being the highest value and -1 the lowest (most negative)
        double[] floatSamples = intSamplesToFloats(chunkSamples, max);
    
        System.out.println(AudioDataTransformation.getFrequencyOfChunk(floatSamples,waveFile.getSampleRate()) + "Hz");
        
        // Turning the interface off while testing FFT
        // UserInterface gui = new UserInterface(WIDTH,HEIGHT);
        // gui.startDisplaying(waveFile, 5);
        
        out.close();
    }
    
    private static double[] intSamplesToFloats(double[] samples, double max) {
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