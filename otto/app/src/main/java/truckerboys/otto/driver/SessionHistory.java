package truckerboys.otto.driver;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;


/**
 * Created by Martin on 24/09/2014.
 */
public class SessionHistory {
    /**
     * The list of past sessions, sorted with the last session first.
     */
    private List<Session> sessions = new ArrayList<Session>();

    /**
     * Adds a sessions to the history.
     *
     * @param session
     */
    public void addSession(Session session) {
        for (Session s : sessions) {
            if (session.getEndTime().isAfter(s.getEndTime())) {
                sessions.add(sessions.indexOf(s), session);
                break;
            }
        }
    }

    /**
     * Returns the sum of the duration of all past sessions until a break longer than the speciefied duration.
     * @param duration
     * @return
     */
    public Duration getActiveTimeSinceBreakLongerThan(Duration duration) {
        Duration time = new Duration(0);

        if (!sessions.get(0).isActive()) {
            //If more time than the specified duration has passed since the last session.
            if (new Duration(sessions.get(0).getEndTime(), new Instant()).isLongerThan(duration)) {
                return time;
            }
        }

        //Loop through the sessions and add the durations up to the first break longer than duration.
        for (int i = 0; i < sessions.size(); i++) {
            time.plus(sessions.get(i).getDuration());

            //Need to check for last index to avoid outofbounds exception.
            if (i == sessions.size() - 1 || new Duration(sessions.get(i + 1).getEndTime(), sessions.get(i).getStartTime()).isLongerThan(duration)) {
                break; // If we find a long enough break we will break the loop;
            }
        }
        return time;
    }


}
