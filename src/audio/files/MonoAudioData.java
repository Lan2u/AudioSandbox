package audio.files;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 24/11/2016
 */
public class MonoAudioData extends AudioData{
        
    byte[] data_ch1;
    
    
    public MonoAudioData(int dataSize) {
        
    }
    
    boolean hasNextSample(int channel) {
        return false;
    }
    
    short nextSample() {
        return 0;
    }
    
    void readData(FileInputStream in) throws IOException {
        
    }
    
    @Override
    void readData(FileInputStream in, int bytesPerSample) throws IOException {
        
    }
}
