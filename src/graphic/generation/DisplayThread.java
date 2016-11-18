package graphic.generation;

import audio.transforms.AudioDataTransformation;
import graphic.DrawPanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
            int HEIGHT = 800;
            int WIDTH = 1000;

            // FIXME why is this height -60?
            int[] yValues = AudioDataGraphGenerator.ScaleValues(samples,HEIGHT/2 - 60);
            
            AudioDataTransformation.modulate(yValues, 200);
            
            final int GENERATION_COLUMN_X = 0; // The x value that the values are generated out before moving along the image
            
            long uT = System.nanoTime();
            
            // TODO Efficiency (in terms of doing the same stuff but much faster) needs to be greatly increased
            ArrayList<DataPoint> dataPoints = new ArrayList<>();
            // TODO currently takes 141 seconds to display (depending on computer power) a 2 second audio file
            for (int i = 0; i < yValues.length; i++){
                /* Starts one column in from the right and moves that column to the right (thereby overwriting the existing
                    column. Moves through the image all the way to the generation point moving the columns to the right) this
                    leaves the starting column (GENERATION_POINT_X) and the column next to it (GENERATION_POINT_X + 1) being
                    the same.*/
                
                // TODO Shifting the entire image is expensive need to just move the area that is actually coloured
                // TODO Try using an int[][] array storing only relevant points like in the snake
                // TODO Add a center line (different colour / red)

                for (int j = 0; j < dataPoints.size(); j++){
                    dataPoints.get(j).moveRight(1);
                    if (dataPoints.get(j).x >= WIDTH){
                        dataPoints.remove(dataPoints.get(j));
                    }
                }
                
                DataPoint newPoint = new DataPoint(); // New point to add
                newPoint.setX(GENERATION_COLUMN_X);
                newPoint.setY(yValues[i]);
                if ((i % channels) == 0){
                    newPoint.setColour(Color.RED);
                }else{
                    newPoint.setColour(Color.GREEN);
                }
                dataPoints.add(newPoint);

                BufferedImage image = genDisplayImage(WIDTH,HEIGHT,dataPoints);
                display.setCurrentFrame(image);
            }
            System.out.println();
            System.out.println("deltaT " + (System.nanoTime()-uT));
        }
    }

    private BufferedImage genDisplayImage(int WIDTH, int HEIGHT, ArrayList<DataPoint> dataPoints) {
        int CENTER_HEIGHT = HEIGHT/2;
        BufferedImage image = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_RGB);
        for(DataPoint dataPoint: dataPoints){
            try {
                image.setRGB(dataPoint.getIntX(), dataPoint.getIntY()+ CENTER_HEIGHT, dataPoint.getColour().getRGB());
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }

        image.getGraphics().drawLine(0,CENTER_HEIGHT,image.getWidth(),CENTER_HEIGHT); // Center line
        return image;
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
