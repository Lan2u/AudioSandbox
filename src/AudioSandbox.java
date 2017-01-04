import audio.file.WaveFile;
import display.AudioDisplay;
import effects.CHANNEL;
import effects.StringFreqEffect;
import effects.VisualEffect;

import java.io.File;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 30/10/2016
 */

    
public class AudioSandbox {
    private static final String FILE_PATH = "resources/440HzTo20KHzLinear.wav";
    
    public static void main(String[] args) throws IOException {
        WaveFile waveFile = new WaveFile(new File(FILE_PATH));
        
        System.out.println(waveFile);
        AudioDisplay display = new AudioDisplay();
        
        int chunkSize = 1024;
        int height = 1000;
        VisualEffect freqNumEffect = new StringFreqEffect(waveFile,chunkSize,height,CHANNEL.one);
        
        display.queue(freqNumEffect);
        display.play();
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