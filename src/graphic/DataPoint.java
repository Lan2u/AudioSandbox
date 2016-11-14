package graphic;

import java.awt.*;

/**
 * Created by Paul Lancaster on 14/11/16
 * As always https://docs.oracle.com/javase/ used for reference
 */
public class DataPoint extends Point{
    private Color colour;

    DataPoint(int x, int y){
        this(x,y,Color.CYAN);
    }

    DataPoint(int x, int y, Color colour){
        setLocation(x,y);
        setColour(colour);
    }

    DataPoint(){
        this(0,0);
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public Color getColour() {
        return colour;
    }

    public void moveRight(int amount){
        x = x + amount;
    }

    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }

    public int getIntX(){
        return (int)Math.ceil(super.getX());
    }

    public int getIntY(){
        return (int)Math.ceil(super.getY());
    }
}
