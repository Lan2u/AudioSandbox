package display;

import audio.effects.VisualEffect;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Paul Lancaster on 28/11/2016
 */
public class AudioDisplay extends JFrame {
    private AudioDisplayPanel panel;
    private ArrayList<VisualEffect> queue = new ArrayList<>();
    private int CHUNK_SIZE = 100; // Samples per chunk
    
    /*
        Load the data for the visuals before and then just translate that to something visual as each frame plays
    */
    public AudioDisplay(){
        this(1000,1000);
    }
    
    private AudioDisplay(int width, int height){
        setSize(width, height);
        setMaximumSize(new Dimension(width+1,height));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        panel = new AudioDisplayPanel(width,height);
        add(panel);
        setVisible(true);
    }
    
    // Plays current song queue, plays nothing if the queue is empty
    public void play(){
        // This could be done better to link the effect and queue together
        for (VisualEffect song : queue) {
            play(song);
            queue.remove(song);
        }
    }
    
    // Play the given audio file
    // Return true if successful
    // False if not (file currently playing, use queue file instead)
    // Blocking by default
    public void play(VisualEffect file){
        panel.play(file);
    }
    
    public boolean queueFile(VisualEffect effect){
        queue.add(effect);
        return true; // Queued successfully
    }
}
