package truckerboys.otto.driver;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;


/**Class which handles all the history. If you need the ask for breaks, driving time etc. this is where you do it.
 * Created by Martin on 24/09/2014.
 */
public class SessionHistory {

    private final Duration STANDARD_DAY_SESSION = Duration.standardHours(9);

    private final Duration STANDARD_DAILY_REST = Duration.standardHours(11);
    private final Duration REDUCED_DAILY_REST = Duration.standardHours(9);

    private final Duration REDUCED_WEEKLY_REST = Duration.standardHours(24);
    private final Duration STANDARD_WEEKLY_REST = Duration.standardHours(45);

    private final Duration SPLIT_DAY_REST_FIRST = Duration.standardHours(3);
    private final Duration SPLIT_DAY_REST_SECOND = Duration.standardHours(9);

    /**
     * The list of past sessions, sorted with the last session first.
     */
    private List<Session> sessions = new ArrayList<Session>();


    public SessionHistory(){

    }
    /**
     * Creates a new SessionHistory.
     *
     * @param sessions the history
     */
    public SessionHistory(List<Session> sessions) {
        for(Session s : sessions){
            addSession(s);
        }
    }

    /**
     * Adds a sessions to the history.
     *
     * @param session the past sessions
     */
    public void addSession(Session session) {
        if(sessions.size() == 0){
            sessions.add(session);
        }else{
            boolean added = false;
            for (Session s : sessions) {
                if (session.getStartTime().isAfter(s.getStartTime())) {
                    sessions.add(sessions.indexOf(s), session);
                    added = true;
                    break;
                }
            }
            if(!added){
                sessions.add(session);
            }
        }
    }

    /**
     * Returns the raw history, a list with sessions
     *
     * @return the list with all past sessions.
     */
    public List<Session> getSessions() {
        return sessions;
    }

    public void removeLastSession(){
        if(sessions.size() > 0 ){
            sessions.remove(0);
        }
    }


    /** Regulation checks. */

    /**
     * Returns true if the driver is currently  resting.
     *
     * @return true if driver is resting.
     */
    public boolean isResting() {
        return sessions.get(0) == null || sessions.get(0).getSessionType() == SessionType.RESTING;
    }

    /**
     * Returns the total driving time since the a specified instant in time.
     *
     * @param start The instant where to start calculations driving time.
     * @return The total driving time since Instant start.
     */
    public Duration getActiveTimeSince(Instant start) {

        Duration time = new Duration(0);

        //Loop all sessions

        for (Session session : sessions) {
            //Session is driving and has start-time after 'start'
            if (session.getSessionType() == SessionType.DRIVING) {

                if (session.getStartTime().isAfter(start)) {
                    //Add whole session to time
                    time = time.plus(session.getDuration());
                } else if (session.getStartTime().isBefore(start) && session.getEndTime().isAfter(start)) {
                    //Add the time from 'start' to end to time.
                    time = time.plus(new Duration(start, session.getEndTime()));
                }

            }
        }
        return time;
    }

    /**
     * Returns the sum of the duration of all past sessions until a rest-session longer than the specified duration.
     *
     * @param duration the break length which to search for.
     * @return the total time since a break longer than specified.
     */
    public Duration getActiveTimeSinceBreakLongerThan(Duration duration) {
        Duration time = new Duration(0);

        for (Session session : sessions) {
            if (session.getSessionType() == SessionType.RESTING) {
                if (session.getDuration().isLongerThan(duration)) {
                    return time;
                }
            } else {
                if (session.getSessionType() == SessionType.DRIVING) {
                    time = time.plus(session.getDuration());
                }
            }
        }

        return time;
    }

    /**
     * Returns the total number of extended days this week, week starting at the end of the last weekly rest.
     *
     * @return number of extended days.
     */
    public int getNumberOfExtendedDaysThisWeek() {
        int numberOfExtendedDays = 0;
        // Boolean making sure the loop doesn't register 2 extended days if the
        // driver didn't take a daily break inbetween them
        boolean breakBetweenExtendedDays = true;

        //The time driven a specific day (the day that the loop is currently on)
        Duration dailyTime = Duration.ZERO;

        //Get latest weekly break, if no break was found. We are in first week ever. Search all sessions.
        Instant latestWeeklyBreak;
        try {
            latestWeeklyBreak = getLatestWeeklyRestEndTime(sessions);
        } catch (NoValidBreakFound e) {
            latestWeeklyBreak = new Instant(0);
        }

        for (Session session : sessions) {
            //If 'session' is in this week (since "if after LAST weeks end", we're in this week.)
            if (latestWeeklyBreak.isBefore(session.getStartTime())) {

                //If session was a driving-session
                if (session.getSessionType() == SessionType.DRIVING) {
                    //Add this sessions time to dailyTime
                    dailyTime = dailyTime.plus(session.getDuration());
                } else if (session.getSessionType() == SessionType.RESTING && session.getDuration().isLongerThan(REDUCED_DAILY_REST)) {
                    if (getNumberOfReducedDailyRestsThisWeek() < 3 || !session.getDuration().isShorterThan(STANDARD_DAILY_REST)) {
                        //If we found a daily (or weekly) rest; reset time.
                        dailyTime = Duration.ZERO;
                        breakBetweenExtendedDays = true;
                    }
                }
                if (dailyTime.isLongerThan(STANDARD_DAY_SESSION) && breakBetweenExtendedDays) {

                    numberOfExtendedDays++;
                    breakBetweenExtendedDays = false;
                }
            }
        }

        return numberOfExtendedDays;
    }

    /**
     * Returns the total number of reduced daily rests since the last weekly rest.
     *
     * @return total reduced rests.
     */
    public int getNumberOfReducedDailyRestsThisWeek() {
        int numberOfReducedDailyRests = 0;

        //Get latest weekly break, if no break was found. We are in first week ever. Search all sessions.
        Instant latestWeeklyBreak;
        try {
            latestWeeklyBreak = getLatestWeeklyRestEndTime(sessions);
        } catch (NoValidBreakFound e) {
            latestWeeklyBreak = new Instant(0);
        }
        for (int i = 0; i < sessions.size() - 1; i++) {

            Session session = sessions.get(i);

            //If 'session' is in this week (since "if after LAST weeks end", we're in this week.)
            if (latestWeeklyBreak.isBefore(session.getStartTime())) {
                if (session.getSessionType() == SessionType.RESTING) {
                    //If it's longer than 9 hours and shorter than 11 hours it's a reduced daily rest.
                    if (session.getDuration().isLongerThan(REDUCED_DAILY_REST) && session.getDuration().isShorterThan(STANDARD_DAILY_REST)) {

                        //Check that the reduced break isn't a split daily rest which is a normal daily rest.
                        //If there's a rest longer than 3h before the one just found and occurs on the same day
                        //the one just found is a normal rest.

                        //Find the next daily break
                        for (int j = i + 1; j < sessions.size() - 1; j++) {
                            Session sessionTwo = sessions.get(j);
                            if (sessionTwo.getDuration().isLongerThan(SPLIT_DAY_REST_FIRST) &&
                                    sessionTwo.getDuration().isShorterThan(SPLIT_DAY_REST_SECOND) &&
                                    (getDateOfSession(j).getDayOfYear() == getDateOfSession(i).getDayOfYear())) {
                                numberOfReducedDailyRests--;
                            }
                        }

                        numberOfReducedDailyRests++;
                    }
                }
            }
        }

        return numberOfReducedDailyRests;

    }

    /**
     * Returns the instant of which the last daily break or break with interchangeable properties ended.
     * <p/>
     * All rest longer than a daily rest can due to regulations replace a daily rest.
     * So the last daily break isn't necessarily a standard daily break.
     * If it is the beginning of the week the method will return the last weekly rest.
     * <p/>
     * If there have been no valid weekly breaks the method will return epoch.
     *
     * @return The instant of the last daily break end.
     * @throws NoValidBreakFound When no valid weekly breaks could be found in the session history.
     */
    public Instant getLatestDailyRestEndTime() throws NoValidBreakFound {

        //Find a dailybreak and return the end time.
        //If its a break longer than 11h no further actions is needed
        //but if its reduced (9h) we need to check that its valid.
        Session session;


        for (int i = 0; i < sessions.size(); i++) {
            session = sessions.get(i);
            //If session is of type rest.
            if (session != null && session.getSessionType() == SessionType.RESTING) {
                //If the session is longer than or equal to 9 hours.
                if (!session.getDuration().isShorterThan(REDUCED_DAILY_REST)) {

                    //If the rest is a standard daily rest.
                    if (!session.getDuration().isShorterThan(STANDARD_DAILY_REST)) {
                        return new Instant(session.getEndTime());
                    } else {
                        //If not, it's a reduced one, check if that's valid. Since we can only have 3 per week.
                        if (getNumberOfReducedDailyRestsThisWeek() < 3) {
                            return new Instant(session.getEndTime());
                        } else if (true) /* It might be a split daily rest */ {

                            //If there's a rest longer than 3h before the one just found and occurs on the same day
                            //the one just found is a normal rest.

                            //Find the next daily break
                            for (int j = i + 1; j < sessions.size() - 1; j++) {
                                Session sessionTwo = sessions.get(j);
                                if (sessionTwo.getDuration().isLongerThan(SPLIT_DAY_REST_FIRST) &&
                                        sessionTwo.getDuration().isShorterThan(SPLIT_DAY_REST_SECOND) &&
                                        (getDateOfSession(j).getDayOfYear() == getDateOfSession(i).getDayOfYear())) {
                                    return new Instant(session.getEndTime());
                                }
                            }

                        } else /* It's not valid, check for the last valid one */ {

                            Session temp_session;
                            int validReducedBreakIn = getNumberOfReducedDailyRestsThisWeek() - 3;
                            for (int j = i; j < sessions.size(); j++) {

                                temp_session = sessions.get(j);
                                //If session is a resting-session
                                if (temp_session.getSessionType() == SessionType.RESTING) {

                                    //If session is a standard daily rest, we can return that.
                                    if (temp_session.getDuration().isLongerThan(STANDARD_DAILY_REST)) {
                                        return new Instant(temp_session.getEndTime());
                                    } else {
                                        //If not, count down until we have a valid reduced daily break.
                                        validReducedBreakIn--;

                                        if (validReducedBreakIn <= 0) {

                                            return new Instant(temp_session.getEndTime());
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }

        throw new

                NoValidBreakFound("No valid daily-break was found");

    }

    /**
     * Returns the end time of the last rest longer than specified.
     *
     * @param duration the specified length
     * @return the end time of the last break longer than specified.
     * @throws NoValidBreakFound
     */
    public Instant getEndTimeOfRestLongerThan(Duration duration) throws NoValidBreakFound {
        for (Session session : sessions) {
            if (session.getSessionType() == SessionType.RESTING && !session.getDuration().isShorterThan(duration)) {
                return session.getEndTime();
            }
        }
        throw new

                NoValidBreakFound("No rest longer than specified found");
    }

    /**
     * Returns the end time of the last rest longer and shorter than specified.
     *
     * @param min the specified minimum length
     * @param max the specified max length
     * @return the end time of the last break longer than specified.
     * @throws NoValidBreakFound
     */
    public Instant getEndTimeOfRestInTheInterval(Duration min, Duration max) throws NoValidBreakFound {
        for (Session session : sessions) {
            if (session.getSessionType() == SessionType.RESTING &&
                    !session.getDuration().isLongerThan(min) && session.getDuration().isShorterThan(max)) {
                return session.getEndTime();
            }
        }
        throw new

                NoValidBreakFound("No rest in the interval found");
    }

    /**
     * Returns the instant of which the last weekly break ended.
     * If there have been no valid weekly breaks the method will throw exception
     *
     * @return the instant of the last weekly break end.
     * @throws NoValidBreakFound When no valid weekly breaks could be found in the session history.
     */
    public Instant getLatestWeeklyRestEndTime(List<Session> sessions) throws NoValidBreakFound {

        Duration weeklyRest1;
        Duration weeklyRest2;

        Session session;

        for (int i = 0; i < sessions.size(); i++) {
            session = sessions.get(i);
            //Look through all rests sessions
            if (session.getSessionType() == SessionType.RESTING) {
                //If we found a reduced (or standard) weekly rest.
                if (!session.getDuration().isShorterThan(REDUCED_WEEKLY_REST)) {
                    weeklyRest1 = session.getDuration();

                    Session temp_session;
                    for (int j = i + 1; j < sessions.size(); j++) {
                        temp_session = sessions.get(j);
                        if (!temp_session.getDuration().isShorterThan(REDUCED_WEEKLY_REST)) {
                            weeklyRest2 = temp_session.getDuration();
                            //Found second weekly rest.

                            if (!weeklyRest1.isShorterThan(STANDARD_WEEKLY_REST)) {
                                //First week was a non-reduced weekly rest
                                return new Instant(session.getEndTime());
                            } else if (!weeklyRest2.isShorterThan(STANDARD_WEEKLY_REST)) {
                                //Second week was a non-reduced weekly rest
                                //If the weekly break after last standard one is reduced, return that.
                                return new Instant(session.getEndTime());
                            } else {
                                // None of the two last weeks were standard weekly-rests.
                                // The driver therefor broke a regulation. But we still need to find
                                // The last valid weekly break.

                                session = sessions.get(j);
                                weeklyRest1 = weeklyRest2;
                            }
                        }
                    }

                    return new Instant(session.getEndTime());
                }
            }
        }

        //      More specifically it will throw and exception and therefor getNumberOfExtendedDaysThisWeek() will have no week to check
        //      if days are in. It will therefor never return numberOfExtendedDays if driver has never taken a weekly break.
        throw new NoValidBreakFound("No valid weekly break was found.");
    }

    /**
     * Returns the instant of which the weekly break two weeks ago ended.
     * If there have been no valid weekly breaks the method will return epoch.
     *
     * @return the instant of the weekly break two weeks ago end.
     */
    public Instant getWeeklyRestEndTimeTwoWeeksAgo() throws NoValidBreakFound {
        List<Session> subSessions = new ArrayList<Session>();
        Session session;


        for (int i = 0; i < sessions.size(); i++) {
            session = sessions.get(i);
            if (session != null && session.getEndTime().isBefore(getLatestWeeklyRestEndTime(sessions))) {
                subSessions = sessions.subList(i, sessions.size() - 1);
            }
        }

        return getLatestWeeklyRestEndTime(subSessions);
    }

    /**
     * Returns the total driving time since the last daily break.
     *
     * @return the driving time since the last daily break.
     */
    public Duration getActiveTimeSinceLastDailyBreak() {
        //Get latest weekly break, if no break was found. We are in first week ever. Search all sessions.
        Instant latestDailyBreak;
        try {
            latestDailyBreak = getLatestDailyRestEndTime();
        } catch (NoValidBreakFound e) {
            latestDailyBreak = new Instant(0);
        }

        return getActiveTimeSince(latestDailyBreak);

    }

    /**
     * Returns the total driving time sine the last weekly break
     *
     * @return the total driving time
     */
    public Duration getActiveTimeSinceLastWeeklyBreak() {

        //Get latest weekly break, if no break was found. We are in first week ever. Search all sessions.
        Instant latestWeeklyBreak;
        try {
            latestWeeklyBreak = getLatestWeeklyRestEndTime(sessions);
        } catch (NoValidBreakFound e) {
            latestWeeklyBreak = new Instant(0);
        }

        return getActiveTimeSince(latestWeeklyBreak);
    }

    public Duration getActiveTimeSinceWeeklyBreakTwoWeeksAgo() {
        //Get latest weekly break, if no break was found. We are in first week ever. Search all sessions.
        Instant latestWeeklyBreak;
        try {
            latestWeeklyBreak = getWeeklyRestEndTimeTwoWeeksAgo();
        } catch (NoValidBreakFound e) {
            latestWeeklyBreak = new Instant(0);
        }
        return getActiveTimeSince(latestWeeklyBreak);

    }

    /**
     * Returns the Date of a given Sessions.
     * @param sessionIndex The index of the session
     * @return The DateTime of Session at index i.
     */
    public DateTime getDateOfSession(int sessionIndex) {
        return new DateTime(sessions.get(sessionIndex).getStartTime());
    }
}
