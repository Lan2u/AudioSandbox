package audio.files.loaded;

import audio.files.AudioFile;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Paul Lancaster on 28/11/2016
 */
public enum VisualEffect {
    Frequency_Number, // Display the current frequency as a number
    Amplitude_Number, // Display the current amplitude as a number
    Frequency_Distribution, // Display a static image showing frequency distribution across the song
    None; // Display the default image instead of any effects
    
    void drawFrame(Graphics2D g2d, int width, int height, LoadedFile file){ // Never called directly only called by a doing .drawFrame onto a loaded file
        switch (this){
            case Frequency_Distribution:
                int FREQ_BANDS = 10;
                generateFrequencyDistributionFrame(g2d,width,height,FREQ_BANDS,file);
                break;
            case Frequency_Number:
                break;
            case Amplitude_Number:
                break;
            case None:
                break;
            default:
                throw new IllegalArgumentException("Graphics for effect not yet implemented");
        }
    }
    
    private void generateFrequencyDistributionFrame(Graphics2D g2d, int width, int height, int FREQ_BANDS, LoadedFile file) {
        final int BAND_SIZE = (int) ((file.getFile().getSampleRate() / 2.0) / FREQ_BANDS); // Since the frequency can't actually be higher than sample rate divided by 2
        double BASE_Y = 0.1; //Percentage of the way up the panel
        int BAND_WIDTH = (width / FREQ_BANDS);
        double BAND_MAX_HEIGHT = 0.8; // Percentage of the way up the panel
        
        int BASE_Y_ABS = (int) ((1.0 - BASE_Y) * height); // Convert to actual value (not %)
        int BAND_MAX_HEIGHT_ABS =(int)(BAND_MAX_HEIGHT * height); // Convert to actual value (not %)
    
        g2d.drawLine(0, (int) BASE_Y, width, (int) BASE_Y); // Base (zero) line
        
        switch (file.getChannels()){
            case 2:
                double[] ch2_bandAmplitudes = loadBands(file.getCH2(), FREQ_BANDS, BAND_SIZE);
                drawBands(g2d, ch2_bandAmplitudes, Color.GREEN, BAND_WIDTH, BASE_Y_ABS, BAND_MAX_HEIGHT_ABS);
            case 1:
                double[] ch1_bandAmplitudes = loadBands(file.getCH1(),FREQ_BANDS,BAND_SIZE);
                drawBands(g2d, ch1_bandAmplitudes,Color.RED, BAND_WIDTH, BASE_Y_ABS, BAND_MAX_HEIGHT_ABS);
        }
    }
    
    private void drawBands(Graphics2D g2d, double[] bandAmplitudes, Color colour, int BAND_WIDTH, int BASE_Y, int BAND_MAX_HEIGHT) {
        g2d.setColor(colour);
        for (int band = 0; band < bandAmplitudes.length; band++) {
            int x = band * BAND_WIDTH;
            int BAND_HEIGHT = (int) (BAND_MAX_HEIGHT * (bandAmplitudes[band] / 33000.0));
            int y = BASE_Y - BAND_HEIGHT;
            g2d.drawRect(x, y, BAND_WIDTH, BAND_HEIGHT);
        }
    }
    
    private double[] loadBands(Chunk[] chunks, int FREQ_BANDS, int BAND_SIZE) {
        double[] bandAmplitudes = new double[FREQ_BANDS - 1];
        for(Chunk chunk: chunks){
            int band = (int) (chunk.getFrequency() / BAND_SIZE);
            if (bandAmplitudes[band] == 0) {
                bandAmplitudes[band] = chunk.getAmplitude();
            } else {
                bandAmplitudes[band] = (bandAmplitudes[band] + chunk.getAmplitude()) / 2.0;
            }
        }
        return bandAmplitudes;
    }
    
    
    private void frequencyDistribution(AudioFile file){
        int CPF = 100; //Chunks per frame
        int FPS = 10; // Frames per second
        int channel = 1; // Audio channel
        int freqBands = 50; // The number of frequency bands, more bands means a more accurate representation of the various frequencies
        long NANO_PER_FRAME = (1000000000L/FPS); // Nano seconds per frame
        
        file.resetPos();
        
        int CHUNK_SIZE = (file.getSampleRate()/(FPS*CPF));
        loadChunks(file,CPF,FPS,channel, true); // Get frequencies/amplitudes and store
        
        long lastTime = System.nanoTime();
        while (chunksLeft()){
            long currentTime = System.nanoTime();
            long dT = Math.abs(currentTime-lastTime);
            timeSinceLastFrame=timeSinceLastFrame+dT;
            if (timeSinceLastFrame >= NANO_PER_FRAME){
                frame = new BufferedImage(frame.getWidth(),frame.getHeight(),frame.getType());
                Graphics2D g2d = frame.createGraphics();
                generateFrame(g2d,this.getWidth(),this.getHeight(),nextChunks(CPF,channel),freqBands,file.getSampleRate());
                this.repaint();
                timeSinceLastFrame = 0;
            }
            lastTime = currentTime;
        }
    }
}
