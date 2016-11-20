package graphic.display.drawpanels.dynamic;

import audio.files.AudioFile;
import audio.files.WaveFile;
import audio.transforms.AudioDataTransformation;

/**
 * Created by Paul Lancaster on 18/11/2016
 */
public class FrequencyDisplayPanel extends DynamicDrawPanel{
    AudioFile audioFile;
    
    FrequencyDisplayPanel(int WIDTH, int HEIGHT, AudioFile audioFile) {
        super(WIDTH, HEIGHT);
        this.audioFile = audioFile;
    }
    
    
    long lastTime = System.nanoTime();
    @Override
    public void run() {
        int CHUNK_SIZE = 1024;// Size of each chunk in samples
        long CHUNK_TIME = audioFile.getSampleRate()/CHUNK_SIZE;
        CHUNK_TIME  = (long) (CHUNK_TIME * Math.pow(10,9)); // Time per chunk in nano seconds
        
        boolean running = true;
        long deltaT = 0;
        while (running) {
            long currentTime = System.nanoTime();
            deltaT = deltaT + currentTime - lastTime;
            if (deltaT >= CHUNK_TIME) {
                int[] chunk = audioFile.getChunk(CHUNK_SIZE);
                
                System.out.println(AudioDataTransformation.getFrequencyOfChunk(audioFile.getChunk(CHUNK_SIZE)));
            }
    
            lastTime = currentTime;
        }
    }
}
