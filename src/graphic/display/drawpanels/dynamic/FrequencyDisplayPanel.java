package graphic.display.drawpanels.dynamic;

/**
 * Created by Paul Lancaster on 18/11/2016
 */
public class FrequencyDisplayPanel extends DynamicDrawPanel{

    private double[] frequency;
    private double[] amplitude;

    public FrequencyDisplayPanel(int WIDTH, int HEIGHT, double[] initialFrequencyData, double[] initialAmplitudeData, double displayRate) {
        super(WIDTH, HEIGHT);
        this.frequency = initialFrequencyData;
        this.amplitude = initialAmplitudeData;
    }
    
    @Override
    public void run() {

        boolean running = true;
        long lastFrameTime = System.nanoTime();
        while (running){
            long frameTime = System.nanoTime();
            long deltaT = Math.abs(frameTime - lastFrameTime);
            

            lastFrameTime = frameTime;
        }
    }
}
