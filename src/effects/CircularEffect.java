package effects;

import audio.file.AudioFile;

import java.awt.*;

/**
 * Created by paul on 23/01/17.
 */
public class CircularEffect extends VisualEffect{
    int PADDING = 10;
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    CircularEffect(AudioFile file) {
        super(file);
    }

    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {
        int x = (int)(width /2.0);
        int y = (int)(height/2.0);
        int diameter = (int)(width/2.0) - PADDING;
        if (diameter <= 0){
            diameter = 1;
        }
        g2d.fill(x,y,diameter,diameter);
    }

    @Override
    public boolean hasNextFrame() {
        return false;
    }

    @Override
    public String getName() {
        return "Circle Effect";
    }

    @Override
    public void finish() {

    }
}
