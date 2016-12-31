package audio.effects;

import audio.file.AudioFile;

import java.awt.*;

/**
 * Created by Paul Lancaster on 31/12/2016 01:32
 */
public abstract class VisualEffect{
    /**
     * The number of nano seconds that each frame should be displayed for ideally (in actual fact it will
     * just be the minimum because if the next frame takes longer to load then the time each frame
     * is displayed for will increase (and FPS will decrease))
     */
    protected long minimumNanoPerFrame = 0;
    /**
     * The audio file that is used within the visual effect
     */
    protected AudioFile audioFile;
    /**
     * Draw the next frame of the visual effect onto the supplied graphics and then return true however if the time
     * difference is too small for a frame update then nothing is drawn and instead the method returns false to
     * signify that a frame update was not necessary
     *
     * @param g2d The graphics onto which the effect should be drawn
     * @param width The width of the frame
     * @param height The height of the frame
     * @param deltaT The time in nano seconds since the last frame was displayed
     * @return true if the frame was updated or false if it wasn't
     */
    
    // This should be the only public method for drawing frames the other methods are internal
    public boolean drawNextFrame(Graphics2D g2d, int width, int height, long deltaT){
        if (deltaT > minimumNanoPerFrame){
            drawEffect(g2d, width, height, deltaT);
            return true;
        }else {
            return false;
        }
    }
    
    // TODO used as part of the
    abstract long calcMinNanoPerFrame(AudioFile file);
    
    protected abstract void drawEffect(Graphics2D g2d, int width, int height, long deltaT);
        
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    VisualEffect(AudioFile file) {
        this.audioFile = file;
        minimumNanoPerFrame = calcMinNanoPerFrame(file);
    }
    
    public long getNanoPerFrame(){
        return minimumNanoPerFrame;
    }
    
    public abstract boolean hasNextFrame();
}