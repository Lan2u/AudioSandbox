package effects;

/**
 * Created by Paul Lancaster on 17/05/2017
 *
 * @author paul
 */
class ValueNotFoundException extends RuntimeException{
    ValueNotFoundException(String s) {
        super(s);
    }
}
