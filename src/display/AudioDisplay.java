package display;

import audio.loaded.LoadedFile;
import audio.loaded.VisualEffect;
import audio.file.AudioFile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Paul Lancaster on 28/11/2016
 */
public class AudioDisplay extends JFrame {
    private AudioDisplayPanel panel;
    private ArrayList<LoadedFile> queue = new ArrayList<>();
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
        for (LoadedFile song : queue) {
            play(song);
            queue.remove(song);
        }
    }
    
    // Play the given audio file
    // Return true if successful
    // False if not (file currently playing, use que file instead)
    // Blocking by default
    private void play(LoadedFile file){
        if (!file.isLoaded()){
            file.load(CHUNK_SIZE);
        }
        panel.play(file);
    }
    
    public boolean queueFile(AudioFile file, VisualEffect effect){
        queue.add(new LoadedFile(file,effect));
        return true; // Queued successfully
    }
}
