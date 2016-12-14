package audio.loaded;

import java.awt.*;

/**
 * Created by Paul Lancaster on 28/11/2016
 */
public enum VisualEffect {
    Frequency_Number(10), // Display the current frequency as a number
    Amplitude_Number(10), // Display the current amplitude as a number
    Frequency_Distribution(10), // Display a static image showing frequency distribution across the song
    None(1); // Display the default image instead of any effects
    
    public long nanoPerFrame;
    
    VisualEffect(int FPS){
        nanoPerFrame = (1000000000L/FPS);
    }
    
    void drawFrame(Graphics2D g2d, int width, int height, LoadedFile file){ // Never called directly only called by a doing .drawFrame onto a loaded file
        switch (this){
            case Frequency_Distribution:
                int FREQ_BANDS = 400;
                drawFrequencyDistributionFrame(g2d,width,height,FREQ_BANDS,file);
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
    
    private void drawFrequencyDistributionFrame(Graphics2D g2d, int width, int height, int FREQ_BANDS, LoadedFile file) {
        final int BAND_SIZE = (int) ((file.getFile().getSampleRate() / 2.0) / FREQ_BANDS); // Since the frequency can't actually be higher than sample rate divided by 2
        double BASE_Y = 0.1; //Percentage of the way up the panel
        int BAND_WIDTH = (width / FREQ_BANDS);
        double BAND_MAX_HEIGHT = 0.8; // Percentage of the way up the panel
        int BASE_Y_ABS = (int) ((1.0 - BASE_Y) * height); // Convert to actual value (not %)
        int BAND_MAX_HEIGHT_ABS =(int)(BAND_MAX_HEIGHT * height); // Convert to actual value (not %)
        int CPF = 10; // Chunks per frame
        
        g2d.drawLine(0, (int) BASE_Y, width, (int) BASE_Y); // Base (zero) line
        
        switch (file.getChannels()){
            case 2:
                double[] ch2_bandAmplitudes = loadBands(file.getCH2(CPF), FREQ_BANDS, BAND_SIZE);
                drawBands(g2d, ch2_bandAmplitudes, Color.GREEN, BAND_WIDTH, BASE_Y_ABS, BAND_MAX_HEIGHT_ABS);
            case 1:
                double[] ch1_bandAmplitudes = loadBands(file.getCH1(CPF),FREQ_BANDS,BAND_SIZE);
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
            bandAmplitudes[band] = bandAmplitudes[band] + chunk.getAmplitude();
            
        }
        return bandAmplitudes;
    }
    
    public long getNanoPerFrame() {
        return nanoPerFrame;
    }
    
    @Override
    public String toString() {
        return "VisualEffect{name=" + this.getClass().getName() +
                ",nanoPerFrame=" + nanoPerFrame +
                '}';
    }
}
