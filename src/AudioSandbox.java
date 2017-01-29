import audio.file.WaveFile;
import display.EffectDisplay;
import effects.CircularEffect;
import effects.VisualEffect;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

/**
 * Created by Paul Lancaster on 30/10/2016
 */


public class AudioSandbox {
    private static final String FILE_PATH = "resources/TutTutChild-Monstercat-TheBestof2013-24Hummingbird(feat.AugustusGhost).mp3";
    
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        /*
        Mp3File file = new Mp3File(new File(FILE_PATH));
        EffectDisplay display = new EffectDisplay();
        int chunkSize = 512;
        VisualEffect effect = new LagFreqEffect(file, 1000, CHANNEL.one,100);
        display.queue(effect);
        display.play();*/
        
        
        WaveFile file = new WaveFile(new File("resources/high_risk.wav"));
        System.out.println(file);
        EffectDisplay display = new EffectDisplay();
        VisualEffect effect = new CircularEffect(file, 3, 4, 10, 10, 50);
        display.queue(effect);
        display.play();
        
    }
    
    /* Correct usage of the visual effects with a audio file using amplitude number effect as an example
        AudioFile file = new AudioFile(new File(FILE_PATH));
        EffectDisplay display = new EffectDisplay();
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