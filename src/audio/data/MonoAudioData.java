package audio.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Paul Lancaster on 24/11/2016
 */
public class MonoAudioData extends AudioData{
    private byte[] data_ch1; // Audio byte data
    
    private int[] sample_ch1; // Audio sample data
    
    private int pos_ch1 = -1; // Position within the data in samples
    
    MonoAudioData(int dataSize) {
        data_ch1 = new byte[dataSize];
    }
    
    public boolean hasNextSample(int channel) {
        return pos_ch1 < data_ch1.length;
    }
    
    @Override
    public int[] getChunk(int samples, int channel) {
        int[] chunk  = new int[samples];
        for (int i = 0; i < samples; i++) {
            chunk[i] = nextSample();
            if (chunk[i] == Integer.MAX_VALUE){
                return chunk;
            }
        }
        return chunk;
    }
    
    @Override
    public int[] getSamples(double seconds, int sampleRate, int channel, double length) {
        switch (channel){
            case 1:
                double secondsLeft = length - ((double)pos_ch1/(double)sampleRate);
                int samplesToRead;
                if (seconds < secondsLeft){
                    samplesToRead = (int)(seconds * sampleRate);
                }else{
                    samplesToRead =  (int)(secondsLeft * sampleRate);
                }
            
                if (samplesToRead == 0) return new int[0];
                
                int[] samples = new int[samplesToRead];
                for (int i = 0; i < samples.length; i++) {
                    samples[i] = nextSample();
                }
                return samples;
            default:
                throw new IllegalArgumentException("Channel out of range ("+channel+")");
        }
    }
    
    private int nextSample() {
        if (pos_ch1 >= (sample_ch1.length-1)){
            System.out.println("Position out of range (mono audio) : " + pos_ch1);
            return Integer.MAX_VALUE; // Should never be returned normally so is used to terminate
        }
        pos_ch1++;
        return sample_ch1[pos_ch1];
    }
    
    @Override
    public void readData(FileInputStream in, int bytesPerSample) throws IOException {
        System.out.println(in.read(data_ch1) + " Bytes of mono audio data read into memory");
        // Sample_ch1
        sample_ch1 = new int[(int)Math.ceil(data_ch1.length/bytesPerSample)];
        for (int i = 0; i < (data_ch1.length/bytesPerSample); i++) {
            byte[] sampleData = new byte[bytesPerSample];
            System.arraycopy(data_ch1,i*bytesPerSample, sampleData, 0, bytesPerSample);
            sample_ch1[i] = (short) getAmplitude(sampleData);
        }
    }
    
    @Override
    public int getNumberOfSamples(){
        return sample_ch1.length;
    }
    
    @Override
    public void resetPos() {
        pos_ch1 = 0;
    }
    
    @Override
    public int getSamplesLeft(int channel) {
        return (getNumberOfSamples() - pos_ch1-1);
    }
    
    @Override
    public String toString() {
        return "MonoAudioData{" +
                "data_ch1=" + Arrays.toString(data_ch1) +
                ", sample_ch1=" + Arrays.toString(sample_ch1) +
                ", pos_ch1=" + pos_ch1 +
                '}';
    }
}
