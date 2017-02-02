package oldeffects;

import audio.file.AudioFile;
import calculate.FreqCalculator;
import effects.VisualEffect;

import java.awt.*;

/**
 * An effect which displays a number of circles each representing a different frequency band theses circles
 * then expand depending on the amplitude of that frequency each frame. The frequency of each circle is
 * marked by a label in the circle
 * <p>
 * Created by paul on 23/01/17.
 *
 * @author Paul Lancaster
 */
public class CircularEffect extends VisualEffect {
    private static final double SETTLE_RATE = 0.5;
    private final int CHUNK_SIZE;
    private final int PADDING; // Minimum space between circles at any point (even if the 2 circles are fully expanded)
    private final int MIN_RADIUS;
    private final int MAX_RADIUS;
    private final int BAND_SIZE;
    private final long MIN_NANO_PER_EFFECT_UPDATE;
    private DisplayCircle[] displayCircles;
    private int channel = 1;
    private long dTSinceLastFrequency = 0;
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     * @param rows The number of rows of circles (rows * cols = number of circles displayed)
     */
    
    // FIXME The issue with this whole thing is that since the chunks are being taken in at only approximately the right time the effect will
    // very quickly end up running behind and so deltaT needs to be used when updating to make sure the correct number of chunks are taken in
    // the file.getSamples(seconds) should be able to help with this by using chunk = file.getSamples(deltaT);
    // Fix for this has been attempted
    public CircularEffect(AudioFile file, int rows, int cols, int padding, int minRadius, int maxRadius) {
        super(file);
        PADDING = padding;
        MIN_RADIUS = minRadius;
        MAX_RADIUS = maxRadius;
        BAND_SIZE = file.getSampleRate() / (rows * cols);
        CHUNK_SIZE = calcChunkSize(rows, cols, file.getSampleRate());
        MIN_NANO_PER_EFFECT_UPDATE = calcMinNanoPerEffectUpdate(CHUNK_SIZE, file.getSampleRate());
        calculateCirclePositions(rows, cols);
    }
    
    private long calcMinNanoPerEffectUpdate(int chunkSize, int sampleRate) {
        /*
        double samplesPerSecond = (1.0/sampleRate);
        double secondsPerChunk = chunkSize * samplesPerSecond;
        return secondsPerChunk * 1e9; */
        return (long) ((chunkSize * (1.0 / sampleRate)) * 1000000000);
    }
    
    
    private int calcChunkSize(int rows, int cols, int sampleRate) {
        /*
        For FFT
        http://electronics.stackexchange.com/questions/12407/what-is-the-relation-between-fft-length-and-frequency-resolution
        
        FFT resolution = sampleRate / chunkSize
        
        So if the FFT resolution == FREQ_BAND_SIZE then
        sampleRate / FFT resolution = ChunkSize
         */
        return sampleRate / (rows * cols);
    }
    
    
    private void calculateCirclePositions(int rows, int columns) {
        /* Example of circle layout with 3 rows and 4 columns
            1*   2*   3*   4*
            5*   6*   7*   8*
            9*  10*  11*  12*
         */
        final int NUMBER_OF_CIRCLES = rows * columns;
        displayCircles = new DisplayCircle[NUMBER_OF_CIRCLES];
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                // Distance = (n+1) * P + (2*n+1) * R
                // Where n is the circle in the row starting at 0
                // P is the padding (the space between circles
                // R is the radius of the circles
                int x = (col + 1) * PADDING + (col * 2 + 1) * MAX_RADIUS;
                int y = (row + 1) * (MAX_RADIUS + PADDING);
                
                int circleNumber = row * columns + col + 1; // The circles number (Ranging from 1 to NUMBER_OF_CIRCLES inclusive)
                int lowerFreq = BAND_SIZE * (circleNumber - 1); // The lowest freq (inclusive covered by this circle)
                int upperFreq = BAND_SIZE * circleNumber; // The highest freq (inclusive covered by this circle)
                displayCircles[row * columns + col] = new DisplayCircle(x, y, MIN_RADIUS, MAX_RADIUS, lowerFreq, upperFreq, SETTLE_RATE);
            }
        }
    }
    
    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {
        if (dTSinceLastFrequency >= MIN_NANO_PER_EFFECT_UPDATE) {
            int[] chunk = audioFile.getSamples(nanoToSeconds(deltaT), channel);
            updateCircles(displayCircles, chunk, audioFile.getSampleRate());
            drawCircles(g2d, displayCircles);
            settleCircles(deltaT);
            dTSinceLastFrequency = 0;
        } else {
            dTSinceLastFrequency = dTSinceLastFrequency + deltaT;
        }
    }
    
    @Override
    public boolean hasNextFrame() {
        return audioFile.hasNextSamples(CHUNK_SIZE, channel);
    }
    
    @Override
    public String getName() {
        return "Circle Effect";
    }
    
    @Override
    public void finish() {
        
    }
    
    private void updateCircles(DisplayCircle[] displayCircles, int[] chunk, int sampleRate) {
        int freq = FreqCalculator.getPrimaryFreqOfChunk(chunk, sampleRate);
        double amplitude = getAvg(chunk);
        double ampPercentage = Math.abs(getAsPercentage(amplitude));
        
        for (DisplayCircle circle : displayCircles) {
            if (circle.freqInRange(freq)) {
                circle.pulse((float) ampPercentage);
                break;
            }
        }
    }
    
    private double getAsPercentage(double amplitude) {
        double MAX_AMP = Short.MAX_VALUE;
        return (amplitude / MAX_AMP);
    }
    
    private double getAvg(int[] array) {
        return ((double) sumArray(array) / array.length);
    }
    
    private int sumArray(int[] array) {
        int total = 0;
        for (int n : array) {
            total = total + n;
        }
        return total;
    }
    
    private void drawCircles(Graphics2D g2d, DisplayCircle[] displayCircles) {
        for (DisplayCircle circle : displayCircles) {
            circle.drawThis(g2d);
        }
    }
    
    private void settleCircles(long deltaT) {
        for (DisplayCircle displayCircle : displayCircles) {
            displayCircle.settle(nanoToSeconds(deltaT));
        }
    }
}

/**
 * An object representing one of the display circles which pulse at the amplitude of their relevant frequency as it
 * happens in the song.
 * The x and y cords are locked and the only thing which changes it the radius
 */
class DisplayCircle {
    private final int x; // X of the center of the circle
    private final int y; // Y of the center of the circle
    private final int MIN_RADIUS; // The minimum radius of the circle
    private final int MAX_RADIUS; // The maximum radius of the circle
    private final int LOWER_FREQ; // The lower value of the freq range (inclusive)
    private final int UPPER_FREQ; // The upper value of the freq range (inclusive)
    private final double SETTLE_RATE; /*
    * The amount to change the radius of the circle when it isn't pulsing each
    * second must be less than 1 and greater than 0 otherwise the circle wouldn't decrease
    */
    private int radius; // The current radius of the circle
    private Color circleColour = Color.ORANGE; // The current colour of the circle
    
    /**
     * @param x          The x coordinate of the center of this circle. This is fixed
     * @param y          The y coordinate of the center of this circle. This is fixed
     * @param MIN_RADIUS The minimum radius that this circle has even when there isn't any amplitude at this circles
     *                   frequency band
     * @param MAX_RADIUS The maximum radius of the circle when pulsing
     * @param LOWER_FREQ The minimum frequency that this circle should cover (the lower limit of this circles frequency band)
     * @param UPPER_FREQ The maximum frequency that this circle should cover (the upper limit of this circles frequency band)
     * @param settleRate The rate in percentage per second that the circle should decrease after it has pulsed every second
     */
    DisplayCircle(int x, int y, int MIN_RADIUS, int MAX_RADIUS, int LOWER_FREQ, int UPPER_FREQ, double settleRate) {
        this.MIN_RADIUS = MIN_RADIUS;
        this.MAX_RADIUS = MAX_RADIUS;
        this.SETTLE_RATE = settleRate;
        this.radius = MIN_RADIUS;
        
        this.LOWER_FREQ = LOWER_FREQ;
        this.UPPER_FREQ = UPPER_FREQ;
        
        this.x = x;
        this.y = y;
    }
    
    /**
     * Draw to the provided graphics a filled circle which has a centerpoint x and y and the specified radius
     *
     * @param g2d    The graphics to draw the circle to
     * @param x      The x coordinate of the center of the circle
     * @param y      The y coordinate of the center of the circle
     * @param radius The radius of the circle to draw
     */
    private static void fillCircle(Graphics2D g2d, int x, int y, int radius) {
        x = x - radius;
        y = y - radius;
        g2d.fillOval(x, y, radius * 2, radius * 2);
    }
    
    /**
     * @param circleColour The colour to change the colour of this circle too
     */
    public void setColor(Color circleColour) {
        this.circleColour = circleColour;
    }
    
    /**
     * Check if the specified frequency is within range of this circles frequency band (inclusive)
     *
     * @param freq The frequency to check
     * @return true if the frequency is in range (inclusive) or false if it isn't
     */
    boolean freqInRange(int freq) {
        return freq >= LOWER_FREQ && freq <= UPPER_FREQ;
    }
    
    /**
     * Draws the circle to the provided graphics at the predetermined x and y in the colour of this circle
     *
     * @param g2d The graphics to draw the circle to
     */
    void drawThis(Graphics2D g2d) {
        g2d.setColor(circleColour);
        fillCircle(g2d, x, y, radius);
    }
    
    /**
     * Settle the circle
     *
     * @param deltaT The amount of time in seconds that has passed since the last frame
     */
    void settle(double deltaT) {
        if (radius > MIN_RADIUS) {
            radius = (int) (radius * SETTLE_RATE * deltaT);
        }
        if (radius < MIN_RADIUS) {
            radius = MIN_RADIUS;
        }
    }
    
    /**
     * Cause the circle to "pulse" (increase in size temporarily)
     *
     * @param percentageAmount The amount to pulse by as a percentage of the way with 0 being the base radius and
     *                         1 being the max radius. This value must be >= 0 and <= 1;
     */
    void pulse(float percentageAmount) {
        if (percentageAmount > 1 || percentageAmount < 0) {
            throw new IllegalArgumentException("Percentage amount out of range(0-1) : " + percentageAmount);
        }
        radius = (int) (MIN_RADIUS + (MAX_RADIUS - MIN_RADIUS * percentageAmount));
    }
}
