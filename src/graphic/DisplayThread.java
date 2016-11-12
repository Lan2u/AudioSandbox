package graphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Paul Lancaster on 08/11/16
 * As always https://docs.oracle.com/javase/ used for reference
 */
public class DisplayThread extends Thread{

    private DrawPanel display;
    private int[] samples = null;
    private int sampleRate;
    private int channels;


    DisplayThread(DrawPanel display){
        this.display = display;
    }

    @Override
    public void run(){
        
        if (samples == null || samples.length ==0) {
            System.out.println("Null samples to display, samples to display never set or sample length = 0");
        }else{
            
            
            // Blank Buffered Image
            // Data starts being displayed at the left pushing all the rest of the data right
            // FIXME use of constants here is for testing need to replace
            BufferedImage image = new BufferedImage(1000,800, BufferedImage.TYPE_INT_RGB);
            
            int[] yValues = AudioDataGraphGenerator.ScaleValues(samples,image.getHeight());
            
            Color colour = Color.cyan;
            
            final int GENERATION_COLUMN_X = 0; // The x value that the values are generated out before moving along the image
            
            long uT = System.nanoTime();
            int CENTER_HEIGHT = display.getHeight()/2;
            
            for (int i = 0; i < yValues.length; i++) {
                
                /* Starts one column in from the right and moves that column to the right (thereby overwriting the existing
                    column. Moves through the image all the way to the generation point moving the columns to the right) this
                    leaves the starting column (GENERATION_POINT_X) and the column next to it (GENERATION_POINT_X + 1) being
                    the same.*/
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = image.getWidth() - 1; x > 0; x--){
                        int pixel = image.getRGB(x-1,y);
                        image.setRGB(x,y,pixel);
                    }
                }
                // Set the starting column (GENERATION_POINT_X) to the new data value
                int y = CENTER_HEIGHT - yValues[i] -1;
                    if (yValues[i] == 0){
                  //      System.out.println(GENERATION_COLUMN_X + "," + CENTER_HEIGHT);
                        image.setRGB(GENERATION_COLUMN_X, CENTER_HEIGHT, colour.getRGB());
                    }else {
                        image.setRGB(GENERATION_COLUMN_X, y, colour.getRGB());
                    }
               // System.out.println(y);
                display.setCurrentFrame(image);
                
            }
            System.out.println();
            System.out.println("deltaT " + (System.nanoTime()-uT));
        }
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
