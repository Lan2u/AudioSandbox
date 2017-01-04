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
    private int segments = 200;
    private long minNanoPerFrequencyUpdate; // The minimum number of nano seconds between each time the frequency of a chunk is used to effect the string
    private double[] stringSegmentDeflection; // The deflection of the points from the center ranging from +- 1
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    public LagFreqEffect(AudioFile file, int chunkSize, int height, CHANNEL channel) {
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
            maxFrequencies[i] = FreqCalculator.getPrimaryFreqOfChunk(chunk, file.getSampleRate());
        }
    }
    
    long dTSinceLastFrequency = 0;
    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {
        settleSegments(deltaT);
        
        if (dTSinceLastFrequency >= minNanoPerFrequencyUpdate){
            int freq = maxFrequencies[pos]; // Frequency
            double freqPerSegment = (audioFile.getSampleRate()/2.0)/segments; // The amount of frequency values (integer) per segment
            int band = (int)(freq/freqPerSegment); // The band that the frequency falls under
            deflectSegment(band, 1.0); // Deflect the string
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
        if (stringSegmentDeflection[segmentIndex] > 1.0) stringSegmentDeflection[segmentIndex] = 1.0;
        if (stringSegmentDeflection[segmentIndex] < -1.0) stringSegmentDeflection[segmentIndex] = -1.0;
    }
    
    /**
     * Bring the points closer to the center point as the string "settles"
     * @param deltaT The change in time in nano seconds since the last frame
     */
    private void settleSegments(long deltaT){
        double DEFLECTION_RATIO = 0.8 / 100000000.0; // The amount that the deflection should change per nanosecond
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
