package graphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Paul Lancaster on 03/11/16
 */
public class AudioDataGraphGenerator {
    // FIXME Alot of data, large array (millions of values) only a 1000 or so pixels to represent it
    // Doesn't seem to be very possible so just only show a small amount of data at a time (1000 pixels)
    // This means the picture needs to be updated at (sampleRate/pixelsDisplayed) times per second

    /** Generates a segment of a graph given an array of amplitudes which cannot be bigger than the width of the segment image
     * @param amplitude An array of values with each value representing the amplitude of a sample
     * @param WIDTH The width of the image/graph to be generated
     * @param HEIGHT The height of the image/graph
     * @param channels The number of audio channels
     * @return The generated graph as a buffered image
     */
    private static BufferedImage generateGraphSegment(int[] amplitude, int WIDTH, int HEIGHT, int channels) { // y = data[x] + offset to centralise
        
        if (amplitude.length > WIDTH){
            throw new IllegalArgumentException("The amplitude data length must be the same or less than the width");
        }
        
        // Bring all the values within the range of the height
        int[] normalisedValues = ScaleValues(amplitude,HEIGHT);
        
        // Set the colour, Green if only 1 channel or if there are 2 channels green is the second channel and left is the first
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        setToColour(image, Color.black);

        for (int x = 0; x < WIDTH; x++){
            Color colour;
            if ((x%channels) == 0){
                colour = Color.GREEN;
            }else{
                colour = Color.RED;
            }

            int CENTER_HEIGHT = (int)((HEIGHT)/2.0);
            // The amplitude data might not cover the whole of the width so if it doesn't have a no amplitude line at the end to makeup space
            if (x >= normalisedValues.length){
                // If the image has a bigger width than there is values then draw a straight line halfway up the page
                System.out.println("hi");
                image.setRGB(x,CENTER_HEIGHT,colour.getRGB());
            }else{
                // The amplitude has a value so draw it as a point on the graph
                // 0,0 is the top left of the image (I checked) so for values to be closer to the top (as high not negative values should be
                // the positive values must be negative and the negative values positive (so negative values will be further down the image)
                int y = CENTER_HEIGHT - normalisedValues[x] -1;
               
                if (y < 0){
                    y = 0;
                }else if(y > (HEIGHT-1)){
                    y = HEIGHT-1;
                }
                try {
                    image.setRGB(x, y, colour.getRGB());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return image;
    }
    
    /**
     * Scales an int array of values so that all the int values are <= to the desired max value
     * @param values An array of integer values
     * @param desiredMaxValue The new maximum of the array of values
     * @return An array where all the values have been scaled so that they are all smaller or equal to the desiredMaxValue
     */
    //  Is returning 0 only
    public static int[] ScaleValues(int[] values, int desiredMaxValue){
        System.out.println("Height " + desiredMaxValue);
        
        int[] newValues = new int[values.length];
        
        // Get the maximum amplitude value
        int maxValue = 0;
        for (int value : values) {
            if (value > maxValue) maxValue = value;
        }
        double ratio = ((double)desiredMaxValue/(double)maxValue);
        // Turn the values from ratios to actual numbers representing their high on the page
        for (int i = 0; i < newValues.length; i++) {
            // HEIGHT/2 because the top half of the image is for positive values and the bottom half for "negative" values
            newValues[i] = (int)((double)values[i] * ratio);
        }
        return newValues;
    }
    
    /**
     * @param amplitude The amplitude data on the data to be mapped
     * @param imageWidth The width of each frame/image/segment of graph
     * @param imageHeight The height of each frame/image/segment of graph
     * @param channels The number of channels if this is mapping audio
     * @return An array with each segment of the data displayed as an array of images
     */
    public static BufferedImage[] generateGraphImages(int[] amplitude, int imageWidth, int imageHeight, int channels){
        int segmentNum = (int)Math.ceil((((double)amplitude.length)/((double)imageWidth)));
        BufferedImage[] graphSegments = new BufferedImage[segmentNum];
        int length = imageWidth;
        for (int i = 0; i < segmentNum; i++) {
            int[] segmentData = new int[imageWidth];
            if((i*imageWidth + length) > amplitude.length){
                length = amplitude.length - i*imageWidth;
            }
            try {
                System.arraycopy(amplitude, (i * imageWidth), segmentData, 0, length);
                graphSegments[i] = generateGraphSegment(segmentData,imageWidth,imageHeight,channels);
            }catch(ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
        return graphSegments;
    }
    
    /** Set an image to a solid colour
     * @param image The image to set to the designated colour
     * @param colour The designated colour
     */
    private static void setToColour(BufferedImage image, Color colour) {
        for (int y = 0; y < image.getHeight(); y++){
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x,y,colour.getRGB());
            }
        }
    }
}
