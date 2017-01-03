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
        int[] data = powerSpectrums[pos];
        
        final int PADDING = 1;
    
        // The height must be even so this adds a pixel to ensure that
        int CENTER_Y = height/2;
        
        g2d.drawLine(0,height/2, width,height/2); // Center line
        
        int maxAmplitude = (int)Math.ceil(data[getMaxAmpIndex(data)]);
        double scaleFactor;
        if (maxAmplitude == 0){
            scaleFactor = 1;
        }else {
            scaleFactor = (height - PADDING * 2) / (2 * maxAmplitude);
        }
        
        for (int x = 0; x < data.length; x++) {
            int y = (int) Math.abs((data[x] * scaleFactor)); // Scale the values down
            y = height - y; // Inverse the display so peaks are peaks rather than troughs
            y = y - height/2; // Bring into center
            g2d.drawLine(x,CENTER_Y,x,y);
        }
        pos++;
    }
    
    private static int getMaxAmpIndex(int[] data) {
        int max = data[0];
        int maxIndex = 0;
        for (int i = 1; i < data.length; i++) {
            if (max < Math.abs(data[i])){
                maxIndex = i;
                max = Math.abs(data[i]);
            }
        }
        return maxIndex;
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
