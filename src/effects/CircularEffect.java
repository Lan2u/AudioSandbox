package effects;

import audio.file.AudioFile;
import calculate.FreqCalculator;

import java.awt.*;
import java.awt.List;
import java.util.*;

/**
 * Created by paul on 23/01/17.
 */
public class CircularEffect extends VisualEffect{
    int PADDING = 10;
    int BASE_DIAMETER = 16;
    private long dTSinceLastFrequency = 0;
    private long minNanoPerFrequencyUpdate;


    /**
     * Loads the visual effect using details from the given LoadedFile and encapsulates that file
     *
     * @param file The file that becomes stored (encapsulated) in and used for the visual effect
     */
    CircularEffect(AudioFile file) {
        super(file);
    }

    @Override
    protected void drawEffect(Graphics2D g2d, int width, int height, long deltaT) {

        if (dTSinceLastFrequency >= minNanoPerFrequencyUpdate){
            int x = (int)(width /2.0);
            int y = (int)(height/2.0);
            int diameter = calcDiameter();
            g2d.fillOval(x,y,diameter,diameter);
            dTSinceLastFrequency = 0;
        }else{
            dTSinceLastFrequency = dTSinceLastFrequency + deltaT;
        }
    }

    private int calcDiameter(int[] chunk, int sampleRate) {
        FreqCalculator.getPrimaryFreqOfChunk(chunk,sampleRate);
        return BASE_DIAMETER;
    }

    @Override
    public boolean hasNextFrame() {
        return false;
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
