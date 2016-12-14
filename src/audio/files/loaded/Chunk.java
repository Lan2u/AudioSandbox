package audio.files.loaded;

public class Chunk {
    private int amplitude;
    private double frequency;
    // Default chunk everything 0
    Chunk() {
        amplitude = 0;
        frequency = 0;
    }
    
    Chunk(short[] amplitudes, double frequency){
        int temp = 0;
        for (short amp: amplitudes){
            temp=temp+Math.abs(amp);
        }
        this.amplitude = (temp/amplitudes.length);
        this.frequency = frequency;
    }
    
    @Override
    public String toString(){
        return String.format("%d at %.1fHz",amplitude,frequency);
    }
    
    double getFrequency() {
        return frequency;
    }
    
    int getAmplitude() {
        return amplitude;
    }
}
