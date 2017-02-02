package effects;

import audio.file.AudioFile;
import calculate.FreqCalculator;
import javafx.scene.canvas.GraphicsContext;

/**
 * Created by Paul Lancaster on 02/02/2017
 */
public class TurbineEffect extends VisualEffect{
    private final int CHANNEL; // Audio channel
    private final int APPROXIMATE_CHUNK_SIZE; // In samples
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
    TurbineEffect(AudioFile file, int chunkSize, int channel) {
        super(file);
        this.APPROXIMATE_CHUNK_SIZE = chunkSize;
        if (file.getChannels() < channel){
            throw new IllegalArgumentException("Invalid channel for " + toString() + " : " + channel);
        }
        this.CHANNEL = channel;
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
    @Override
    long calcMinNanoPerFrame(AudioFile file) {
        double seconds = ((double)file.getSampleRate()/APPROXIMATE_CHUNK_SIZE);
        return FreqCalculator.secondsToNano(seconds);
    }
    
    /**
     * Called when the effect is started
     *
     * @see VisualEffect#play(GraphicsContext gc)
     */
    @Override
    public void start() {
        
    }
    
    /**
     * Checks if there is another frame of the effect left to display
     *
     * @return True if the effect has another frame and false if not
     */
    @Override
    public boolean hasNextFrame() {
        return false;
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
