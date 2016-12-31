package audio.effects;

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
        super(file); // Calls the load specific details and the calculate nano seconds methods
        this.chunkSize = chunkSize;
        this.channel = channel;
    }
    
    // EFFECT SETTINGS / DETAILS METHODS //
    
    @Override
    long calcMinNanoPerFrame(AudioFile file){
        
        return 0L;
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
    
    private double getArrayAverage(int[] samples) {
        long total = 0L;
        for (int sample: samples){
            total = total + sample;
        }
        return (total/samples.length);
    }
    
    @Override
    public boolean hasNextFrame() {
        switch (channel){
            case two:
                return audioFile.hasNextSample(2);
            case both:
                return audioFile.hasNextSample(1) || audioFile.hasNextSample(2);
            case one:
                return audioFile.hasNextSample(1);
            default:
                return true;
        }
    }
}
