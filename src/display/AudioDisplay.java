package display;

import audio.files.AudioFile;

import java.util.ArrayList;

/**
 * Created by Paul Lancaster on 28/11/2016
 */
public class AudioDisplay {
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
        
    }
    
    public boolean queueFile(AudioFile file, VisualEffect effect){
        
    }
    
}
