package display;

import audio.files.loaded.LoadedFile;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Paul Lancaster on 28/11/2016
 */
public class AudioDisplayPanel extends JPanel{
    private BufferedImage frame; // Display frame
    private static long timeSinceLastFrame = 0; // Nanoseconds
    
    AudioDisplayPanel(int width, int height) {
        frame = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        setSize(width, height);
    }
    
    void play(LoadedFile file) {
        file.setSize(this.getSize());
        
        long lastTime = System.nanoTime();
        while (file.hasNextFrame()){
            long currentTime = System.nanoTime();
            if (file.nextFrame(frame, currentTime-lastTime)){
                repaint();
            }
            lastTime = currentTime;
        }
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
       // System.out.println("Painted");
        g.drawImage(frame,0,0,Color.PINK, this);
    }
    
    
    //    1   So the image updates continously between 10 and 30 times a second
    //    2   The chunk size is a factor of sample rate * time of each frame(seconds)
    //    3   so at 10fps and sample rate 44100
    //    4   chunksize is a factor of 4410 eg. 3 or 5 although preferably a number which gives the highest efficiency (need to research)
    //    5   So for each the frequencies of all the chunks in the frame is worked out and the corresponding amplitude at that frequency
    //    6   The highest amplitude at each frequency band (There will be 20 bands for now but the number of bands can increase/decrease)
    //    7   All bands contain cover an equivalent number of frequencies each
    //    8   The highest amplitude at that band will then be displayed as a bar
    //    9   This will update each frame as the song moves along.
    //   10   For now just channel 1 will be displayed but channel 2 will be added later and displayed in a different colour
}
