package effects;

import audio.file.AudioFile;
import calculate.FreqCalculator;

import java.awt.*;

/**
 * Created by Paul Lancaster on 03/01/2017
 *
 * This effect is like the string effect but the points are not linked and they fade separately
 */
public class LagFreqEffect extends VisualEffect {
    private int chunk_size;
    private int[] maxFrequencies;
    private int pos;
    private int SEGMENTS = 50;
    private int SEGMENT_WIDTH = 20;
    private long minNanoPerFrequencyUpdate; // The minimum number of nano seconds between each time the frequency of a chunk is used to effect the string
    private double[] stringSegmentDeflection; // The deflection of the points from the center ranging from +- 1
    private long dTSinceLastFrequency = 0;
    private double DEFLECT_AMOUNT = 0.5;
    private double DEFLECTION_RATIO = 9e-9; // The amount that the deflection should change per nanosecond
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    public LagFreqEffect(AudioFile file, int height, CHANNEL channel, int segments) {
        super(file);
        this.SEGMENTS =segments;
        // Bin size = sampleRate / chunkSize
        // sampleRate / 2 * bin size = chunk size
        // bin size = sampleRate / segments
        // sampleRate = bin size * segments = sampleRate * segments / chunkSize
        // sampleRate * segments / sampleRate = chunkSize
        chunk_size = 1024;
        calcPrimaryFreqs(file,channel);
        minimumNanoPerFrame = calcMinNanoPerFrame(file);
        minNanoPerFrequencyUpdate = 1000000000L * chunk_size /file.getSampleRate(); // Same as usual
        stringSegmentDeflection = new double[SEGMENTS];
        pos=0;
    }
    
    private long calcMinNanoPerFrame(AudioFile file) {
        // Update quicker than the frequency data so that the string can move
        int MAX_FPS = 30;
        return 1000000000L/ MAX_FPS;
    }
    
    private void calcPrimaryFreqs(AudioFile file, CHANNEL channel) {
        int chunkCount = file.getNumberOfSamples()/chunk_size;
        maxFrequencies = new int[chunkCount];
        for (int i = 0; i < chunkCount; i++) {
            int[] chunk = file.getChunk(chunk_size, channel.getInt());
            maxFrequencies[i] = FreqCalculator.getPrimaryFreqOfChunk(chunk, file.getSampleRate());
        }
    }
    
    
    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {
        settleSegments(deltaT);
        
        if (dTSinceLastFrequency >= minNanoPerFrequencyUpdate){
            int freq = maxFrequencies[pos]; // Frequency
            double freqPerSegment = (audioFile.getSampleRate()/2.0)/ SEGMENTS; // The amount of frequency values (integer) per segment
            int band = (int)(freq/freqPerSegment); // The band that the frequency falls under
            deflectSegment(band, DEFLECT_AMOUNT); // Deflect the string
            dTSinceLastFrequency = 0;
            pos++;
        }else{
            dTSinceLastFrequency = dTSinceLastFrequency + deltaT;
        }
        
        for (int i = 0; i < stringSegmentDeflection.length; i++) {
            int x = i*SEGMENT_WIDTH;
            double amplitude = height/2.0;
           // int y = (int)(amplitude - );
            int bandHeight = (int)(stringSegmentDeflection[i] * amplitude);
            g2d.drawRect(x,(int)amplitude - bandHeight,SEGMENT_WIDTH,bandHeight);
        }
    }
    
    /**
     * Deflect a segment by a certain amount
     * @param segmentIndex The index of the segment to apply the "force" to
     * @param force The force to apply ranging from -1.0 to 1.0
     */
    private void deflectSegment(int segmentIndex, double force){
        stringSegmentDeflection[segmentIndex] = stringSegmentDeflection[segmentIndex] + force;
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
        return "Frequency lag bar";
    }
    
    @Override
    public void finish() {
        audioFile.resetPos();
        System.out.println(getName() + " effect has finished");
    }
}
