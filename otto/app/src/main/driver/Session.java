package truckerboys.otto.driver;

import org.joda.time.Duration;
import org.joda.time.Instant;
/**
 * Created by Martin on 17/09/2014.
 */
public class Session {

    private boolean active = false;
    private Instant start, end;

    /**
     * Creates a new Session with the current time as a start Time.
     */
    public Session(){
        start = new Instant();
        active = true;
    }

    /**
     * Starts a session with a specified start time.
     * @param start The start time, the Instant must have occured, no future Instants allowed.
     */
    public Session(Instant start){
        if(start.isBefore(new Instant())){
            this.start = start;
        }else{
            start = new Instant();
        }
        active = true;
    }

    /**
     * Ends the session.
     */
    public void end(){
        end = new Instant();
        active = false;
    }

    /**
     * Returns the elapsed time from the start time until now.
     * @return The elapsed time.
     */
    public Duration getDuration(){
        if(active){
            return new Duration(start, new Instant());
        }else{
            return new Duration(start, end);
        }
    }

    /**
     * Returns the status of the session.
     * @return true if the session is in progress, false if the session has stopped.
     */
    public boolean isActive(){
        return active;
    }

}
