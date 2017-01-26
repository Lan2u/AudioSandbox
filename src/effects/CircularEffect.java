package effects;

import audio.file.AudioFile;
import calculate.FreqCalculator;

import java.awt.*;
import java.awt.List;
import java.util.*;

/**
 * An effect which displays a number of circles each representing a different frequency band theses circles
 * then expand depending on the amplitude of that frequency each frame. The frequency of each circle is
 * marked by a label in the circle
 *
 * Created by paul on 23/01/17.
 * @author Paul Lancaster
 */
public class CircularEffect extends VisualEffect{
    private final int CHUNKSIZE;
    private final int PADDING; // Minimum space between circles at any point (even if the 2 circles are fully expanded)
    private final int MIN_DIAMETER;
    private final int MAX_DIAMETER;
    private final int BAND_SIZE;
    
    private ArrayList<DisplayCircle> displayCircles = new ArrayList<>();
    
    private int channel = 1;
    
    private long dTSinceLastFrequency = 0;
    private long minNanoPerFrequencyUpdate;
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     * @param rows The number of rows of circles (rows * cols = number of circles displayed)
     *
     */
    public CircularEffect(AudioFile file, int rows, int cols, int padding, int minDiameter, int maxDiameter) {
        super(file);
        PADDING = padding;
        MIN_DIAMETER = minDiameter;
        MAX_DIAMETER = maxDiameter;
        BAND_SIZE = file.getSampleRate() / (rows * cols);
        CHUNKSIZE = calcChunkSize( rows, cols, file.getSampleRate());
        
        calculateCirclePositions(rows, cols);
    }
    
    private int calcChunkSize(int rows, int cols, int sampleRate) {
        return sampleRate / (rows * cols);
    }
    
    
    private void calculateCirclePositions(int rows, int columns) {
        /* Example of circle layout with 3 rows and 4 columns
            *   *   *   *
            *   *   *   *
            *   *   *   *
         */
        // TODO next thing for this effect is to calculate and store circle positions
        // TODO and the upper and lower limits of the frequency bands that each circle covers
        
    }
    
    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {
        if (dTSinceLastFrequency >= minNanoPerFrequencyUpdate){
            int x = (int)(width /2.0);
            int y = (int)(height/2.0);
            int[] chunk = audioFile.getChunk(CHUNKSIZE, channel);
            
            
            
            dTSinceLastFrequency = 0;
        }else{
            dTSinceLastFrequency = dTSinceLastFrequency + deltaT;
        }
    }
    
    @Deprecated
    private void calcDiameter(int[] chunk, int sampleRate) {
        int freq = FreqCalculator.getPrimaryFreqOfChunk(chunk,sampleRate);
        int freqLog10 = (int) Math.round( Math.log10(freq) );
    }

    @Override
    public boolean hasNextFrame() {
        return audioFile.hasNextSamples(CHUNKSIZE, channel);
    }

    @Override
    public String getName() {
        return "Circle Effect";
    }

    @Override
    public void finish() {

    }

    public void collectionsExamples(){
        ArrayList<Integer> arrayList;
        List simpleList = new List();
        Vector<Integer> vector; // Old array from early java
        LinkedList linkedList;

        Set<Integer> mySet = new TreeSet<>();

        Map<String,Integer> myMap = new HashMap<>();
        myMap.put("Key",20);
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
    private final float SETTLE_RATE; /*
    * The amount to change the radius of the circle when it isn't pulsing each
    * second must be less than 1 and greater than 0 otherwise the circle wouldn't decrease
    */
    private int radius; // The current radius of the circle
    private Color circleColour = Color.ORANGE; // The current colour of the circle
    
    /**
     * @param x The x coordinate of the center of this circle. This is fixed
     * @param y The y coordinate of the center of this circle. This is fixed
     * @param MIN_RADIUS The minimum radius that this circle has even when there isn't any amplitude at this circles
     *                      frequency band
     * @param MAX_RADIUS The maximum radius of the circle when pulsing
     * @param LOWER_FREQ The minimum frequency that this circle should cover (the lower limit of this circles frequency band)
     * @param UPPER_FREQ The maximum frequency that this circle should cover (the upper limit of this circles frequency band)
     * @param settleRate The rate in percentage per second that the circle should decrease after it has pulsed every second
     */
    DisplayCircle(int x, int y, int MIN_RADIUS, int MAX_RADIUS, int LOWER_FREQ, int UPPER_FREQ, int settleRate){
        this.MIN_RADIUS = MIN_RADIUS;
        this.MAX_RADIUS = MAX_RADIUS;
        this.SETTLE_RATE = settleRate;
        
        this.LOWER_FREQ = LOWER_FREQ;
        this.UPPER_FREQ = UPPER_FREQ;
        
        this.x = x;
        this.y = y;
    }
    
    /**
     * @param circleColour The colour to change the colour of this circle too
     */
    public void setColor(Color circleColour){
        this.circleColour = circleColour;
    }
    
    /**
     * Check if the specified frequency is within range of this circles frequency band (inclusive)
     * @param freq The frequency to check
     * @return true if the frequency is in range (inclusive) or false if it isn't
     */
    public boolean freqInRange(int freq){
        return freq >= LOWER_FREQ && freq <= UPPER_FREQ;
    }
    
    /**
     * Draw to the provided graphics a filled circle which has a centerpoint x and y and the specified radius
     * @param g2d The graphics to draw the circle to
     * @param x The x coordinate of the center of the circle
     * @param y The y coordinate of the center of the circle
     * @param radius The radius of the circle to draw
     */
    private static void fillCircle(Graphics2D g2d, int x, int y, int radius){
        x =  x - radius;
        y = y - radius;
        g2d.fillOval(x,y,radius*2,radius*2);
    }
    
    /**
     * Draws the circle to the provided graphics at the predetermined x and y in the colour of this circle
     * @param g2d The graphics to draw the circle to
     */
    public void drawThis(Graphics2D g2d){
        g2d.setColor(circleColour);
        fillCircle(g2d, x, y, radius);
    }
    
    /**
     * Settle the circle
     * @param deltaT The amount of time in seconds that has passed since the last frame
     */
    public void settle(double deltaT){
        if (radius > MIN_RADIUS){
            radius = (int) (radius * SETTLE_RATE * deltaT);
        }
    }
    
    /**
     * Cause the circle to "pulse" (increase in size temporarily)
     * @param percentageAmount The amount to pulse by as a percentage of the way with 0 being the base radius and
     *                         1 being the max radius. This value must be >= 0 and <= 1;
     */
    public void pulse(float percentageAmount){
        if (percentageAmount > 1 || percentageAmount < 0) {
            throw new IllegalArgumentException("Percentage amount out of range(0-1) : " + percentageAmount);
        }
        radius = (int)(MIN_RADIUS + (MAX_RADIUS - MIN_RADIUS * percentageAmount));
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}
