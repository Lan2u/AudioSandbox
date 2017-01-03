package audio.effects;

import audio.file.AudioFile;
import calculate.FreqCalculator;

import java.awt.*;

/**
 * Created by Paul Lancaster on 03/01/2017
 */
public class FrequencyLog10PowerSpectrumPlotEffect extends VisualEffect {
    int chunk_size;
    int[][] powerSpectrums;
    int pos;
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    public FrequencyLog10PowerSpectrumPlotEffect(AudioFile file, int chunkSize) {
        super(file);
        this.chunk_size = chunkSize;
        calcPowerSpectrums(file, CHANNEL.one);
        minimumNanoPerFrame = calcMinNanoPerFrame(file);
    }
    
    private long calcMinNanoPerFrame(AudioFile file) {
        return 1000000000L * chunk_size /file.getSampleRate();
    }
    
    private void calcPowerSpectrums(AudioFile file, CHANNEL channel) {
        int spectrumCount = file.getNumberOfSamples()/ chunk_size;
        powerSpectrums = new int[spectrumCount][];
        for (int i = 0; i < spectrumCount; i++) {
            int[] chunk = file.getChunk(chunk_size,channel.getInt());
            powerSpectrums[i] = getPowerSpectrum(chunk);
        }
        
    }
    
    private int[] getPowerSpectrum(int[] chunk){
        double[] magnitude = FreqCalculator.getMagPowerSpectrum(chunk);
        magnitude = FreqCalculator.log10Array(magnitude);
        return doubleToInt(magnitude);
    }
    
    private int[] doubleToInt(double[] array) {
        int[] out = new int[array.length];
        for (int i = 0; i <array.length; i++) {
            out[i] = (int)Math.round(array[i]);
        }
        return out;
    }
    
    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {
        int bands = powerSpectrums[0].length;
        int bandWidth = width / bands; // FIXME what if there are more bands than the width pixel
        for (int band = 0; band < bands; band++) {
            g2d.setColor(Color.CYAN);
            int bandHeight = powerSpectrums[pos][band];
            int x = bands * bandWidth;
            /*
            if (((bandHeight - (height/2)) < 0) || (bandHeight + height/2) > height){
                bandHeight = height/2;
                g2d.setColor(Color.GREEN);
            }
            */
            g2d.fillRect(x, (height / 2) - bandHeight, bandWidth, bandHeight);
        }
        pos++;
    }
    
    @Override
    public boolean hasNextFrame() {
       return pos < powerSpectrums.length;
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
