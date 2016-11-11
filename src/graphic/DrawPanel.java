package graphic;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Paul Lancaster on 14/10/16
 */

class DrawPanel extends JPanel {
    private BufferedImage currentFrame;

    DrawPanel(int width, int height){
        setSize(width,height);
        currentFrame = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.clearRect(0,0,getWidth(),getHeight());
        g.drawImage(currentFrame, 0,0, this);
    }


    // FIXME THe image still doesn't update, I think the reason for this that all the generated images are exactly the same
    void setCurrentFrame(BufferedImage newFrame) {
        this.currentFrame = newFrame;
        repaint();
    }
}
