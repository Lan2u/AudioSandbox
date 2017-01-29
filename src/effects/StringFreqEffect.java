package effects;

import audio.file.AudioFile;
import calculate.oldFreqCalculator;

import java.awt.*;

/**
 * Created by Paul Lancaster on 03/01/2017
 */
public class StringFreqEffect extends VisualEffect {
    private static final int SEGMENT_EFFECT_RANGE = 10; // The number of segments effected either side of the point in the "string" where the force is applied
    private static final double SEGMENT_EFFECT_DROPOFF = 0.004; // for each index away from the point where the force was applied the amount that the force on the neighbouring segments is decreased by
    private static final double DEFLECT_FORCE = 1.0;
    private static final double DEFLECTION_RATIO = 0.999999999999 / 100000000.0; // The amount that the deflection should change per nanosecond
    
    private int chunk_size;
    private int[] maxFrequencies;
    private int pos;
    private int segments = 200;
    private long minNanoPerFrequencyUpdate; // The minimum number of nano seconds between each time the frequency of a chunk is used to effect the string
    private double[] stringSegmentDeflection; // The deflection of the points from the center ranging from +- 1
    // 0.0 deflection = center of page (in height)
    // 1 deflection = top of page
    // -1 deflection = bottom of page
    // 0.5 deflection = 3 quarters up the page
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    public StringFreqEffect(AudioFile file,int chunkSize,int height, CHANNEL channel) {
        super(file);
        this.chunk_size = chunkSize;
        calcPrimaryFreqs(file,chunkSize,channel);
        minimumNanoPerFrame = calcMinNanoPerFrame(file);
        minNanoPerFrequencyUpdate = 1000000000L * chunk_size /file.getSampleRate(); // Same as usual
        stringSegmentDeflection = new double[segments];
        pos=0;
    }
    
    private long calcMinNanoPerFrame(AudioFile file) {
        // Update quicker than the frequency data so that the string can move
        int MAX_FPS = 30;
        return 1000000000L/ MAX_FPS;
    }
    
    private void calcPrimaryFreqs(AudioFile file, int chunkSize, CHANNEL channel) {
        int chunkCount = file.getNumberOfSamples()/chunkSize;
        maxFrequencies = new int[chunkCount];
        for (int i = 0; i < chunkCount; i++) {
            int[] chunk = file.getChunk(chunk_size, channel.getInt());
            maxFrequencies[i] = oldFreqCalculator.getPrimaryFreqOfChunk(chunk, file.getSampleRate());
        }
    }
    
    private long dTSinceLastFrequency = 0;
    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {
        settleSegments(deltaT);
        
        if (dTSinceLastFrequency >= minNanoPerFrequencyUpdate){
            int freq = maxFrequencies[pos]; // Frequency
            double freqPerSegment = (audioFile.getSampleRate()/2.0)/segments; // The amount of frequency values (integer) per segment
            int band = (int)(freq/freqPerSegment); // The band that the frequency falls under
            deflectSegment(band, DEFLECT_FORCE); // Deflect the string
            dTSinceLastFrequency = 0;
            pos++;
        }else{
            dTSinceLastFrequency = dTSinceLastFrequency + deltaT;
        }
        
        int SEGMENT_SPACING = 5;
        for (int i = 0; i < stringSegmentDeflection.length; i++) {
            int x = i*SEGMENT_SPACING;
            double amplitude = height/2.0;
            int y = (int)(amplitude - stringSegmentDeflection[i] * amplitude);
            g2d.drawRect(x,y,1,1);
        }
    }
    
    /**
     * Deflect a segment by a certain amount
     * @param segmentIndex The index of the segment to apply the "force" to
     * @param force The force to apply ranging from -1.0 to 1.0
     */
    private void deflectSegment(int segmentIndex, double force){
        stringSegmentDeflection[segmentIndex] = stringSegmentDeflection[segmentIndex] + force;
        for (int i = 1; i < SEGMENT_EFFECT_RANGE; i++) { // Starting at the segment in front of the forced segment apply a force to all the other segments (within range SEGMENT_EFFECT_RANGE) but of a smaller and smaller amount
            int index = segmentIndex + i;
            if (index < stringSegmentDeflection.length){ // Stop it being out of range (of the array) on the high end
                stringSegmentDeflection[index] = stringSegmentDeflection[index] + force * SEGMENT_EFFECT_DROPOFF * i;
            }
        }
        
        for (int i = -1; i > segmentIndex-SEGMENT_EFFECT_RANGE; i--) { // Starting at the segment behind the forced segment (segmentIndex) apply a gradually decreasing force (decreasing by SEGMENT_EFFECT_DROPOFF each time) to all the segment behind it that are in range of the original (SEGMENT_EFFECT_RANGE)
            int index = segmentIndex + i;
            if (index >= 0) { // Stop it being out of range (of the array) on the low end
                stringSegmentDeflection[index] = stringSegmentDeflection[index] + force * SEGMENT_EFFECT_DROPOFF * -i;
            }
        }
        
        if (stringSegmentDeflection[segmentIndex] > 1.0) stringSegmentDeflection[segmentIndex] = 1.0;
        if (stringSegmentDeflection[segmentIndex] < -1.0) stringSegmentDeflection[segmentIndex] = -1.0;
    }
    
    /**
     * Bring the points closer to the center point as the string "settles"
     * @param deltaT The change in time in nano seconds since the last frame
     */
    private void settleSegments(long deltaT){
        for (int i = 0; i < stringSegmentDeflection.length; i++) {
            stringSegmentDeflection[i] = stringSegmentDeflection[i] * DEFLECTION_RATIO * deltaT;
        }
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
