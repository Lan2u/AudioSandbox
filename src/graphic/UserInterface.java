package graphic;

import sound.files.WaveFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Paul Lancaster on 01/11/2016
 */
public class UserInterface extends JFrame {
    
    private DrawPanel drawPanel;

    public UserInterface(int WIDTH, int HEIGHT){
        drawPanel = new DrawPanel(WIDTH,HEIGHT);
        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(drawPanel);
        pack();
        setVisible(true);
        repaint();
    }
    public void setImage(BufferedImage image){
        drawPanel.setCurrentFrame(image);
    }

    public void startDisplaying(WaveFile waveFile, double seconds) {
        DisplayThread displayThread = new DisplayThread(drawPanel);
        displayThread.dataToDisplay(waveFile.getSamples(seconds), waveFile.getSampleRate(), waveFile.getChannels());
        displayThread.start();
    }
}
