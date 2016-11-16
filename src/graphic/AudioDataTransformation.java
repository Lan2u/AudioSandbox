package graphic;

import org.jtransforms.dct.FloatDCT_1D;
import org.jtransforms.dht.FloatDHT_1D;

import java.lang.reflect.Method;


/**
 * Created by Paul Lancaster on 15/11/2016
 */
public class AudioDataTransformation {
    
    public Boolean[] posNegativeModulation(int[] dataPoints){
        Boolean[] positive = new Boolean[dataPoints.length];
        for (int i = 0; i < dataPoints.length; i++) {
            if (dataPoints[i] > 0){
                positive[i] = true;
            }else if (dataPoints[i] == 0){
                positive[i] = null;
            } else{
                positive[i] = false;
            }
        }
        return positive;
    }
    
    public static void modulate(int[] yValues, int value) {
        for (int i = 0; i < yValues.length; i++) {
            if (yValues[i] > 0){
                yValues[i] = value;
            }else if (yValues[i] == 0){
                yValues[i] = 0;
            } else{
                yValues[i] = -value;
            }
        }
    }
    
    /*
    http://stackoverflow.com/questions/2704139/how-to-get-audio-frequency-data-from-a-wave-file
    
    https://en.wikipedia.org/wiki/Digital_signal_processing
    
    https://en.wikipedia.org/wiki/Fast_Fourier_transform
    
    https://en.wikipedia.org/wiki/Quantization_(signal_processing)
    https://en.wikipedia.org/wiki/Sampling_(signal_processing)
     */
    public static void quantize(){
        
    }
    
    public static void performFastFourierTransform(float[] array){
            
    }
}
