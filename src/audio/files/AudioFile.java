package audio.files;

/**
 * Created by Paul Lancaster on 11/11/2016
 */
public interface AudioFile {
    double getLength();
    int getSampleRate();
    int getChannels();
    int getNumberOfSamples();
    
    int[] getAllSamples(int channel);
    short[] getChunk(int samples,int channel);
    int[] getSamples(double seconds, int channel);
}
