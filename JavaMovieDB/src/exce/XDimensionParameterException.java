package exce;

public class XDimensionParameterException extends Exception {
    public XDimensionParameterException() {
        super("The X Dimension is greater than the actual number of X Labels.");
    }
}
