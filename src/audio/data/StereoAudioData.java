package audio.data;

import javax.sound.sampled.AudioInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Paul Lancaster on 24/11/2016
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class StereoAudioData extends AudioData{
    private int dataSize; // The size of the data in each channel
    
    private int[] sample_ch1; // Channel 1 samples
    private int[] sample_ch2; // Channel 2 samples
    
    private int pos_ch1 = -1; // Position within the data in samples
    private int pos_ch2 = -1;
    
    StereoAudioData(int dataSize) {
        this.dataSize = dataSize;
    }
    
    public boolean hasNextSample(int channel) {
        switch(channel){
            case 1:
                return pos_ch1 < sample_ch1.length;
            case 2:
                return pos_ch2 < sample_ch2.length;
            default:
                throw new IllegalArgumentException("Channel out of range");
        }
    }
    
    @Override
    public int[] getChunk(int samples, int channel) {
        int[] chunk  = new int[samples];
        for (int i = 0; i < samples; i++) {
            chunk[i] = nextSample(channel);
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
                    samples[i] = nextSample(channel);
                }
                return samples;
            case 2:
                double secondsLeftCh2 = length - ((double)pos_ch2/(double)sampleRate);
                int samplesToReadCh2;
                if (seconds < secondsLeftCh2){
                    samplesToReadCh2 = (int)(seconds * sampleRate);
                }else{
                    samplesToReadCh2 =  (int)(secondsLeftCh2 * sampleRate);
                }
    
                if (samplesToReadCh2 == 0) return new int[0];
                
                int[] samplesCh2 = new int[samplesToReadCh2];
                for (int i = 0; i < samplesCh2.length; i++) {
                    samplesCh2[i] = nextSample(channel);
                }
                return samplesCh2;
            default:
                throw new IllegalArgumentException("Channel out of range ("+channel+")");
        }
    }
    
    @Override
    public void resetPos() {
        pos_ch1 = 0;
        pos_ch2 = 0;
    }
    
    @Override
    public int getSamplesLeft(int channel) {
        switch(channel){
            case 1:
                return (getNumberOfSamples() - pos_ch1 -1 );
            case 2:
                return (getNumberOfSamples() - pos_ch2 - 1);
            default:
                System.out.println("Samples left on channels other than 1 and 2 not implemented");
                return 0;
        }
    }
    
    @Override
    public boolean hasNextSamples(int chunkSize, int channel) {
        switch (channel){
            case 1:
                return (pos_ch1+chunkSize) < sample_ch1.length;
            case 2:
                return (pos_ch2+chunkSize) < sample_ch2.length;
            default:
                return false;
        }
    }
    
    private int nextSample(int channel) {
        switch(channel){
            case 1:
                if (pos_ch1 >= (sample_ch1.length -1)){
                    System.out.println("Sample (ch1) out of range");
                    return 0;
                }else {
                    pos_ch1++;
                    return sample_ch1[pos_ch1];
                }
            case 2:
                pos_ch2++;
                if (pos_ch2 >= (sample_ch2.length-1)){
                    System.out.println("Sample (ch2) out of range");
                    return 0;
                }else {
                    return sample_ch2[pos_ch2];
                }
            default:
                throw new IllegalArgumentException("Channel out of range");
        }
    }
    
    @Override
    public void readData(FileInputStream in, int bytesPerSample) throws IOException {
        byte[] data = new byte[dataSize*2];
        
        in.read(data);
    
        byte[] data_ch1 = new byte[dataSize]; // Channel 1 bytes
        byte[] data_ch2 = new byte[dataSize]; // Channel 2 bytes
        
        // Data_ch1 and data_ch2
        for (int k = 0; k < dataSize; k++) {
            data_ch1[k] = data[k*2];
            data_ch2[k] = data[k*2+1];
        }
        
        sample_ch1 = new int[(int)Math.ceil(dataSize/bytesPerSample)];
        sample_ch2 = new int[(int)Math.ceil(dataSize/bytesPerSample)];
        
        // Sample_ch1
        for (int i = 0; i < (data_ch1.length/bytesPerSample); i++) {
            byte[] sampleData = new byte[bytesPerSample];
            System.arraycopy(data_ch1,i*bytesPerSample, sampleData, 0, bytesPerSample);
            sample_ch1[i] = (short) convertToSample(sampleData);
        }
        
        // Sample_ch2
        for (int i = 0; i < (data_ch2.length/bytesPerSample); i++) {
            byte[] sampleData = new byte[bytesPerSample];
            System.arraycopy(data_ch2,i*bytesPerSample, sampleData, 0, bytesPerSample);
            sample_ch2[i] = (short) convertToSample(sampleData);
        }
    }
    
    @Override
    public void readData(AudioInputStream in) throws IOException {
        
    }
    
    @Override
    public int getNumberOfSamples(){
        return sample_ch1.length;
    }
    
    @Override
    public String toString() {
        return "StereoAudioData{" +
                ", sample_ch1=" + Arrays.toString(sample_ch1) +
                ", sample_ch2=" + Arrays.toString(sample_ch2) +
                ", pos_ch1=" + pos_ch1 +
                ", pos_ch2=" + pos_ch2 +
                '}';
    }
}
