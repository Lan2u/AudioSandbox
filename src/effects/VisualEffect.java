package effects;

import audio.file.AudioFile;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

/**
 * Created by Paul Lancaster on 31/12/2016 01:32
 * <p>
 * The parent class of all visual effects
 */
public abstract class VisualEffect extends AnimationTimer {
    /**
     * The number of nano seconds that each frame should be displayed for ideally (in actual fact it will
     * just be the minimum because if the next frame takes longer to load then the time each frame
     * is displayed for will increase (and FPS will decrease))
     */
    private long lastFrame = -1L;
    protected long timeSinceLastFrame = 0;
    
    private GraphicsContext graphicsContext;
    private Dimension dimensions;
    
    /**
     * The audio file that is used within the visual effect
     */
    AudioFile audioFile;
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    VisualEffect(AudioFile file) {
        this.audioFile = file;
        file.resetPos();
    }
    
    public final void play(GraphicsContext gc, Dimension dimensions) {
        graphicsContext = gc;
        this.dimensions = dimensions;
        this.start();
    }
    
    abstract boolean nextFrameReady(long deltaT);
    
    @Override
    public final void handle(long now) {
        if (lastFrame == -1) { // Handle the first time the handle method is ran
            lastFrame = now;
        }
        
        if (nextFrameReady(Math.abs(now - lastFrame))) {
            drawEffect(graphicsContext, timeSinceLastFrame);
            timeSinceLastFrame = 0;
        }
        
        lastFrame = now;
    }
    
    /**
     * Called when the effect finishes
     */
    public final void finish() {
        stop();
    }
    
    public abstract void begin();
    
    /**
     * Checks if there is another frame of the effect left to display
     *
     * @return True if the effect has another frame and false if not
     */
    public abstract boolean hasNextFrame();
    
    /**
     * Draw the effect
     *
     * @param gc2d   The graphics context to draw the effect to (this gets passed back up to the canvas)
     * @param deltaT the time in nano seconds since the last frame was played
     */
    // Called to draw the effect each frame
    abstract void drawEffect(GraphicsContext gc2d, long deltaT);
    
    /**
     * Called when the effect is finished or stopped
     *
     * @see VisualEffect#finish()
     */
    @Override
    public abstract void stop();
    
    protected Dimension getDimensions() {
        return dimensions;
    }
}