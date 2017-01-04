package audio.file;

/**
 * Created by Paul Lancaster on 04/01/2017
 */
public class Mp3File implements AudioFile {
    // TODO Access the mp3 and read in a chunk of data
    // TODO Read the headers of this data and use this to get information about each frame in the data chunk
    // TODO extract the audio data from the data section
    // TODO convert the data to samples and store theses
    
    
    @Override
    public double getLength() {
        return 0;
    }
    
    @Override
    public int getSampleRate() {
        return 0;
    }
    
    @Override
    public int getChannels() {
        return 0;
    }
    
    @Override
    public int getNumberOfSamples() {
        return 0;
    }
    
    @Override
    public int[] getAllSamples(int channel) {
        return new int[0];
    }
    
    @Override
    public int[] getChunk(int samples, int channel) {
        return new int[0];
    }
    
    @Override
    public int[] getSamples(double seconds, int channel) {
        return new int[0];
    }
    
    /**
     * Reset the read position of the file back to the start
     */
    @Override
    public void resetPos() {
        
    }
    
    /**
     * Checks if there is another sample left in the specified channel
     *
     * @param channel The channel to check
     * @return true if there is another sample and false if there isn't
     */
    @Override
    public boolean hasNextSample(int channel) {
        return false;
    }
    
    /**
     * @param channel The channel to see how many samples left
     * @return The number of samples left in the specified channel
     */
    @Override
    public int samplesLeft(int channel) {
        return 0;
    }
    
    /**
     * Checks if there are at least that number of samples left in the specified channel
     *
     * @param number  The number of samples
     * @param channel The channel to check
     * @return True if there are that many samples left and false if there isn't
     */
    @Override
    public boolean hasNextSamples(int number, int channel) {
        return false;
    }
}
