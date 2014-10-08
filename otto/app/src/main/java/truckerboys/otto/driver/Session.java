package truckerboys.otto.driver;

import org.joda.time.Duration;
import org.joda.time.Instant;
/**
 * Created by Martin on 17/09/2014.
 */
public class Session {

    private final SessionType type;
    private boolean active = false;
    private Instant start, end;

    /**
     * Creates a new Session with the current time as a start Time.
     */
    public Session(SessionType type){
        start = new Instant();
        active = true;
        this.type = type;
    }

    /**
     * Starts a session with a specified start time.
     *
     * @param type
     * @param start The start time, the Instant must have occurred, no future Instants allowed.
     */
    public Session(SessionType type, Instant start) {
        this.type = type;
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
     * Returns the type of the Session.
     * @return The type of the session.
     */
    public SessionType getSessionType(){
        return type;
    }



    /**
     * Returns the end time of the session
     *
     * If the Session is active, the method will return the current Instant as the end time.
     *
     * @return Now if active, the end time if the session is finished.
     */
    public Instant getEndTime() {
        if(active){
            return new Instant();
        }else{
            return end;
        }
    }
}
