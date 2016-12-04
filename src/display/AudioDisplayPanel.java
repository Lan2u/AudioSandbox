package display;

import audio.files.AudioFile;
import org.jtransforms.fft.DoubleFFT_1D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 28/11/2016
 */
public class AudioDisplayPanel extends JPanel{
    
    BufferedImage frame;
    
    AudioDisplayPanel(int width, int height) {
        setSize(width, height);
        
    }
    
    public void play(AudioFile file, VisualEffect effect) {
        switch(effect){
            case Frequency_Distribution:
                frequencyDistribution(file);
                break;
        }
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(frame,0,0, this);
    }
    
    private void frequencyNumberDisplay(AudioFile file) {
        int[] frequencies = getFrequencies(file, 64);
        System.out.println("Frequency number display not implemented");
    }
    
    private void frequencyDistribution(AudioFile file){
        /*
        
            
            
         */
        int WIDTH = 1800;
        int HEIGHT = 800;
        
        
        int fps = 10;
        long samplesPerFrame = Math.round(file.getNumberOfSamples()/fps);
        int chunkSize = 20;
        
        long dT = 10000;
        getNextFrame(chunkSize,samplesPerFrame,dT);
        
        int CHUNK_SIZE = 128;
        
            System.out.println("Loop start");
            file.resetPos();
            int[] frequencies = getFrequencies(file, CHUNK_SIZE);
            int[] yValues = new int[frequencies.length];
            // Get max frequency
            int maxFreq = (file.getSampleRate() / 2);
            for (int freq : frequencies) {
                if (freq > maxFreq) {
                    maxFreq = freq;
                }
            }
    
            // Average frequencies
            // int AVERAGE_PASSES = 5; // Average out waveform this many times
            // for (int run = 0; run < AVERAGE_PASSES; run++) {
            //   for (int k = 0; k < AVERAGE_PASSES; k++) {
            //       int lastFreq = frequencies[0];
            //       for (int i = 1; i < frequencies.length; i++) {
            //          frequencies[i] = (int) ((lastFreq + frequencies[i]) / 2.0);
            //          lastFreq = frequencies[i];
            //      }
            //  }
    
    
            // Bring all frequencies within range of the height
            for (int i = 0; i < yValues.length; i++) {
                yValues[i] = HEIGHT - 1 - (int) Math.round((frequencies[i] / ((double) maxFreq)) * (HEIGHT));
            }
    
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            for (int x = 0; x < yValues.length; x++) {
                if (x < WIDTH) {
                    graphics.drawLine(x, HEIGHT, x, yValues[x]);
                } else {
                    System.out.println(frequencies[x]);
                }
            }
            try {
                ImageIO.write(image, "png", new File("CHUNKSIZE" + CHUNK_SIZE + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("File made");
    }
    
    
    
    /*
    1   So the image updates continously between 10 and 30 times a second
        2   The chunk size is a factor of sample rate * time of each frame(seconds)
        3   so at 10fps and sample rate 44100
        4   chunksize is a factor of 4410 eg. 3 or 5 although preferably a number which gives the highest efficiency (need to research)
        5   So for each the frequencies of all the chunks in the frame is worked out and the corresponding amplitude at that frequency
        6   The highest amplitude at each frequency band (There will be 20 bands for now but the number of bands can increase/decrease)
        7   All bands contain cover an equivalent number of frequencies each
        8   The highest amplitude at that band will then be displayed as a bar
        9   This will update each frame as the song moves along.
       10   For now just channel 1 will be displayed but channel 2 will be added later and displayed in a different colour
     */
    private long timeSinceLastFrame = 0; // nano seconds
    private static long NANO_PER_FRAME = 0; // nano seconds
    private int channel = 1;
    
    private void getNextFrame(AudioFile file, int chunkSize,int samplesPerFrame, long dT) {
        // Use the chunk size to get all the samples except the last
        // chunk which is a smaller size to make up the samples
        
        timeSinceLastFrame =+ dT;
        if (timeSinceLastFrame > NANO_PER_FRAME){
            
            int numberOfChunks = samplesPerFrame/chunkSize;
            Chunk[] chunks = new Chunk[numberOfChunks+1];
            
            for (int i = 0; i < numberOfChunks; i++) { // Get the chunks, get their amplitude average then store along with frequency
                short[] chunkAmp = file.getChunk(chunkSize,channel);
                Chunk chunk = new Chunk(chunkAmp,getFreqOfChunk(chunkAmp,file.getSampleRate()));
                chunks[i] = chunk;
            }
            int samplesLeft = samplesPerFrame - numberOfChunks*chunkSize;
            if (samplesLeft > 0){ // Get the last chunk (which may be a different size to the others to make up the samples per frame)
                short[] chunkAmp = file.getChunk(samplesLeft,channel);
                Chunk chunk = new Chunk(chunkAmp,getFreqOfChunk(chunkAmp,file.getSampleRate()));
                chunks[chunks.length-1] = chunk;
            }
            timeSinceLastFrame = 0;
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
    
    // Low-Pass filtering : Average out the frequency this will smooth out large peaks or troughs in the data
    // http://blog.bjornroche.com/2012/07/frequency-detection-using-fft-aka-pitch.html
    //TODO testing I don't think this works properly
    private static short[] averageFilterSamples(short[] chunkSamples) {
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
        private short amplitude;
        private double frequency;
        Chunk(short[] amplitudes, double frequency){
            // TODO average amplitudes then store
            this.frequency = frequency;
        }
        
    }
}