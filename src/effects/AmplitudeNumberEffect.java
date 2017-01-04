package effects;

import audio.file.AudioFile;

import java.awt.*;

/**
 * Created by Paul Lancaster on 31/12/2016
 */
public class AmplitudeNumberEffect extends VisualEffect{
    private int chunkSize; // The number of amplitude values to average and display (There would be well too many if each individual value was displayed)
    private CHANNEL channel;
    
    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     * The various settings needed for the effect are then calculated and stored
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    public AmplitudeNumberEffect(AudioFile file, int chunkSize, CHANNEL channel){
        super(file); // Calls the load specific details that every VisualEffect has to
        this.chunkSize = chunkSize;
        this.channel = channel;
        minimumNanoPerFrame = calcMinNanoPerFrame(audioFile);
        System.out.println("Minimum Nano Per Frame " +minimumNanoPerFrame);
    }
    
    // EFFECT SETTINGS / DETAILS METHODS //
    
    private long calcMinNanoPerFrame(AudioFile file){
        double chunksPerSecond = file.getSampleRate()/((double)chunkSize);
        double nanoseconds = 1000000000.0/chunksPerSecond;
        System.out.println("Nanoseconds per frame " + nanoseconds);
        return Math.round(nanoseconds);
    }
    
    // DRAW EFFECT METHODS //
    
    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {
        if (channel == CHANNEL.both){
            // TODO 2 different channels
        }else{
            int[] samples = audioFile.getChunk(chunkSize,channel.getInt());
            double val = getArrayAverage(samples);
            g2d.drawString(String.valueOf(val),0,height/2);
        }
    }
    
    /**
     * @param array The int array to average
     * @return The average of the values in the array
     */
    private double getArrayAverage(int[] array) {
        long total = 0L;
        for (int n: array){
            total = total + n;
        }
        return (total/array.length);
    }
    
    /**
     * Check if there is another frame left in the effect
     * @return True if there is another frame left in the effect (on the effect channel) and false if there isn't
     */
    @Override
    public boolean hasNextFrame() {
        switch (channel){
            case two:
                return audioFile.hasNextSamples(chunkSize,2);
            case both:
                return audioFile.hasNextSamples(chunkSize,1) || audioFile.hasNextSamples(chunkSize,2);
            case one:
                return audioFile.hasNextSamples(chunkSize,1);
            default:
                return true;
        }
    }
    
    
    @Override
    public String getName() {
        return "Amplitude number display";
    }
    
    @Override
    public void finish() {
        audioFile.resetPos();
        System.out.println(getName() + " finished playing");
    }
}
