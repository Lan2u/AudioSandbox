package graphic.display;

import audio.files.WaveFile;
import graphic.display.drawpanels.DrawPanel;
import graphic.display.drawpanels.dynamic.FrequencyDisplayPanel;
import graphic.generation.DisplayThread;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Created by Paul Lancaster on 01/11/2016
 */
public class UserInterface extends JFrame {
    
    private FrequencyDisplayPanel displayPanel;

    public UserInterface(int WIDTH, int HEIGHT){
        displayPanel = new FrequencyDisplayPanel(WIDTH,HEIGHT);
        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(displayPanel);
        pack();
        setVisible(true);
        repaint();
    }

    public void startDisplaying(WaveFile waveFile, double seconds) {
        Thread thread = new Thread(displayPanel);
        thread.start();
    }
}
