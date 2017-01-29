package effects;

import audio.file.AudioFile;
import calculate.oldFreqCalculator;

import java.awt.*;

/**
 * Created by Paul Lancaster on 03/01/2017
 */
public class PrimaryFrequencyDisplay extends VisualEffect {
    private int chunk_size;
    private int[] maxFrequencies;
    private int pos;
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    public PrimaryFrequencyDisplay(AudioFile file, int chunkSize, CHANNEL channel) {
        super(file);
        this.chunk_size = chunkSize;
        calcPrimaryFreqs(file,chunkSize,channel);
        minimumNanoPerFrame = calcMinNanoPerFrame(file);
        pos=0;
    }
    
    private long calcMinNanoPerFrame(AudioFile file) {
        return 1000000000L * chunk_size /file.getSampleRate();
    }
    
    private void calcPrimaryFreqs(AudioFile file, int chunkSize, CHANNEL channel) {
        int chunkCount = file.getNumberOfSamples()/chunkSize;
        maxFrequencies = new int[chunkCount];
        for (int i = 0; i < chunkCount; i++) {
            int[] chunk = file.getChunk(chunk_size, channel.getInt());
            maxFrequencies[i] = oldFreqCalculator.getPrimaryFreqOfChunk(chunk, file.getSampleRate());
        }
    }
    
    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {
        final int bands = 20;
        int bandWidth = width/bands;
        int freqBandWidth = audioFile.getSampleRate()/ (2 *bands);
        int bandHeight = 60;
        
        g2d.drawLine(0,height/2, width,height/2); // Center line
        
        int band = maxFrequencies[pos] / freqBandWidth;
        g2d.setColor(Color.GREEN);
        g2d.fillRect(band*bandWidth,height/2 - bandHeight,bandWidth, bandHeight);
        pos++;
    }
    
    @Override
    public boolean hasNextFrame() {
        return pos < maxFrequencies.length;
    }
    
    @Override
    public String getName() {
        return "Frequency Log10 Power Spectrum Plot";
    }
    
    @Override
    public void finish() {
        audioFile.resetPos();
        System.out.println(getName() + " effect has finished");
    }
}
