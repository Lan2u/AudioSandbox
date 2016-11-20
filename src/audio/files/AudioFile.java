package audio.files;

/**
 * Created by Paul Lancaster on 11/11/2016
 */
public interface AudioFile {
    public int[] getSamples(double seconds);
    public int getSampleRate();
    public int getChannels();
    public double getLength();
    public int[] getAllSamples();
    public int getNumberOfSamples();
    
    public double[] getChunk(int chunk_size);
}
