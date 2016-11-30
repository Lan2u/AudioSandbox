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
        int WIDTH = 1800;
        int HEIGHT = 800;
        // We want there to be as many chunks as there are pixels of width
       // int CHUNK_SIZE = (file.getNumberOfSamples() / WIDTH);
        
        int CHUNK_SIZE = 1000;
        int[] frequencies = getFrequencies(file, CHUNK_SIZE);
        int[] yValues = new int[frequencies.length];
        // Get max frequency
        int maxFreq = 0;
        for (int freq: frequencies){
            if (freq > maxFreq){
                maxFreq = freq;
            }
        }
        // Bring all frequencies within range of the height
        for (int i = 0; i < yValues.length; i++) {
            yValues[i] = HEIGHT - 1 -(int)Math.round((frequencies[i]/((double)maxFreq))*(HEIGHT - 10.0));
        }
    
        BufferedImage image = new BufferedImage(yValues.length, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        for (int x = 0; x < yValues.length; x++) {
            if (x < WIDTH ){
                graphics.drawLine(x,HEIGHT,x,yValues[x]);
            }else{
                System.out.println(frequencies[x]);
            }
        }
        try {
            ImageIO.write(image,"png",new File("Frequency.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private int[] getFrequencies(AudioFile file, int CHUNK_SIZE /* Size of each chunk in samples */) {
        double NUMBER_OF_CHUNKS = Math.ceil(file.getNumberOfSamples() / CHUNK_SIZE);
    
        int[] frequencies = new int[(int) NUMBER_OF_CHUNKS];
        for (int i = 0; i < frequencies.length; i++){
            short[] chunk = file.getChunk(CHUNK_SIZE,1);
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
        chunk = averageFilterSamples(chunk);
    
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
    //TODO testing
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
    
}
