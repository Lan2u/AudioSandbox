package audio.effects;

import audio.file.AudioFile;

import java.awt.*;

/**
 * Created by Paul Lancaster on 31/12/2016
 */
public class AmplitudeNumberEffect extends VisualEffect{
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */

    public AmplitudeNumberEffect(AudioFile file, int chunkSize){
        super(file); // Calls the load specific details and the calculate nano seconds methods
        
        
    }
    
    // EFFECT SETTINGS / DETAILS METHODS //
    
    /**
     * Load effect specific details/settings using the file
     * The nano seconds per frame is special and loaded in a different method because it is the absolute minimum needed
     *
     * @param file The file to load the settings from. This should almost always be the file that is already encapsulated
     *             within this visual effect class
     */
    @Override
    protected void loadSpecificDetails(AudioFile file) {
        
        
    }
    
    // TODO used as part of the
    @Override
    long calcMinNanoPerFrame(AudioFile file){
        
        return 0L;
    }
    
    // DRAW EFFECT METHODS //
    
    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {
        
    }
    
    /**
     * Load effect specific details/settings using the file
     * The nano seconds per frame is special and loaded in a different method because it is the absolute minimum needed
     *
     * @param file The file to load the settings from. This should almost always be the file that is already encapsulated
     *             within this visual effect class
     */
}
