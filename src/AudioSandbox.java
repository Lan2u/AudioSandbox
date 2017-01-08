import audio.file.Mp3File;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 30/10/2016
 */

    
public class AudioSandbox {
    private static final String FILE_PATH = "resources/audiocheck.net_sweep_10Hz_20000Hz_-3dBFS_4s.wav";
    
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        Mp3File file = new Mp3File(new File(FILE_PATH));
        
    }
    
    /* Correct usage of the visual effects with a audio file using amplitude number effect as an example
        AudioFile file = new AudioFile(new File(FILE_PATH));
        AudioDisplay display = new AudioDisplay();
        int chunkSize = 512;
        VisualEffect ampNumEffect = new AmplitudeNumberEffect(waveFile, chunkSize);
        display.play(effect);
        
        or alternatively
        
        int chunkSize = 512;
        VisualEffect ampNumEffect = new AmplitudeNumberEffect(waveFile, chunkSize);
        display.queue(effect);
        queue more effects...
        display.play(); start playback (blocking)
        
        alternatively
        int chunkSize = 512;
        VisualEffect ampNumEffect = new AmplitudeNumberEffect(waveFile, chunkSize);
        display.queue(effect);
        queue more effects...
        TODO display.playNoBlock(); start playback
        TODO diplay.queue(effect) queue doing playback
     */
}