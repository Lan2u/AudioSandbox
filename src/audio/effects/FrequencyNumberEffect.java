package audio.effects;

import audio.file.AudioFile;
import calculate.FreqCalculator;

import java.awt.*;

/**
 * Created by Paul Lancaster on 02/01/2017
 */
public class FrequencyNumberEffect extends VisualEffect{
    private int chunk_size;
    private int[] freq_ch1;
    private int[] freq_ch2;
    private int pos = 0;
    
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    public FrequencyNumberEffect(AudioFile file, int chunk_size) {
        super(file);
        this.chunk_size = chunk_size;
        calcFrequencies(file);
        System.out.println("Calculations finished");
        minimumNanoPerFrame = calcMinNanoPerFrame(file);
        System.out.println("Minimum Nano Per Frame " +minimumNanoPerFrame);
    }
    
    private void calcFrequencies(AudioFile file) {
        int chunkCount = (file.getNumberOfSamples() / chunk_size) + 1;
        switch (file.getChannels()){
            case 2:
                freq_ch2 = new int[chunkCount];
                for (int i = 0; i < chunkCount; i++) {
                    freq_ch2[i] = FreqCalculator.getPrimaryFreqOfChunk(file.getChunk(chunk_size,2),file.getSampleRate());
                }
            case 1:
                freq_ch1 = new int[chunkCount];
                for (int i = 0; i < chunkCount; i++) {
                    freq_ch1[i] = FreqCalculator.getPrimaryFreqOfChunk(file.getChunk(chunk_size,1), file.getSampleRate());
                }
                break;
        }
    }
    
    private long calcMinNanoPerFrame(AudioFile file) {
        // long amount = sampleRate/chunk_size
        // 1/ amount = seconds per chunk
        // seconds per chunk * 1000000000 = nano seconds per chunk
        // nanoseconds per chunk = 1000000000L * chunk_size /sampleRate
        return 1000000000L * chunk_size /file.getSampleRate();
    }
    
    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {
        String str = "Channel 1 : " + freq_ch1[pos];
        if (audioFile.getChannels() > 1){
            str = str + " , Channel 2 : " + freq_ch2[pos];
        }
        g2d.drawString(str,0,height/2);
        pos++;
    }
    
    @Override
    public boolean hasNextFrame() {
        return pos < freq_ch1.length;
    }
    
    @Override
    public String getName() {
        return "Frequency Number Display";
    }
    
    @Override
    public void finish() {
        audioFile.resetPos();
        System.out.println(getName() + " effect finished");
    }
}
