package audio.files;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 24/11/2016
 */
class MonoAudioData extends AudioData{
    private byte[] data_ch1; // Audio byte data
    
    private short[] sample_ch1; // Audio sample data
    
    private int pos_ch1 = -1; // Position within the data in samples
    
    MonoAudioData(int dataSize) {
        data_ch1 = new byte[dataSize];
    }
    
    boolean hasNextSample(int channel) {
        return pos_ch1 < data_ch1.length;
    }
    
    @Override
    short[] getChunk(int samples, int channel) {
        short[] chunk  = new short[samples];
        for (int i = 0; i < samples; i++) {
            chunk[i] = nextSample();
        }
        return chunk;
    }
    
    @Override
    int[] getSamples(double seconds, int sampleRate, int channel, double length) {
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
    
    short nextSample() {
        if (pos_ch1 > sample_ch1.length){
            return 0;
        }
        pos_ch1++;
        return sample_ch1[pos_ch1];
    }
    
    @Override
    public void readData(FileInputStream in, int bytesPerSample) throws IOException {
        System.out.println(in.read(data_ch1) + " Bytes of mono audio data read into memory");
        // Sample_ch1
        sample_ch1 = new short[(int)Math.ceil(data_ch1.length/bytesPerSample)];
        for (int i = 0; i < (data_ch1.length/bytesPerSample); i++) {
            byte[] sampleData = new byte[bytesPerSample];
            System.arraycopy(data_ch1,i*bytesPerSample, sampleData, 0, bytesPerSample);
            sample_ch1[i] = (short) getAmplitude(sampleData);
        }
    }
    
    @Override
    int getNumberOfSamples(){
        return sample_ch1.length;
    }
}
