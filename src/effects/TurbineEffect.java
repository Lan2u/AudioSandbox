package effects;

import audio.file.AudioFile;
import javafx.scene.canvas.GraphicsContext;

/**
 * Created by Paul Lancaster on 02/02/2017
 */
public class TurbineEffect extends VisualEffect{
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    TurbineEffect(AudioFile file) {
        super(file);
        
    }
    
    /**
     * The method that the effect is actually drawn in and is called each frame
     *
     * @param gc2d   The graphics context to draw the effect too
     * @param deltaT The time difference since the last frame was displayed
     */
    @Override
    void drawEffect(GraphicsContext gc2d, long deltaT) {
        
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
     * @return The english name of the effect
     */
    @Override
    public String getName() {
        return null;
    }
    
    /**
     * Called when the effect finishes
     */
    @Override
    public void finish() {
        
    }
    
    @Override
    public void handle(long now) {
        
    }
    
    @Override
    public void play(GraphicsContext gc) {
        
    }
}
