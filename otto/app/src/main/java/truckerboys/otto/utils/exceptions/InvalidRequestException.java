package truckerboys.otto.utils.exceptions;

/**
 * Thrown when an API responses that the request was invalid
 */
public class InvalidRequestException extends Exception {
    public InvalidRequestException(String input){
        super(input);
    }
}
