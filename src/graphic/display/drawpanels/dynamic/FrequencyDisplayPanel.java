package graphic.display.drawpanels.dynamic;

import audio.files.AudioFile;
import audio.transforms.AudioDataTransformation;

/**
 * Created by Paul Lancaster on 18/11/2016
 */
public class FrequencyDisplayPanel extends DynamicDrawPanel{
    AudioFile audioFile;
    
    public FrequencyDisplayPanel(int WIDTH, int HEIGHT, AudioFile audioFile) {
        super(WIDTH, HEIGHT);
        this.audioFile = audioFile;
    }
    
    
    
    @Override
    public void run() {
        int CHUNK_SIZE = 64;// Size of each chunk in samples
        double CHUNK_TIME = 1.0/(audioFile.getSampleRate()/CHUNK_SIZE);
        CHUNK_TIME  = (long) (CHUNK_TIME * Math.pow(10,9)); // Time per chunk in nano seconds
        double NUMBER_OF_CHUNKS = Math.ceil(audioFile.getNumberOfSamples() /CHUNK_SIZE);
        
        boolean running = true;
        long deltaT = 0;
        long lastTime = System.nanoTime();
        for (int i = 0; i < NUMBER_OF_CHUNKS; i++) {
            long currentTime = System.nanoTime();
            deltaT = deltaT + currentTime - lastTime;
            if (deltaT >= CHUNK_TIME) {
                double[] chunk = audioFile.getChunk(CHUNK_SIZE);
                System.out.println(AudioDataTransformation.getFrequencyOfChunk(chunk,audioFile.getSampleRate()));
                deltaT = 0;
            }
            
            lastTime = currentTime;
           // System.out.println(deltaT);
        }
        System.exit(0);
    }
}
