package exce;

public class GenderException extends Exception{
    public GenderException() {
        super("The given gender is invalid; ensure its value lies in {0, 1, 2}.");
    }
}
