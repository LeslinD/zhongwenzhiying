package exce;

public class homeTypeException extends Exception{
    public homeTypeException() {
        super("The given homepage type is invalid; ensure its value lies in {'popular','latest'}.");
    }
}
