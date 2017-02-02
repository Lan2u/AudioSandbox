package display;

import effects.VisualEffect;
import javafx.scene.canvas.Canvas;

/**
 * Created by Paul Lancaster on 28/11/2016
 *
 * For displaying visualizer effects
 */

public class VisualizerCanvas extends Canvas{
   // private BufferedImage frame; // Display frame
    
    VisualizerCanvas(int width, int height) {
        super(width,height);
       // frame = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
       // setSize(width, height);
    }
    
    void play(VisualEffect effect){
        
        
        long timeSinceLastFrame = 0;
        long lastTime = System.nanoTime();
    
        while (effect.hasNextFrame()){
            long currentTime = System.nanoTime();
            timeSinceLastFrame += Math.abs(currentTime - lastTime);
            frameUpdate(timeSinceLastFrame, effect);
            lastTime = currentTime;
        }
        
        effect.finish();
    }
    
    /**
     * Where the frame is updated (called from the game loop);
     */
    private void frameUpdate(long deltaT, VisualEffect effect) {
        effect.drawNextFrame(this.getGraphicsContext2D(), deltaT);
    }
    
    /*
     * This is where the effect is actually triggered from this is the "game loop" while the effect is playing
     * @param effect to play
     *
    void play(VisualEffect effect) {
        long timeSinceLastFrame = 0;
        long lastTime = System.nanoTime();
        
        while (effect.hasNextFrame()){
            BufferedImage nextFrame = new BufferedImage(frame.getWidth(),frame.getHeight(),frame.getType());
            long currentTime = System.nanoTime();
            timeSinceLastFrame += Math.abs(currentTime - lastTime);
            if (effect.drawNextFrame(nextFrame.createGraphics(), nextFrame.getWidth(),nextFrame.getHeight(), timeSinceLastFrame)) {
                frame = nextFrame;
                repaint();
                timeSinceLastFrame = 0;
            }
            lastTime = currentTime;
        }
        effect.finish();
    }

    public void paintComponent(Graphics g){
        g.drawImage(frame,0,0,Color.PINK, this);
    }

    private ArrayList<VisualEffect> queue = new ArrayList<>();

    // Plays current song queue, plays nothing if the queue is empty
    public void play(){
        // This could be done better to link the effect and queue together
        for (VisualEffect aQueue : queue) {
            play(aQueue);
        }
        queue.clear();
    }


    public boolean queue(VisualEffect effect){
        queue.add(effect);
        return true; // Queued successfully
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
    */
}
