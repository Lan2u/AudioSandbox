package calculate;

import org.jtransforms.fft.DoubleFFT_1D;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Paul Lancaster on 14/12/2016
 */
/*
http://stackoverflow.com/questions/3058236/how-to-extract-frequency-information-from-samples-from-portaudio-using-fftw-in-c

 */
    

public abstract class FreqCalculator {
    
    /**
     * Perform FFT on the chunk of amplitudes passed in and then store the results in the 3 arrays
     * @param chunk The input chunk of amplitudes
     * @param real The output real component of the FFT on chunk
     * @param im The output imaginary component of the FFT on chunk
     * @param magnitude The magnitude of each component
     */
    private static void performFFT(int[] chunk, double[] real, double[] im, double[] magnitude){
        // TODO decide if we actually need to calculate and store the magnitude and imaginary components of the FFT out since the real is only used
        double[] decimalChunk = intToDoubleNoScale(chunk);
        
        // Build and apply window
        // buildHannWindow(chunk.length);
        // decimalChunk = applyWindow(decimalChunk,decimalChunk);
        
        new DoubleFFT_1D(chunk.length).realForward(decimalChunk);
        splitIntoParts(decimalChunk, real, im, magnitude);
    }
    
    // Chunk should be of length N (FFT SIZE)
    public static int getPrimaryFreqOfChunk(int[] chunk, int sampleRate){
        final int ARRAY_LENGTH = chunk.length/2;
        double[] real = new double[ARRAY_LENGTH];
        double[] im = new double[ARRAY_LENGTH];
        double[] magnitude = new double[ARRAY_LENGTH/2 + 1];
        performFFT(chunk,real,im,magnitude);
        return calcPrimaryFreq(real, chunk.length, sampleRate);
    }
    
    /**
     * Separate out the real and imaginary components from the FFT output and calculate the magnitude of each component
     * @param decimalChunk The FFT output array
     * @param real The array to store the real values (must have size >= decimalChunk.length/2)
     * @param im The array to store the imaginary values (must have size >= decimalChunk.length/2)
     * @param magnitude The array to store the magnitudes of each component (must have size >= decimalChunk.length/2)
     */
    private static void splitIntoParts(double[] decimalChunk, double[] real, double[] im, double[] magnitude) {
        int N = decimalChunk.length; // Used to improve readability
        
        if (decimalChunk.length % 2 == 0){ // Even
            for (int k = 0; k < (N/2); k++) { // Real
                real[k] = decimalChunk[2*k];
            }
            for (int k = 1; k < N/2; k++) { // Imaginary
                im[k] = decimalChunk[2*k + 1];
            }
            real[N/2 - 1] = decimalChunk[1];
        
        }else{ // Odd
            for (int k = 0; k < (N+1)/2; k++) { // Real
                real[k] = decimalChunk[2*k];
            }
            for (int k = 0; k < (N-1)/2; k++) { // Imaginary
                im[k] = decimalChunk[2*k +1];
            }
            im[(N-1)/2] = decimalChunk[1];
        }
        
        for (int i = 0; i < magnitude.length; i++) {
            magnitude[i] = Math.sqrt(real[i] * real[i] + im[i] * im[i]);
        }
    }
    
    /**
     * This method is used to plot data on a graph with a center line and negative values below and
     * positive values above. The graph is as wide as the amount of data so care is needed not to have too large a data array
     * @param height The height of the graph ( values are scaled to fit so use a bigger height for more obvious separation)
     * @param data The data to be plotted (suggest considering using a log function on the data if the range is high else it may not display)
     * @return The ploted graph
     */
    private static BufferedImage plotDataOnGraph(int height, double[] data) {
        final int PADDING = 1;
        int width = data.length;
        
        // The height must be even so this adds a pixel to ensure that
        if ((height % 2) != 0) height++;
        
        int CENTER_Y = height/2;
        BufferedImage plot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = plot.createGraphics();
        g2d.drawLine(0,height/2, width,height/2);
        
        int maxAmplitude = (int)Math.ceil(data[getMaxAmpIndex(data)]);
        double scaleFactor;
        if (maxAmplitude == 0){
            scaleFactor = 1;
        }else {
            scaleFactor = (height - PADDING * 2) / (2 * maxAmplitude);
        }
        
        int xOfMaxY = -1;
        int maxY = Integer.MIN_VALUE;
        for (int x = 0; x < data.length; x++) {
            int y = (int) Math.abs((data[x] * scaleFactor)); // Scale the values down
            if (y > maxY){
                maxY = y;
                xOfMaxY = x;
            }
            y = height - y; // Inverse the display so peaks are peaks rather than troughs
            y = y - height/2; // Bring into center
            g2d.drawLine(x,CENTER_Y,x,y);
        }
        System.out.println("Maximum y value found at x value = " + xOfMaxY);
        
        return plot;
    }
    
    /**
     * @param data The array to scan
     * @return The index of the value in the array which has the highest value regardless of sign so -5 is greater than 2 in this case
     */
    private static int getMaxAmpIndex(double[] data) {
        double max = data[0];
        int maxIndex = 0;
        for (int i = 1; i < data.length; i++) {
            if (max < Math.abs(data[i])){
                maxIndex = i;
                max = Math.abs(data[i]);
            }
        }
        return maxIndex;
    }
    
    private static int calcPrimaryFreq(double[] real, int N, int sampleRate) {
        int index = getMaxIndex(real);
        double binSize = sampleRate/N;
        return (int)Math.round(index * binSize);
    }
    
    public static double[] getRealPowerSpectrum(int[] chunk) {
        double[] real = new double[chunk.length/2];
        performFFT(chunk,real, new double[chunk.length/2],new double[chunk.length/2]);
        return real;
    }
    
    private static int getMaxIndex(double[] array) {
        double max = array[0];
        int maxI = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max){
                max = array[i];
                maxI = i;
            }
        }
        return maxI;
    }
    
    private static double[] intToDoubleNoScale(int[] array){
        double[] temp = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            temp[i] = array[i];
        }
        return temp;
    }
    
    // The array is changed to a double array with values newArray[i] = (SCALE_VALUE / array)
    
    
    /* Old classes used (other implementation used before) */
    private static final int MIN_DATA_CHUNK_SIZE = 2;
    
    private static BufferedImage getFreqDistributionPlot(int[] chunk, DOMAIN domain, int height){
        double[] decimalChunk = intToDoubleNoScale(chunk);
        
        DoubleFFT_1D dFF = new DoubleFFT_1D(chunk.length);
        dFF.realForward(decimalChunk);
        
        final int ARRAY_LENGTH = decimalChunk.length/2;
        double[] real = new double[ARRAY_LENGTH];
        double[] im = new double[ARRAY_LENGTH];
        double[] magnitude = new double[ARRAY_LENGTH/2 + 1];
        splitIntoParts(decimalChunk, real, im, magnitude);
        
        switch(domain){
            case REAL:
                return plotDataOnGraph(height,real);
            case IMAGINARY:
                return plotDataOnGraph(height,im);
            default:
                return plotDataOnGraph(height, magnitude);
        }
        
    }
    
    public static double[] log10Array(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.log10(array[i]);
        }
        return array;
    }
    
    private static double[] shortToDouble(short[] array) {
        double[] temp = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0){
                temp[i] = 0;
            } else {
                temp[i] = (Short.MAX_VALUE*2 / array[i]);
            }
            
            if(temp[i] > 1) System.out.println("ERROR DOUBLE VALUE GREATER THAN 1 : " + temp[i]); // Means that the amplitude was out of range
        }
        return temp;
    }
    
    private static double[] intToDouble(int[] array, int SCALE_VALUE) {
        double[] temp = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0.0) {
                temp[i] = 0.0;
            } else {
                temp[i] = (array[i] / SCALE_VALUE);
            }
        }
        return temp;
    }
    
    private void drawBands(Graphics2D g2d, double[] bandAmplitudes, Color colour, int BAND_WIDTH, int BASE_Y, int BAND_MAX_HEIGHT) {
        g2d.setColor(colour);
        for (int band = 0; band < bandAmplitudes.length; band++) {
            int x = band * BAND_WIDTH;
            int BAND_HEIGHT = (int) (BAND_MAX_HEIGHT * (bandAmplitudes[band] / 33000.0));
            int y = BASE_Y - BAND_HEIGHT;
            g2d.drawRect(x, y, BAND_WIDTH, BAND_HEIGHT);
        }
    }
    
    private static int getMaxIndex(int[] array){
        return getMaxIndex(intToDoubleNoScale(array));
    }
    
    private static void PerformFFT(ComplexNumber[] data, int N){
        
    }
    
    public static ComplexNumber DDT(short[] data, int sampleRate){
        // https://en.wikipedia.org/wiki/Discrete_Fourier_transform
        // http://www.librow.com/articles/article-10
        int N = data.length;
        double totalSum = 0.0;
        for (int n = 0; n <= N-1; n++){
            int xn = data[n];
            for (int k =0; k <= sampleRate/2; k++){
                // k = frequency being considered
                // See the wikipedia page
            }
        }
        return null;
    }
    
    //TODO testing I don't think this works properly
    private static short[] averageFilterSamples(short[] chunkSamples) {
        // Low-Pass filtering : Average out the frequency this will smooth out large peaks or troughs in the data
        // http://blog.bjornroche.com/2012/07/frequency-detection-using-fft-aka-pitch.html
        
        short[] filterOutput = new short[chunkSamples.length];
        short lastSample = chunkSamples[0];
        filterOutput[0] = chunkSamples[0];
        for (int i = 1; i < chunkSamples.length; i++) {
            filterOutput[i] = (short)((chunkSamples[i] + lastSample)/2);
            lastSample = filterOutput[i];
        }
        return filterOutput;
    }
    
    //TODO testing
    private static double[] buildHannWindow(int size) {
        double[] window = new double[size];
        for (int i = 0; i <size; i++) {
            window[i] = 0.5 * (1 - Math.cos(2 * Math.PI * i/ (size - 1.0)));
        }
        return window;
    }

    //TODO testing
    private static double[] applyWindow(double[] window, double[] samples) {
        for (int i = 0; i < samples.length; i++) {
            samples[i] = Math.round(samples[i] * window[i]);
        }
        return samples;
    }
    
    public static double[] getMagPowerSpectrum(int[] chunk) {
        double[] magnitude = new double[chunk.length/2];
        performFFT(chunk, new double[chunk.length/2],new double[chunk.length/2], magnitude);
        return magnitude;
    }
}