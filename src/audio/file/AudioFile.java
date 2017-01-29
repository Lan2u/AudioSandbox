package audio.file;

/**
 * Created by Paul Lancaster on 11/11/2016
 */
public interface AudioFile {
    double getLength();
    
    int getSampleRate();
    
    int getChannels();
    
    int getNumberOfSamples();
    
    /**
     * Return all the samples in that channel ( this moves the position marker up)
     *
     * @param channel The channel
     * @return The samples
     */
    int[] getAllSamples(int channel);
    
    int[] getChunk(int samples, int channel);
    
    int[] getSamples(double seconds, int channel);
    
    
    /**
     * Reset the read position of the file back to the start
     */
    void resetPos();
    
    /**
     * Checks if there is another sample left in the specified channel
     *
     * @param channel The channel to check
     * @return true if there is another sample and false if there isn't
     */
    boolean hasNextSample(int channel);
    
    /**
     * @param channel The channel to see how many samples left
     * @return The number of samples left in the specified channel
     */
    int samplesLeft(int channel);
    
    /**
     * Checks if there are at least that number of samples left in the specified channel
     *
     * @param number  The number of samples
     * @param channel The channel to check
     * @return True if there are that many samples left and false if there isn't
     */
    boolean hasNextSamples(int number, int channel);
}
