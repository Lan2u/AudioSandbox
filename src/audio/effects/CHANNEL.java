package audio.effects;

/**
 * Created by Paul Lancaster on 31/12/2016
 */
public enum CHANNEL {
    one, two, both;
    
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
