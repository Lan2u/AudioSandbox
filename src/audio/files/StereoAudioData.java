package audio.files;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 24/11/2016
 */
public class StereoAudioData extends AudioData{
    private byte[] data_ch1;
    private byte[] data_ch2;
    
    private short[] sample_ch1;
    private short[] sample_ch2;
    
    
    public StereoAudioData(int dataSize) {
        data_ch1 = new byte[dataSize];
        data_ch2 = new byte[dataSize];
    }
    
    boolean hasNextSample(int channel) {
        switch(channel){
            case 1:
                return position < data_ch1.length;
            case 2:
                return position < data_ch2.length;
            default:
                throw new IllegalArgumentException("Channel out of range");
        }
    }
    
    short nextSample(int channel) {
        switch(channel){
            case 1:
                
                break;
            case 2:
                break;
            default:
                throw new IllegalArgumentException("Channel out of range");
        }
    }
    
    @Override
    void readData(FileInputStream in, int bytesPerSample) throws IOException {
        byte[] data = new byte[data_ch1.length*2];
        in.read(data);
        
        for (int k = 0; k < data_ch1.length; k++) {
            data_ch1[k] = data[k*2];
            data_ch2[k] = data[k*2+1];
        }
    
        for (int i = 0; i < data_ch1.length; i++) {
            //TODO turn data into samples and store in sample_ch1 and sample_ch2
        }
    }
}
