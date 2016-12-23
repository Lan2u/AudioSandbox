package calculate;

/**
 * Created by Paul Lancaster on 17/12/2016
 */
public class ComplexNumber {
    public double real;
    public double imaginary;
    
    public double returnMagnitude(){
        return Math.sqrt(real*real+imaginary+imaginary);
    }
}
