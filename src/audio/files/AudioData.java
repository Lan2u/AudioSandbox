package audio.files;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 24/11/2016
 */
abstract class AudioData {
    
    protected int position = 0; // Position within the data in samples
    
    static AudioData getData(int dataSize, int numberOfChannels) {
        switch(numberOfChannels){
            case 1:
                return new MonoAudioData(dataSize);
            case 2:
                return new StereoAudioData(dataSize);
            default:
                throw new IllegalArgumentException("Unsupported number of channels");
        }
    }
    
    int getPosition(){
        return position;
    }
    
    void resetPosition(){
        position = 0;
    }
    
    abstract void readData(FileInputStream in, int bytesPerSample) throws IOException;
    
}
