package graphic;

import java.awt.image.BufferedImage;

/**
 * Created by Paul Lancaster on 08/11/16
 * As always https://docs.oracle.com/javase/ used for reference
 */
public class DisplayThread extends Thread{
    

    private DrawPanel display;
    private int[] samples;
    private int sampleRate;
    private int channels;


    DisplayThread(DrawPanel display){
        this.display = display;
    }

    @Override
    public void run(){
        // Images to display (graph segments)
        BufferedImage[] frames = AudioDataGraphGenerator.generateGraphImages(samples,display.getWidth(),display.getHeight(),channels);
        System.out.println("Frames to display " + frames.length);

        long updateRate = calculateUpdateRate(); // The number of nano seconds between each frame
        System.out.println("nano seconds per frame " + updateRate);

        long startTime; // The time at the start of the frame drawing (nano seconds)
        long deltaT; // The time taken to draw the frame (nano seconds)
        long timeTaken = 0L;
        int frame = 0;

        // Update section, should update to display the wave in real time
        long DisplayStart;
        System.out.println("Display Start " + (DisplayStart=System.nanoTime()));
        while (frame < frames.length) {
            startTime = System.nanoTime();
            if (timeTaken >= updateRate) {
                display.setCurrentFrame(frames[frame]);
                timeTaken = 0;
                frame++;
            }
            deltaT = System.nanoTime() - startTime;
            timeTaken = timeTaken + deltaT;
        }
        System.out.println("Display End " + System.nanoTime());
        System.out.println("Display Time " + (System.nanoTime()-DisplayStart));
    }

    private long calculateUpdateRate() {
        double secondsPerSample = 1.0 / sampleRate;
        double samplesDisplayedEachFrame = display.getWidth();
        double updateRate = samplesDisplayedEachFrame * secondsPerSample; // The number of seconds for each frame
        return (long)(updateRate * Math.pow(10, 9)); // Convert to nano seconds
    }

    void dataToDisplay(int[] audioSamples, int sampleRate, int channels) {
        samples = audioSamples;
        this.sampleRate = sampleRate;
        this.channels = channels;
    }
}
