package audio.file;

import audio.data.AudioData;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 04/01/2017
 */
public class Mp3File implements AudioFile {
    AudioHeader header;
    AudioData data;
    
    
    // TODO Access the mp3 and read in a chunk of data
    // TODO Read the headers of this data and use this to get information about each frame in the data chunk
    // TODO extract the audio data from the data section
    // TODO convert the data to samples and store theses
    
    public Mp3File(File file) throws IOException, UnsupportedAudioFileException {
        header = new AudioHeader();
        loadData(file);
    }
    // http://www.programcreek.com/java-api-examples/index.php?api=javax.sound.sampled.AudioInputStream
    public void loadData(File file) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
        AudioFormat audioFormat = audioIn.getFormat();
        int CHUNK_SIZE = audioFormat.getFrameSize();
        byte[] b;
        while (audioIn.read(b = new byte[CHUNK_SIZE]) > -1){
            
        }
        
    }
    
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
