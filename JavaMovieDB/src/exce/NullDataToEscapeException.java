package exce;

public class NullDataToEscapeException extends Exception {
    public NullDataToEscapeException() {
        super("Input data cannot be null");
    }
}
