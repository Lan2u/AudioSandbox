package audio.files.loaded;

import audio.files.AudioFile;
import org.jtransforms.fft.DoubleFFT_1D;

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
    
    private int[] getFrequencies(AudioFile file, int CHUNK_SIZE /* Size of each chunk in samples */) {
        double NUMBER_OF_CHUNKS = Math.ceil(file.getNumberOfSamples() / CHUNK_SIZE);
        
        int[] frequencies = new int[(int) NUMBER_OF_CHUNKS];
        
        int CHANNEL = 1;
        
        for (int i = 0; i < frequencies.length; i++){
            short[] chunk = file.getChunk(CHUNK_SIZE,CHANNEL);
            frequencies[i] = (int) Math.round(getFreqOfChunk(chunk, file.getSampleRate()));
            if (frequencies[i] < 0) frequencies[i] = Math.abs(frequencies[i]); // positive negative values
            // TODO understand why we get negative values and how to handle them
        }
        return frequencies;
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
    
    // TODO testing
    // Get the frequency of a chunk
    private double getFreqOfChunk(short[] chunk, int sampleRate) {
        // Average out the samples in the chunk
        //   chunk = averageFilterSamples(chunk); // TUrned off because the frequency data was more spread out and spiky not less
        
        // Apply a Hann window to the chunk to reduce sidelobes
        double[] window = buildHannWindow(chunk.length);
        chunk = applyWindow(window, chunk);
        
        // binSize = sampleRate/N so as bin size increases accuracy of the frequency
        // increases (accurate to a smaller value)
        
        // Perform fft
        short[] fftOut = new short[chunk.length * 2];
        System.arraycopy(chunk, 0, fftOut, 0, chunk.length);
        DoubleFFT_1D fft = new DoubleFFT_1D(fftOut.length / 2); // FIXME check that the right length is being passed here and that it isn't too big
        fft.realForwardFull(shortToDouble(fftOut));
        
        double[] real = new double[chunk.length];
        double[] imaginary = new double[chunk.length];
        
        for (int k = 0; k < chunk.length; k++) {
            real[k] = fftOut[k * 2];
            imaginary[k] = fftOut[k * 2 + 1];
        }
        
        // THIS IS NOT AN ARRAY OF FREQUENCIES IT IS AN ARRAY OF MAGNITUDES
        double[] magnitude = new double[chunk.length]; // FIXME array length might be able to be halfed, testing needed
        for (int i = 0; i < magnitude.length; i++) {
            magnitude[i] = Math.sqrt(Math.pow(real[i], 2) + Math.pow(imaginary[i], 2));
        }
        
        int maxMagnitudeIndex = -1;
        double maxMagnitude = Double.MIN_VALUE;
        for (int i = 0; i < magnitude.length; i++) {
            if (magnitude[i] > maxMagnitude) {
                maxMagnitude = magnitude[i];
                maxMagnitudeIndex = i;
            }
        }
        double binSize = (sampleRate / chunk.length);
        return Math.abs(maxMagnitudeIndex * binSize);
    }
    
    private double[] shortToDouble(short[] array) {
        double[] temp = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            temp[i] = array[i];
        }
        return temp;
    }
    
    //TODO testing I don't think this works properly
    private static short[] averageFilterSamples(short[] chunkSamples) {
        // Low-Pass filtering : Average out the frequency this will smooth out large peaks or troughs in the data
        // http://blog.bjornroche.com/2012/07/frequency-detection-using-fft-aka-pitch.html
        
        short[] filterOutput = new short[chunkSamples.length];
        short lastSample = chunkSamples[0];
        filterOutput[0] = chunkSamples[0];
        for (int i = 1; i < chunkSamples.length; i++) {
            filterOutput[i] = (short)((chunkSamples[i] + lastSample)/2);
            lastSample = filterOutput[i];
        }
        return filterOutput;
    }
    
    //TODO testing
    private static double[] buildHannWindow(int size) {
        double[] window = new double[size];
        for (int i = 0; i <size; i++) {
            window[i] = 0.5 * (1 - Math.cos(2 * Math.PI * i/ (size - 1.0)));
        }
        return window;
    }
    
    //TODO testing
    private static short[] applyWindow(double[] window, short[] samples) {
        for (int i = 0; i < samples.length; i++) {
            samples[i] = (short)Math.round(samples[i] * window[i]);
        }
        return samples;
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
