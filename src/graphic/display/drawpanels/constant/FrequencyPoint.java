package graphic.display.drawpanels.constant;

/**
 * Created by Paul Lancaster on 22/11/2016
 */
public class FrequencyPoint {
    private double freq = 0.0;
    private long count = 0;
    
    public FrequencyPoint(double frequency) {
        this.freq = frequency;
        count = 1;
    }
    
    public void setFreq(double freq){
        this.freq = freq;
    }
    
    public void countUp() {
        count++;
    }
    
    public double getFreq() {
        return freq;
    }
    
    public long getCount() {
        return count;
    }
}
