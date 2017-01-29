package effects;

import audio.file.AudioFile;
import calculate.oldFreqCalculator;

import java.awt.*;

/**
 * Created by Paul Lancaster on 03/01/2017
 */

// https://dzone.com/articles/exploring-html5-web-audio
    // TODO Change it so that the number of displayed bars directly corresponds to the bin size from the FFT so that the FFT doesn't have to be larger than necessary
public class FrequencyLog10PowerSpectrumPlotEffect extends VisualEffect {
    private int chunk_size;
    private int[][] powerSpectrums;
    private int pos;
    
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
    
    /**
     * Calculate the minimum amount of nano seconds that a frame should be displayed for
     * @param file The file containing the information used to calculate the value
     * @return The calculated minimum amount of nano seconds that a frame should be displayed for
     */
    private long calcMinNanoPerFrame(AudioFile file) {
        return 1000000000L * chunk_size /file.getSampleRate();
    }
    
    /**
     * Calculate all the frequency spectrums for each chunk
     * @param file The file to check the chunks from
     * @param channel The channel on which to get the chunks from
     */
    private void calcPowerSpectrums(AudioFile file, CHANNEL channel) {
        int spectrumCount = file.getNumberOfSamples()/ chunk_size;
        powerSpectrums = new int[spectrumCount][];
        for (int i = 0; i < spectrumCount; i++) {
            int[] chunk = file.getChunk(chunk_size,channel.getInt());
            powerSpectrums[i] = getPowerSpectrum(chunk);
        }
        
    }
    
    /**
     * @param chunk The chunk to get the power spectrum of
     * @return The power spectrum (magnitude) of the chunk
     */
    private int[] getPowerSpectrum(int[] chunk){
        double[] magnitude = oldFreqCalculator.getMagPowerSpectrum(chunk);
        magnitude = oldFreqCalculator.log10Array(magnitude);
        return doubleToInt(magnitude);
    }
    
    /**
     * Converts a double array into an int array with the values rounded to the nearest integer (rounding up if half way between)
     * @param array The double array to convert
     * @return The converted int array of the double array
     */
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
    
    /**
     * Returns the index of the highest magnitude value in the array (ignoring the sign of the value so -2 is greater than 1 in this case)
     * @param data The array of data to scan for the highest magnitude value
     * @return The index of the highest magnitude value regardless of sign
     */
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
