package truckerboys.otto.driver;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;


/**
 * Created by Martin on 24/09/2014.
 */
public class SessionHistory {

    private final Duration STANDARD_DAY_SESSION = Duration.standardHours(9);

    private final Duration STANDARD_DAILY_REST = Duration.standardMinutes(660);
    private final Duration REDUCED_DAILY_REST = Duration.standardMinutes(540);


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
     *
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

    /**
     * Returns the total number of extended days this week, week starting at monday 00:00.
     *
     * @return number of extended days.
     */
    public int getNumberOfExtendedDaysThisWeek() {

        //Calculate start of the week
        DateTime time = new DateTime(DateTime.now().minusDays(DateTime.now().getDayOfWeek() - 1));
        time = new DateTime(time.minusMillis(time.getMillisOfDay()));

        int numberOfExtendedDays = 0;

        //Loop through all the days on the week that starts with date "time" and calculate each day's total time.
        while (time.isBefore(DateTime.now().minusDays(1))) {

            if (getActiveTimeOnDate(time).isLongerThan(STANDARD_DAY_SESSION)) {
                numberOfExtendedDays++;
            }
            time = new DateTime(time.plusDays(1));

        }

        return numberOfExtendedDays;
    }

    /**
     * Returns the total driving time on a specified date.
     *
     * @param date
     * @return total time
     */
    public Duration getActiveTimeOnDate(DateTime date) {
        Duration time = new Duration(0);

        //Set the "date" to start at midnight
        date = new DateTime(date.minusMillis(date.getMillisOfDay()));


        //Loop through the sessions and adds the ones that fits the interval of the specified day.
        for (Session s : sessions) {
            time.plus(s.getEndTime().isAfter(date) && s.getEndTime().isBefore(date.plusDays(1)) ? s.getDuration() : new Duration(0));
        }
        return time;
    }

    public Duration getActiveTimeSinceLastDailyBreak() {
        //TODO FIx
        Duration timeSinceDailyBreak = getActiveTimeSinceBreakLongerThan(REDUCED_DAILY_REST);

        return null;
    }


}
