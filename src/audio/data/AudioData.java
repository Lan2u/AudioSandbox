package audio.data;

import javax.sound.sampled.AudioInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 24/11/2016
 */
public abstract class AudioData {
    
    public static AudioData getData(int dataSize, int numberOfChannels) {
        switch(numberOfChannels){
            case 1:
                return new MonoAudioData(dataSize);
            case 2:
                return new StereoAudioData(dataSize);
            default:
                throw new IllegalArgumentException("Unsupported number of channels");
        }
    }
    
    public abstract void readData(FileInputStream in, int bytesPerSample) throws IOException;
    
    public abstract void readData(AudioInputStream in) throws IOException;
    
    /**
     * Coverts the byte array (of length 2 maximum at this point) into a sample and returns it
     * @param sample The byte array with data on the sample
     * @return The sample
     */
    int convertToSample(byte[] sample){
        switch(sample.length){
            case 1:
                System.out.println("Sample case one not implemented");
                return -1;
            case 2:
                return (sample[1] << 8 | sample[0] & 0xFF);
            default:
                // FIXME this only supports 2 bytes per sample
                throw new IllegalArgumentException("AudioData#convertToSample doesn't support more than 2 bytes per sample");
        }
        
    }
    
    public abstract int getNumberOfSamples();
    
    public abstract boolean hasNextSample(int channel);
    
    public abstract int[] getChunk(int samples, int channel);
    
    public abstract int[] getSamples(double seconds, int sampleRate, int channel, double length);
    
    public abstract void resetPos();
    
    public abstract int getSamplesLeft(int channel);
    
    public abstract boolean hasNextSamples(int chunkSize, int channel);
}
