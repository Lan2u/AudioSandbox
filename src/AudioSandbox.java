import graphic.UserInterface;
import sound.files.WaveFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 30/10/2016
 */

    
public class AudioSandbox {
    private static final String FILE_PATH = "resources/high_risk.wav";

    public static void main(String[] args) throws IOException {
        
        WaveFile waveFile = new WaveFile(new File(FILE_PATH));
        System.out.println(waveFile.toString());
        
        // FIXME, doesn't display the images at the right size
        // Theses numbers refer to the value of WIDTH below
        // Runs fine with width >= 600
        // Didn't display correctly with width = 500
        // Reran at 500 and worked
        // Ran at 400 fine
        // Re-ran at 400, didn't display correctly
        // Ran again at 400 displayed correctly
        
        int WIDTH = 1000;
        int HEIGHT = 800;
        UserInterface gui = new UserInterface(WIDTH,HEIGHT);
        gui.setSize(WIDTH,HEIGHT);
        
        gui.startDisplaying(waveFile, 2);
    }
    
    /** Get the amplitude data representation of the wave file
     * @param waveFile The wavefile to convert to amplitude data
     * @return The amplitude data as an int array with a max value of TODO add max value here (just in comment)
     */
    private static int[] getAmplitudeData(WaveFile waveFile) {
        return waveFile.getSamples(waveFile.getLength());
    }
    
    /**
     * @param bytes
     */
    private static void printByteArray(byte[] bytes){
        for(byte b: bytes){
            System.out.print(b + " ");
        }
    }

}