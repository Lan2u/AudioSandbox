package effects;

import audio.file.AudioFile;

import java.awt.*;

/**
 * Created by Paul Lancaster on 31/12/2016 01:32
 *
 * The parent class of all visual effects
 */
public abstract class VisualEffect{
    /**
     * The number of nano seconds that each frame should be displayed for ideally (in actual fact it will
     * just be the minimum because if the next frame takes longer to load then the time each frame
     * is displayed for will increase (and FPS will decrease))
     */
    long minimumNanoPerFrame = 0;
    /**
     * The audio file that is used within the visual effect
     */
    AudioFile audioFile;
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    VisualEffect(AudioFile file) {
        this.audioFile = file;
        file.resetPos();
    }
    
    // This should be the only public method for drawing frames the other methods are internal
    public boolean drawNextFrame(Graphics2D g2d, int width, int height, long deltaT){
        if (deltaT > minimumNanoPerFrame){
            drawEffect(g2d, width, height, deltaT);
            return true;
        }else {
            return false;
        }
    }
        
    /**
     * The method that the effect is actually drawn in and is called each frame
     * @param g2d The Graphics2D object to actually draw the effect on
     * @param width The width of the frame/object that the effect will be drawn on
     * @param height The height of the frame/object that the effect will be drawn on
     * @param deltaT The time difference since the last frame was displayed
     */
    protected abstract void drawEffect(Graphics2D g2d, int width, int height, long deltaT);
    
    /**
     * Checks if there is another frame of the effect left to display
     * @return True if the effect has another frame and false if not
     */
    public abstract boolean hasNextFrame();
    
    /**
     * @return The english name of the effect
     */
    abstract public String getName();
    
    /**
     * Called when the effect finishes
     */
    public abstract void finish();
}