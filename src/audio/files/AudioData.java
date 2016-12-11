package audio.files;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 24/11/2016
 */
abstract class AudioData {
    
    static AudioData getData(int dataSize, int numberOfChannels) {
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
    
    protected int getSample(byte[] bytes) {
        return getAmplitude(bytes);
    }
    
    int getAmplitude(byte[] sample){
        // FIXME this only supports 2 bytes per sample
        if (sample.length >2){
            throw new IllegalArgumentException("AudioData#getAmplitude doesn't support more than 2 bytes per sample");
        }
        return (sample[1] << 8 | sample[0] & 0xFF);
    }
    
    abstract int getNumberOfSamples();
    
    abstract boolean hasNextSample(int channel);
    
    abstract short[] getChunk(int samples, int channel);
    
    abstract int[] getSamples(double seconds, int sampleRate, int channel, double length);
    
    public abstract void resetPos();
    
    public abstract int getSamplesLeft(int channel);
}
