package audio.loaded;

import audio.file.AudioFile;
import calculate.FreqCalculator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Paul Lancaster on 13/12/2016
 */
public class LoadedFile{
    private boolean loaded = false;
    private AudioFile file;
    private VisualEffect effect;
    private ArrayList<Chunk> ch1_chunks;
    private ArrayList<Chunk> ch2_chunks;
    private int ch1_chunk_pos = -1; // Position in chunk array (Actual pos -1)
    private int ch2_chunk_pos = -1;
    private Dimension size; // Size of the produced frames
    
    public LoadedFile(AudioFile file, VisualEffect effect){
        this.file = file;
        this.effect = effect;
    }
    
    /**
     * Load the chunks that represent the audio file getting their amplitudes and frequencies
     * @return true if load successful
     */
    public boolean load(int CHUNK_SIZE){
        // Returns the number of nanoseconds per chunk
        // cpf = Chunks Per frame
        // fps = frame per second
        // Boolean frequencies = Should the frequencies be loaded and stored as well or just the amplitudes
        switch (getChannels()) {
            case 2:
                ch2_chunks = new ArrayList<>();
                processChunks(file,2, CHUNK_SIZE, ch2_chunks);
            case 1:// Fall through is on purpose
                ch1_chunks = new ArrayList<>();
                processChunks(file, 1, CHUNK_SIZE, ch1_chunks);
                break;
            default:
                throw new IllegalArgumentException("Unsupported number of channels : " + file.getChannels());
        }
        return loaded = true;
    }
    
    private void processChunks(AudioFile file, int channel, int CHUNK_SIZE, ArrayList<Chunk> chunks) {
        long numberOfChunks =(file.getNumberOfSamples()/CHUNK_SIZE)/2;
        // FIXME If not divided by 2 then the file is to long (by double) and the second half is all 0 amplitude but I have no idea why
        
        for (int i = 0; i < numberOfChunks; i++) { // Get the chunks, get their amplitude average then store along with frequency
            short[] chunkAmp = file.getChunk(CHUNK_SIZE,channel);
            chunks.add(new Chunk(chunkAmp, getFreqOfChunk(chunkAmp, file.getSampleRate())));
        }
        int samplesLeft = (int)(file.getNumberOfSamples() - numberOfChunks*CHUNK_SIZE);
        if (samplesLeft > 0){ // Get the last chunk (which may be a different size to the others to make sure no samples are ignored
            // FIXME this may need removed if it causes issues at the end)
            short[] chunkAmp = file.getChunk(samplesLeft,channel);
            chunks.add(new Chunk(chunkAmp,getFreqOfChunk(chunkAmp,file.getSampleRate())));
        }
    }
    
    public boolean chunksLeft(int channel) {
        switch (channel){
            case 1:
                return ch1_chunk_pos+1 < ch1_chunks.size();
            case 2:
                return ch2_chunk_pos+1< ch2_chunks.size();
            default:
                throw new IllegalArgumentException("Channel out of range");
        }
    }
    
    public boolean hasNextFrame() {
        //TODO THIS
        return true;
    }
    
    // Return true if the frame should be repainted
    public BufferedImage nextFrame(BufferedImage frame, long dT) {
        if (dT >= effect.getNanoPerFrame()) {
            frame = new BufferedImage((int) size.getWidth(), (int) size.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = frame.createGraphics();
            effect.drawFrame(g2d, frame.getWidth(), frame.getHeight(), this);
            return frame;
        }else{
            return null;
        }
    }
    
    private static double getFreqOfChunk(short[] chunk, int sampleRate) {
        return FreqCalculator.getFreqOfChunk(chunk, sampleRate);
    }
    
    public void setSize(Dimension size) {
        this.size = size;
    }
    
    private Chunk[] nextChunks(int chunks, int channel){
        Chunk[] chunk = new Chunk[chunks];
        for (int i = 0; i < chunks; i++) {
            if(chunksLeft(channel)) {
                chunk[i] = nextChunk(channel);
            }else{
                chunk[i] = new Chunk();
            }
        }
        return chunk;
    }
    
    private Chunk nextChunk(int channel){
        switch(channel){
            case 1:
                ch1_chunk_pos++;
                return ch1_chunks.get(ch1_chunk_pos);
            case 2:
                ch2_chunk_pos++;
                return ch2_chunks.get(ch2_chunk_pos);
            default:
                throw new IllegalArgumentException("Channel out of range : " + channel);
        }
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public AudioFile getFile() {
        return file;
    }
    
    public int getChannels() {
        return file.getChannels(); // Created to minimize typing as this is very commonly needed
    }
    
    public Chunk[] getCH1(int numOfChunks) {
        return nextChunks(numOfChunks, 1);
    }
    
    public Chunk[] getCH2(int numOfChunks) {
        return nextChunks(numOfChunks, 2);
    }
}
