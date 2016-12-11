package display;

import audio.files.AudioFile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Paul Lancaster on 28/11/2016
 */
public class AudioDisplay extends JFrame {
    
    AudioDisplayPanel panel;
    
    /*
        3 ways to do this either:
       - Load the visuals dynamically as the music plays frame by frame
*this* - Load the data for the visuals before and then just translate that to something visual as each frame plays
       - Load the visuals before (every frame) then play theses
     */
    public AudioDisplay(){
        this(1000,1000);
    }
    public AudioDisplay(int width,int height){
        setSize(width, height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        panel = new AudioDisplayPanel(width,height);
        add(panel);
        setSize(new Dimension(WIDTH+100,HEIGHT+100));
        setVisible(true);
    }
    
    
    ArrayList<AudioFile> queue = new ArrayList<>();
    ArrayList<VisualEffect> effects = new ArrayList<>();
    
    // Plays current song queue, plays nothing if the queue is empty
    public void play(){
        // This could be done better to link the effect and queue together
        for (int i = 0; i < queue.size(); i++) {
            play(queue.get(i),effects.get(i));
        }
    }
    
    // Play the given audio file
    // Return true if successful
    // False if not (file currently playing, use que file instead)
    // Blocking by default
    public void play(AudioFile file, VisualEffect effect){
        panel.play(file,effect);
    }
    
    public boolean queueFile(AudioFile file, VisualEffect effect){
        queue.add(file);
        effects.add(effect);
        return true; // Queued successfully
    }
    
}
