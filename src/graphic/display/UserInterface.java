package graphic.display;

import audio.files.WaveFile;
import graphic.display.drawpanels.constant.staticFrequencyDisplayPanel;
import graphic.display.drawpanels.dynamic.FrequencyDisplayPanel;

import javax.swing.*;

/**
 * Created by Paul Lancaster on 01/11/2016
 */
public class UserInterface extends JFrame {
    
    private staticFrequencyDisplayPanel displayPanel;

    public UserInterface(int WIDTH, int HEIGHT, WaveFile waveFile){
        displayPanel = new staticFrequencyDisplayPanel(WIDTH,HEIGHT,waveFile);
        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(displayPanel);
        pack();
        setVisible(true);
        repaint();
    }
    
}
