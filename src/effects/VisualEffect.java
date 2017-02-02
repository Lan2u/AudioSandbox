package effects;

import audio.file.AudioFile;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

/**
 * Created by Paul Lancaster on 31/12/2016 01:32
 *
 * The parent class of all visual effects
 */
public abstract class VisualEffect extends AnimationTimer{
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
    public boolean drawNextFrame(GraphicsContext gc2d, long deltaT){
        if (deltaT > minimumNanoPerFrame){
            drawEffect(gc2d, deltaT);
            return true;
        }else {
            return false;
        }
    }
        
    /**
     * The method that the effect is actually drawn in and is called each frame
     * @param gc2d The graphics context to draw the effect too
     * @param deltaT The time difference since the last frame was displayed
     */
    abstract void drawEffect(GraphicsContext gc2d, long deltaT);
    
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
    
    @Override
    public abstract void handle(long now);
    
    public abstract void play(GraphicsContext gc);
}