package graphic.display.drawpanels.dynamic;

import graphic.display.drawpanels.DrawPanel;

import javax.swing.*;

/**
 * Created by Paul Lancaster on 14/10/16
 */

abstract class DynamicDrawPanel extends DrawPanel implements Runnable{
    DynamicDrawPanel(int WIDTH, int HEIGHT) {
        super(WIDTH,HEIGHT);
    }
}
