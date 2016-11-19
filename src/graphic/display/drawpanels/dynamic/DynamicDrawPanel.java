package graphic.display.drawpanels.dynamic;

import javax.swing.*;

/**
 * Created by Paul Lancaster on 14/10/16
 */

abstract class DynamicDrawPanel extends JPanel implements Runnable{
    DynamicDrawPanel(int WIDTH, int HEIGHT) {
        setSize(WIDTH,HEIGHT);
    }


}
