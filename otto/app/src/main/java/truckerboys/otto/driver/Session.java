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
     *
     * @param start The start time, the Instant must have occurred, no future Instants allowed.
     */
    public Session(Instant start) {
        if (start.isBefore(new Instant())) {
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
     *
     * @return The elapsed time.
     */
    public Duration getDuration() {
        if (active) {
            return new Duration(start, new Instant());
        }else{
            return new Duration(start, end);
        }
    }

    /**
     * Returns the status of the session.
     *
     * @return true if the session is in progress, false if the session has stopped.
     */
    public boolean isActive(){
        return active;
    }

    /**
     * Returns the start time of the session
     *
     * @return the start time of the session as an instant.
     */
    public Instant getStartTime() {
        return start;
    }

    /**
     * Returns the end time of the session
     *
     * This is actually extremely retarded, since it will return NULL when the session is active.
     * A possible fix is to Split the class into a Session, without this method, and a
     * PastSession extends session which adds this method, and takes a Session as a parameter to the constructor.
     * Thoughts?
     *
     *
     *
     * @return the end time of the session as an instant.
     */
    public Instant getEndTime() {
        //TODO Do as described as above.
        return end;
    }
}
