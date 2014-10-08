package truckerboys.otto.driver;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;


/**
 * Created by Martin on 24/09/2014.
 */
public class SessionHistory {

    //TODO: Regulation 9.1

    private final Duration ZERO_DURATION = new Duration(0);
    private final Duration STANDARD_DAY_SESSION = Duration.standardHours(9);

    private final Duration STANDARD_DAILY_REST = Duration.standardHours(11);
    private final Duration REDUCED_DAILY_REST = Duration.standardHours(9);

    private final Duration REDUCED_WEEKLY_REST = Duration.standardHours(24);
    private final Duration STANDARD_WEEKLY_REST = Duration.standardHours(45);

    private final Instant NO_RECORD_FOUND = new Instant(0);

    /**
     * The list of past sessions, sorted with the last session first.
     */
    private List<Session> sessions = new ArrayList<Session>();

    /**
     * Checks if there exists a break longer or equal to a specified time in
     * an interval [now, backLimit]
     *
     * @param breakTime The length of the time to check for
     * @param backLimit The length of the interval to check back to.
     * @return True if break exists
     */
    public boolean existBreakLonger(Duration breakTime, Duration backLimit){
        //Loop through all sessions and check break time between each one.
        for(int i = 0; i < sessions.size(); i++){
            //If previous session ended before (or same time as) limit end.
            if(sessions.get(i-1).getEndTime().getMillis() >= backLimit.getMillis()) {
                //If "start-time (current session)" - "end-time (previous session)" >= breakTime
                if (sessions.get(i).getStartTime().getMillis() - sessions.get(i - 1).getEndTime().getMillis() >= breakTime.getMillis()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns weither the driver is currently driving or not.
     * @return True if driver is currently driving.
     */
    public boolean isDriving(){
        return sessions.get(0).isActive();
    }

    /**
     * Get the duration since current break was started.
     * @return Duration since break was started.
     * @throws CurrentlyNotOnBreakException if driver currently isn't on a break.
     */
    public Duration getTimeSinceBreakStart() throws CurrentlyNotOnBreakException{
        if(!isDriving()) {
            //Current time in millis minus the time in millis since last active session ended.
            return new Duration((new Instant().getMillis()) - sessions.get(1).getEndTime().getMillis());
        }

        throw new CurrentlyNotOnBreakException();
    }

    /**
     * Returns the active time since the driver last took a break.
     *
     * @return Duration since last break, Duration.ZERO if not on break.
     */
    public Duration getActiveTimeSinceLastBreak(){
        if(isDriving()){
            return sessions.get(0).getDuration();
        }
        return Duration.ZERO;
    }

    /**
     * Adds a sessions to the history.
     *
     * @param session the past sessions
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
     * Returns the total driving time since the a specified instant in time.
     *
     * @param start the instant where to start calculations driving time.
     * @return the total driving time since Instant start.
     */
    public Duration getActiveTimeSince(Instant start) {

        Duration time = new Duration(0);


        //Loop through the sessions and add the durations.
        //If the first sessions is active, add the time so far, dealt by the property of session it self.
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getStartTime().isBefore(start)) {
                time = time.plus(sessions.get(i).getDuration());
            }
            if (sessions.get(i + 1).getEndTime().isBefore(start)) {
                time = time.plus(new Duration(start, sessions.get(i + 1).getEndTime()));
            }

            //Need to check for last index to avoid outofbounds exception.
            if (i == sessions.size() - 1) {
                break;
            }
        }
        return time;
    }

    /**
     * Returns the sum of the duration of all past sessions until a break longer than the specified duration.
     *
     * @param duration the break length which to search for.
     * @return the total time since a break longer than specified.
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
            time = time.plus(sessions.get(i).getDuration());

            //Need to check for last index to avoid outofbounds exception.
            if (i == sessions.size() - 1 || new Duration(sessions.get(i + 1).getEndTime(), sessions.get(i).getStartTime()).isLongerThan(duration)) {
                break; // If we find a long enough break we will break the loop;
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
        Duration time = new Duration(ZERO_DURATION);


        for (int i = 0; i < sessions.size() - 1; i++) {
            Duration tempBreak = new Duration(sessions.get(i).getStartTime(), sessions.get(i + 1).getEndTime());

            //Add each session's duration until we find a daily rest.
            time = time.plus(sessions.get(i).getDuration());

            //Look for a daily rest otherwise continue to the next session in the list.
            if (tempBreak.isLongerThan(REDUCED_DAILY_REST) && tempBreak.isShorterThan(REDUCED_WEEKLY_REST)) {

                //If the total time up until the daily rest is greater than 9h, declare it as a Extended day.
                if (time.isLongerThan(STANDARD_DAY_SESSION)) {
                    numberOfExtendedDays++;
                    //TODO catch days longer than allowed and report?
                }

            }
            //Look for the start of the week.
            if (sessions.get(i).getStartTime().equals(getLatestWeeklyRestEndTime(sessions)) ||
                    sessions.get(i).getStartTime().isBefore(getLatestWeeklyRestEndTime(sessions))) {

                //check the total time of the previous day (the first day that week) and terminate.
                if (time.isLongerThan(STANDARD_DAY_SESSION)) {
                    numberOfExtendedDays++;
                    //TODO catch days longer than allowed and report?
                }
                return numberOfExtendedDays;
            }

            //Reset time and start over with the next day.
            time = new Duration(ZERO_DURATION);
        }
        //No weekly break where found. Means that the history contains no weekly breaks yet.
        //So terminate with what we found so far.
        return numberOfExtendedDays;
    }

    /**
     * Returns the total number of reduced daily rests since the last weekly rest.
     *
     * @return total reduced rests.
     */
    public int getNumberOfReducedDailyRestsThisWeek() {
        //TODO: Regulation 7.2.2
        int numberOfReducedDailyRests = 0;

        if (!sessions.get(0).isActive()) {

            //If the time since last session is a reduced daily break.
            Duration ongoingBreak = new Duration(sessions.get(0).getEndTime(), new Instant());
            if (ongoingBreak.isLongerThan(REDUCED_DAILY_REST) && ongoingBreak.isShorterThan(STANDARD_DAILY_REST)) {
                numberOfReducedDailyRests++;
            }
        }
        //Loop through the sessions to find breaks longer than 9h and shorter than 11h.
        //Stop when you find a weekly break.
        for (int i = 0; i < sessions.size() - 2; i++) {

            Duration tempBreak = new Duration(sessions.get(i).getStartTime(), sessions.get(i + 1).getEndTime());
            if (tempBreak.isLongerThan(REDUCED_DAILY_REST) && tempBreak.isShorterThan(STANDARD_DAILY_REST)) {
                //TODO Check for split daily break, regulation 7.2.2
                numberOfReducedDailyRests++;
            }
            //Look for weekly rests.
            //If the current session in the loop is before the latest weekly rest.
            if (getLatestWeeklyRestEndTime(sessions).isAfter(sessions.get(i + 1).getEndTime())) {
                return numberOfReducedDailyRests;
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
     * @return the instant of the last daily break end.
     */
    public Instant getLatestDailyRestEndTime() {

        //Find a dailybreak and return the end time.
        //If its a break longer than 11h no further actions is needed
        //but if its reduced (9h) we need to check that its valid.

        Duration dailyRest;

        //Find the two previous daily rest, normal or reduced.
        //Check which if the first one is normal or reduced.
        for (int i = 0; i < sessions.size() - 1; i++) {
            dailyRest = new Duration(sessions.get(i + 1).getEndTime(), sessions.get(i).getStartTime());
            if (dailyRest.isLongerThan(REDUCED_DAILY_REST)) {

                //The last rest found, check if its Standard or longer.
                if (dailyRest.isLongerThan(STANDARD_DAILY_REST)) {
                    return new Instant(sessions.get(i).getStartTime());
                } else {
                    //if its not, its a reduced one, check if its valid.
                    //simply check how many reduced already occurred this week.
                    if (getNumberOfReducedDailyRestsThisWeek() < 2) {
                        return new Instant(sessions.get(i).getStartTime());
                    } else {
                        //if its not valid we need to find the last valid one.

                        int validReducedBreakIn = getNumberOfReducedDailyRestsThisWeek() - 2;
                        for (int j = i + 1; j < sessions.size() - 1; j++) {
                            //Find the next break, if its a normal one then return that.
                            //If its not continue until we find one or until we find the last valid reduced on.
                            //TODO: Catch violation?

                            dailyRest = new Duration(sessions.get(j + 1).getEndTime(), sessions.get(j).getStartTime());
                            if (dailyRest.isLongerThan(STANDARD_DAILY_REST)) {
                                return new Instant(sessions.get(j).getStartTime());
                            } else {
                                //
                                validReducedBreakIn--;
                                if (validReducedBreakIn == 0) {
                                    //last valid reduced break found
                                    return new Instant(sessions.get(j).getStartTime());
                                }
                            }

                        }

                    }
                }
            }
        }
        //No valid daily break found
        return NO_RECORD_FOUND;
    }

    /**
     * Returns the instant of which the last weekly break ended.
     * If there have been no valid weekly breaks the method will return epoch.
     *
     * @return the instant of the last weekly break end.
     */
    public Instant getLatestWeeklyRestEndTime(List<Session> sessions) {
        //TODO: Regulation 9.6
        //TODO: Regulation 10

        Duration weeklyRest1;
        Duration weeklyRest2;

        //Find the two previous weekly rest, normal or reduced.
        //Check which if the first one is normal or reduced.
        for (int i = 0; i < sessions.size() - 1; i++) {
            weeklyRest1 = new Duration(sessions.get(i + 1).getEndTime(), sessions.get(i).getStartTime());
            if (weeklyRest1.isLongerThan(REDUCED_WEEKLY_REST)) {
                //One weekly rest found, find the next one...

                for (int j = i + 1; i < sessions.size() - 1; j++) {
                    weeklyRest2 = new Duration(sessions.get(i + 1).getEndTime(), sessions.get(i).getStartTime());
                    if (weeklyRest2.isLongerThan(REDUCED_WEEKLY_REST)) {
                        //Rest two found.

                        //If the first is a normal then no further actions is needed.
                        if (weeklyRest1.isLongerThan(STANDARD_WEEKLY_REST)) {
                            return new Instant(sessions.get(i).getStartTime());
                        }
                        //if its not but second one is no further actions is needed.
                        else if (weeklyRest2.isLongerThan(STANDARD_WEEKLY_REST)) {
                            return new Instant(sessions.get(i).getStartTime());
                        } else {
                            //Both the two contended rests are reduced ones and there for weeklyRest1 is not valid .
                            //Should only happen if the driver chooses to ignore the app.
                            //Check if weeklyRest2 is valid by examine "weeklyRest3"
                            i = j;
                            weeklyRest1 = weeklyRest2;
                        }
                    }

                }
                //Only one rest found or only one valid.
                //return that one.
                return new Instant(sessions.get(i).getStartTime());
            }
        }
        //No weekly rests found
        //return epoch, think about this a bit more, don't forget javadoc.
        return NO_RECORD_FOUND;
    }

    /**
     * Returns the instant of which the weekly break two weeks ago ended.
     * If there have been no valid weekly breaks the method will return epoch.
     *
     * @return the instant of the weekly break two weeks ago end.
     */
    public Instant getWeeklyRestEndTimeTwoWeeksAgo() {
        List<Session> subSessions = new ArrayList<Session>();

        for (int i = 0; i < sessions.size() - 1; i++) {
            if (sessions.get(i).getStartTime().equals(getLatestWeeklyRestEndTime(sessions))) {
                subSessions = sessions.subList(i+1, sessions.size() - 1);
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
        return getActiveTimeSince(getLatestDailyRestEndTime());

    }

    /**
     * Returns the total driving time sine the last weekly break
     *
     * @return the total driving time
     */
    public Duration getActiveTimeSinceLastWeeklyBreak() {
        return getActiveTimeSince(getLatestWeeklyRestEndTime(sessions));
    }

    public Duration getActiveTimeSinceWeeklyBreakTwoWeeksAgo() {
        return getActiveTimeSince(getWeeklyRestEndTimeTwoWeeksAgo());
    }
}
