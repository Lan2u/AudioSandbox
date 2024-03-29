package effects;

import audio.file.AudioFile;
import calculate.FreqCalculator;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;
import java.util.*;

/**
 * Created by Paul Lancaster on 02/02/2017
 */
public class TurbineEffect extends VisualEffect{
    private final int FREQ_COUNT;
    private long minimumNanoPerFrame;
    
    // The number of frequencies from each chunk to register.
    // The bigger the value the more frequencies detected and included (reduces the filter)
    
    private final int CHANNEL; // Audio channel
    private final int APPROXIMATE_CHUNK_SIZE; // In samples
    private final double SAMPLE_RATE;
    private double MAX_BAR_HEIGHT = 400; // TODO make this dynamic (not set statically)
    // The chunksize that this effect is going to attempt to use
    // (the minimum nanoseconds per frame is calculated off of this)
    // This is approximate because the chunksize depends on deltaT and
    // that can only be controlled so that it is more than a certain value
    // but not less
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    public TurbineEffect(AudioFile file, int chunkSize, int channel, int freqCount){
        super(file);
        
        checkInputsValid(file, chunkSize,channel,freqCount); // Throws illegal argument exception if they aren't
        
        this.APPROXIMATE_CHUNK_SIZE = chunkSize;
        this.CHANNEL = channel;
        this.FREQ_COUNT = freqCount;
        this.SAMPLE_RATE = file.getSampleRate();
        this.minimumNanoPerFrame = calcMinNanoPerFrame(file, chunkSize);
    }
    
    private boolean checkInputsValid(AudioFile file , int chunkSize, int channel, int freqCount){
        if (chunkSize < 2){
            throw new IllegalArgumentException("Invalid chunksize : " + chunkSize + " must be greater >= 2");
        }
        if (file.getChannels() < channel) {
            throw new IllegalArgumentException("Invalid channel for " + toString() + " : " + channel);
        }
            
        if (freqCount < 1) {
            throw new IllegalArgumentException("Invalid frequency count : " + freqCount + " frequency count must be > 0");
        }
        
        return true;
    }
    
    @Override
    boolean nextFrameReady(long deltaT) {
        timeSinceLastFrame = timeSinceLastFrame + deltaT;
        if (timeSinceLastFrame >= minimumNanoPerFrame) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Calculate the minimum number of nano seconds required before a frame change
     * This method gets called in the constructor to change the nanoSeconds required field
     * <p>
     * If this method returns 0 then the effect will update as fast as possible (likely not very useful)
     *
     * @param file The audio file which is behind the effect
     * @return The minimum number of nanoseconds per frame
     */
    long calcMinNanoPerFrame(AudioFile file, int chunkSize) {
        if (chunkSize <= 0){
            throw new IllegalArgumentException("The chunk size is less than 0 cannot play!");
        }
        double seconds = 1.0 / ((double)file.getSampleRate()/chunkSize);
        return FreqCalculator.secondsToNano(seconds);
    }
    
    /**
     * Called when the effect is started
     *
     *
     */
    @Override
    public void begin(){
        audioFile.resetPos(); // Reset the audio file to the start
        start();
    }
    
    /**
     * Checks if there is another frame of the effect left to display
     *
     * @return True if the effect has another frame and false if not
     */
    @Override
    public boolean hasNextFrame() {
        return audioFile.hasNextSamples(APPROXIMATE_CHUNK_SIZE, CHANNEL);
    }
    
    /**
     * Draw the effect
     *
     * @param gc2d   The graphics context to draw the effect to (this gets passed back up to the canvas)
     * @param deltaT the time in nano seconds since the last frame was played
     */
    @Override
    void drawEffect(GraphicsContext gc2d, long deltaT) {
        
        int[] chunk = audioFile.getSamples(FreqCalculator.nanoToSeconds(deltaT), CHANNEL);
        
        TreeMap<Integer,Double> frequencies = FreqCalculator.getChunkFrequencies(chunk, SAMPLE_RATE);
        
        Double maxFreq = getMax(frequencies.values());
        
        Dimension dimensions = this.getDimensions();
        
        double barWidth = dimensions.getWidth()/frequencies.size();
        
        System.out.println("Dimensions : " + dimensions.toString());
        System.out.println("Bar Width : " + barWidth);
        
        int y = 20; // Base y taken from the bottom
        
        for (int i = 0; i < frequencies.size(); i++) {
            int x = (int)(barWidth * i);
            int h = (int)(MAX_BAR_HEIGHT*(frequencies.get(i) / maxFreq));
            fillRectangle(gc2d, x, y, barWidth, h);
        }
    }
    
    private Double getMax(Collection<Double> collection) {
        Optional<Double> d = collection.stream().max(Double::compareTo);
        if (d.isPresent()){
            return d.get();
        }else{
            throw new ValueNotFoundException("The max value of the given collection couldn't be found");
        }
    }
    
    /**
     * Fills a rectangle but the x is the position of the bottom left corner
     *
     * @param gc2d
     * @param x
     * @param y
     * @param w
     * @param h
     */
    private void fillRectangle(GraphicsContext gc2d, double x, double y, double w, double h){
        y = y + h;
        gc2d.fillRect(x,y,w,h);
    }
    
    /**
     * Called when the effect is finished or stopped
     *
     * @see VisualEffect#finish()
     */
    @Override
    public void stop() {
        
    }
    
    @Override
    public String toString(){
        return "Turbine effect";
    }
}
