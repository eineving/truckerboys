package truckerboys.otto.utils.exceptions;

/**
 * Thrown when a connection can not be established to an API
 */
public class NoConnectionException extends Exception {
    public NoConnectionException(String input){
        super(input);
    }
}
