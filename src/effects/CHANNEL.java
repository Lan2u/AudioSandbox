package effects;

/**
 * Represents audio channels in cases where you might want to specify to use both channels rather than just 1
 * Created by Paul Lancaster on 31/12/2016
 */
public enum CHANNEL {
    one, two, both;
    
    /**
     * @return The integer value which represents the channel: one = 1, two = 2, both = 0
     */
    public int getInt() {
        switch (this){
            case one:
                return 1;
            case two:
                return 2;
            case both:
                return 0;
        }
        return -1;
    }
}
