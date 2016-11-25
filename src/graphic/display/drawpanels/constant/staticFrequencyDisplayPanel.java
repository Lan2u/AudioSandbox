package graphic.display.drawpanels.constant;

import audio.files.AudioFile;
import audio.transforms.AudioDataTransformation;
import graphic.generation.DataPoint;
import graphic.generation.FrequencyBand;

import java.util.ArrayList;

/**
 * Created by Paul Lancaster on 20/11/2016
 */
public class staticFrequencyDisplayPanel extends StaticDrawPanel{
    
    public staticFrequencyDisplayPanel(int width, int height, AudioFile audioFile) {
        super(width, height);
        
        int CHUNK_SIZE = 64;// Size of each chunk in samples
        double NUMBER_OF_CHUNKS = Math.ceil(audioFile.getNumberOfSamples() /CHUNK_SIZE);
        
        int[] frequencies = new int[(int)NUMBER_OF_CHUNKS];
        for (int i = 0; i < frequencies.length;i++) {
            double[] chunk = audioFile.getChunk(CHUNK_SIZE,1);
            frequencies[i] = (int) Math.round(AudioDataTransformation.getFrequencyOfChunk(chunk, audioFile.getSampleRate()));
        }
        ArrayList<FrequencyPoint> frequencyPoints = new ArrayList<>();
        for (int i = 0; i <frequencies.length; i++) {
            boolean found = false;
            for  (FrequencyPoint freqP: frequencyPoints){
                if (freqP.getFreq() == frequencies[i]){
                    freqP.countUp();
                    found = true;
                    break;
                }
            }
            if (!found){
                frequencyPoints.add(new FrequencyPoint(frequencies[i]));
            }
        }
    
        for (FrequencyPoint fP: frequencyPoints) {
            System.out.println("Count " + fP.getCount() + " at " + fP.getFreq()+ "Hz");
        }
        
    }
}
