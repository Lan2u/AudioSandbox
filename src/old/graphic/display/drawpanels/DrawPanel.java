package graphic.display.drawpanels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Paul Lancaster on 18/11/2016
 */
public abstract class DrawPanel extends JPanel{
    private BufferedImage currentFrame;
    
    public DrawPanel(int width, int height){
        setSize(width,height);
        currentFrame = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(currentFrame,0,0,this);
    }
    
    void setCurrentFrame(BufferedImage newFrame) {
        this.currentFrame = newFrame;
        repaint();
    }
}
