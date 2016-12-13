package display;

import audio.files.AudioFile;
import org.jtransforms.fft.DoubleFFT_1D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Paul Lancaster on 28/11/2016
 */
public class AudioDisplayPanel extends JPanel{
    private BufferedImage frame; // Display frame
    private static long timeSinceLastFrame = 0; // Nanoseconds
    private ArrayList<Chunk> chunks; // Chunks
    private int chunk_pos = -1; // Position in chunk array
    
    AudioDisplayPanel(int width, int height) {
        setSize(width, height);
        frame = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    }
    
    void play(AudioFile file, VisualEffect effect) {
        switch(effect){
            case Frequency_Distribution:
                frequencyDistribution(file);
                break;
            default:
                System.out.println("Unrecognised effect");
                break;
        }
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
       // System.out.println("Painted");
        g.drawImage(frame,0,0,Color.PINK, this);
    }
    
    private void frequencyDistribution(AudioFile file){
        int CPF = 10; //Chunks per frame
        int FPS = 10; // Frames per second
        int channel = 1; // Audio channel
        boolean loadFrequencies = true; // Load amplitudes and frequencies (false means just amplitudes)
        
        file.resetPos();
        loadChunks(file,CPF,FPS,channel,loadFrequencies); // Get frequencies/amplitudes and store
        
        int WIDTH = 1000; //TODO tie into the actual width of the panel
        int HEIGHT = 1000; // TODO tie into the actual height of the panel
        int freqBands = 100; // The number of frequency bands, more bands means a more accurate representation of the various frequencies
        long NANO_PER_FRAME = (1000000000L/FPS); // Nano seconds per frame
        
        long lastTime = System.nanoTime();
        while (chunksLeft()){
            long currentTime = System.nanoTime();
            long dT = Math.abs(currentTime-lastTime);
            timeSinceLastFrame=timeSinceLastFrame+dT;
            if (timeSinceLastFrame >= NANO_PER_FRAME){
                frame = new BufferedImage(frame.getWidth(),frame.getHeight(),frame.getType());
                Graphics2D g2d = frame.createGraphics();
                g2d.setColor(Color.CYAN);
                generateFrame(g2d,WIDTH,HEIGHT,nextChunks(CPF,channel),freqBands,file.getSampleRate());
                this.repaint();
                timeSinceLastFrame = 0;
            }
            lastTime = currentTime;
        }
    }
    
    private boolean chunksLeft() {
        return chunk_pos+1 < chunks.size(); //chunk_pos + 1 because size of array lists starts at 1 not 0
    }
    
    private int[] scaleArray(int[] array, int limit){
        // Get max frequency
        int maxVal = Integer.MIN_VALUE;
        for (int n : array) {
            if (n > maxVal) {
                maxVal = n;
            }
        }
        // Bring all values within range of the limit
        int[] yValues = new int[array.length];
        
        for (int i = 0; i < yValues.length; i++) {
            yValues[i] = HEIGHT - 1 - (int) Math.round((array[i] / ((double) maxVal)) * (HEIGHT));
        }
        
        return yValues;
    }
    
    public static void banter(){
        System.out.println("Banter");
    }
    
    private void averageOutArray(int[] array) {
        int AVERAGE_PASSES = 5; // Average out waveform this many times
        for (int run = 0; run < AVERAGE_PASSES; run++) {
            for (int k = 0; k < AVERAGE_PASSES; k++) {
                int lastFreq = array[0];
                for (int i = 1; i < array.length; i++) {
                    array[i] = (int) ((lastFreq + array[i]) / 2.0);
                    lastFreq = array[i];
                }
            }
        }
    }
    
    //    1   So the image updates continously between 10 and 30 times a second
    //    2   The chunk size is a factor of sample rate * time of each frame(seconds)
    //    3   so at 10fps and sample rate 44100
    //    4   chunksize is a factor of 4410 eg. 3 or 5 although preferably a number which gives the highest efficiency (need to research)
    //    5   So for each the frequencies of all the chunks in the frame is worked out and the corresponding amplitude at that frequency
    //    6   The highest amplitude at each frequency band (There will be 20 bands for now but the number of bands can increase/decrease)
    //    7   All bands contain cover an equivalent number of frequencies each
    //    8   The highest amplitude at that band will then be displayed as a bar
    //    9   This will update each frame as the song moves along.
    //   10   For now just channel 1 will be displayed but channel 2 will be added later and displayed in a different colour
    
    private void generateFrame(Graphics2D g2d, int width, int height, Chunk[] chunks, int freqBands, int sampleRate) {
        int bandSize = (int)((sampleRate/2.0)/ freqBands); // Since the frequency can't actually be higher than sample rate divided by 2
        int BASE_Y = 600;
        int BAND_WIDTH = (width/freqBands);
        double[] bandAmplitudes = new double[freqBands-1];
        
        for (Chunk chunk: chunks){
            int band = (int)(chunk.frequency/bandSize);
            /* The frequency divided by the band size rounded down.
             Eg.
             Frequency = 10000, freqBands = 10, sample rate = 44100
             Each band covers 4410 frequencies
             Frequency/4410 = 2 (rounding down)
             This means that the frequency is in the band of 2 * 4410 = 8820-13230Hz (band 3)
             The bands range from 0-(freqBands-1)
            */
                    
            if (bandAmplitudes[band] == 0){
                bandAmplitudes[band] = chunk.amplitude;
            }else{
                bandAmplitudes[band] = (bandAmplitudes[band] + chunk.amplitude)/2.0;
            }
        }
        
        g2d.setColor(Color.GREEN);
        g2d.drawLine(0,BASE_Y,width,BASE_Y); // Base (zero) line
        g2d.setColor(Color.red);
        for (int band = 0; band < bandAmplitudes.length; band++) {
            int x = band*BAND_WIDTH;
            double BAND_MAX_HEIGHT = 400;
            int BAND_HEIGHT = (int)(BAND_MAX_HEIGHT*(bandAmplitudes[band]/33000.0));
            int y = BASE_Y-BAND_HEIGHT;
            g2d.fillRect(x,y,BAND_WIDTH,BAND_HEIGHT);
        }
    }
    
    // Returns the number of nanoseconds per chunk
    // cpf = Chunks Per frame
    // fps = frame per second
    // Boolean frequencies = Should the frequencies be loaded and stored as well or just the amplitudes
    private void loadChunks(AudioFile file,int CPF,int FPS, int channel, boolean frequencies){
        int CHUNK_SIZE = (file.getSampleRate()/(FPS*CPF));
        long numberOfChunks =(file.getNumberOfSamples()/CHUNK_SIZE)/2; // FIXME If not divided by 2 then the file is to long (by double) and the second half is all 0 amplitude but I have no idea why
        
        chunks = new ArrayList<>();
        
        for (int i = 0; i < numberOfChunks; i++) { // Get the chunks, get their amplitude average then store along with frequency
            short[] chunkAmp = file.getChunk(CHUNK_SIZE,channel);
            if (frequencies) {
                chunks.add(new Chunk(chunkAmp, getFreqOfChunk(chunkAmp, file.getSampleRate())));
            }else{
                chunks.add(new Chunk(chunkAmp,-1));
            }
        }
        int samplesLeft = (int)(file.getNumberOfSamples() - numberOfChunks*CHUNK_SIZE);
        if (samplesLeft > 0){ // Get the last chunk (which may be a different size to the others to make sure no samples are ignored //FIXME this may need removed if it causes issues at the end)
            short[] chunkAmp = file.getChunk(samplesLeft,channel);
            chunks.add(new Chunk(chunkAmp,getFreqOfChunk(chunkAmp,file.getSampleRate())));
        }
    }
    
    private Chunk[] nextChunks(int chunks, int channel){
        Chunk[] chunk = new Chunk[chunks];
        for (int i = 0; i < chunks; i++) {
            if(chunksLeft()) {
                chunk[i] = nextChunk(channel);
            }else{
                chunk[i] = new Chunk();
            }
        }
        return chunk;
    }
    
    private Chunk nextChunk(int channel){
        chunk_pos++;
        return chunks.get(chunk_pos);
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
        return maxMagnitudeIndex * binSize;
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
    
    //TODO this is likely a bad way to do this but idk how else yet
    private class Chunk {
        private int amplitude;
        private double frequency;
        // Default chunk everything 0
        public Chunk() {
            amplitude = 0;
            frequency = 0;
        }
        Chunk(short[] amplitudes, double frequency){
            int temp = 0;
            for (short amp: amplitudes){
                temp=temp+Math.abs(amp);
            }
            this.amplitude = (temp/amplitudes.length);
            this.frequency = frequency;
        }
        @Override
        public String toString(){
            return String.format("%d at %.1fHz",amplitude,frequency);
        }
    }
    
    @Override
    public String toString() {
        return "AudioDisplayPanel{" +
                "frame=" + frame +
                ", chunks=" + chunks +
                ", chunk_pos=" + chunk_pos +
                '}';
    }
}
