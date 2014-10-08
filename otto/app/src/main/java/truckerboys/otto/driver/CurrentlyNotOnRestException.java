package truckerboys.otto.driver;

/**
 * Created by Simon Petersson on 2014-10-07.
 *
 * Represents that the driver currently isn't on a break.
 */
public class CurrentlyNotOnRestException extends Exception {

    public CurrentlyNotOnRestException(){
        this("");
    }

    public CurrentlyNotOnRestException(String message){
        super(message);
    }
}
