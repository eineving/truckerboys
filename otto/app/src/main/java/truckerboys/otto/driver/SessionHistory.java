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
     * Returns weither the driver is currently driving or not.
     * @return True if driver is currently driving.
     */
    public boolean isDriving(){
        return sessions.get(0).getSessionType() == SessionType.DRIVING;
    }

    /**
     * Checks if there exists a rest-session longer or equal to a specified time in
     * an interval [now, backLimit]
     *
     * @param restTime The length of the time to check for
     * @param backLimit The length of the interval to check back to.
     * @return True if break exists
     */
    public boolean existRestLonger(Duration restTime, Duration backLimit){

        //Loop through all sessions
        for(Session session : sessions){
            //If session is of type RESTING
            if(session.getSessionType() == SessionType.RESTING){
                //If break is longer than specified and within interval
                if(!session.getDuration().isShorterThan(restTime) && session.getEndTime().getMillis() >= backLimit.getMillis()){
                    return true;
                }
            }
        }

        return false;
    }
    /*
     * Get the duration since current break was started.
     *
     * @return Duration since break was started.
     * @throws CurrentlyNotOnRestException if driver currently isn't on a break.
     */
    public Duration getTimeSinceRestStart() throws CurrentlyNotOnRestException {
        if(sessions.get(0).getSessionType() == SessionType.RESTING) {
            return sessions.get(0).getDuration();
        }

        throw new CurrentlyNotOnRestException();
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
     * @param start The instant where to start calculations driving time.
     * @return The total driving time since Instant start.
     */
    public Duration getActiveTimeSince(Instant start) {

        Duration time = new Duration(0);

        //Loop all sessions
        for(Session session : sessions){
            //Session is driving and has start-time after 'start'
            if(session.getSessionType() == SessionType.DRIVING){

                if(session.getStartTime().isAfter(start)) {
                    //Add whole session to time
                    time = time.plus(session.getDuration());
                } else if(session.getStartTime().isBefore(start) && session.getEndTime().isAfter(start)) {
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

        for(Session session : sessions){
            if(session.getSessionType() == SessionType.RESTING){
                if(session.getDuration().isLongerThan(duration)){
                    return time;
                }
            } else {
                if(session.getSessionType() == SessionType.DRIVING){
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

        //The time driven a specific day (the day that the loop is currently on)
        Duration dailyTime = Duration.ZERO;

        for(Session session : sessions){
            //If 'session' is in this week (since "if after LAST weeks end", we're in this week.)
            if(getLatestWeeklyRestEndTime(sessions).isBefore(session.getStartTime().getMillis())){

                //If session was a driving-session
                if(session.getSessionType() == SessionType.DRIVING){
                    //Add this sessions time to dailyTime
                    dailyTime = dailyTime.plus(session.getDuration());
                } else if(session.getSessionType() == SessionType.RESTING && session.getDuration().isLongerThan(REDUCED_DAILY_REST)) {
                    //If we found a daily (or weekly) rest; reset time.
                    dailyTime = Duration.ZERO;
                }

                if(dailyTime.isLongerThan(STANDARD_DAY_SESSION)){
                    numberOfExtendedDays++;
                    //Need to reset dailyTime, since we don't want to add another extended day if next session isn't a daily rest.
                    dailyTime = Duration.ZERO;
                }
            }
            /*
             * TODO Bug will occur if driver does extended day, works, and then takes another extended day (without doing a daily rest.)
             *      More specifically it will register two extended days, while it's rather one extended day and a violation of the rules.
             *      Not 2 extended days.
             */
        }

        return numberOfExtendedDays;

        //TODO Walkthrough with Pegelow
        /*
        for (int i = 0; i < sessions.size() - 1; i++) {
            Duration tempBreak = new Duration(sessions.get(i).getStartTime(), sessions.get(i + 1).getEndTime());

            //Add each session's duration until we find a daily rest.
            dailyTime = dailyTime.plus(sessions.get(i).getDuration());

            //Look for a daily rest otherwise continue to the next session in the list.
            if (tempBreak.isLongerThan(REDUCED_DAILY_REST) && tempBreak.isShorterThan(REDUCED_WEEKLY_REST)) {

                //If the total time up until the daily rest is greater than 9h, declare it as a Extended day.
                if (dailyTime.isLongerThan(STANDARD_DAY_SESSION)) {
                    numberOfExtendedDays++;
                    //TODO catch days longer than allowed and report?
                }

            }
            //Look for the start of the week.
            if (sessions.get(i).getStartTime().equals(getLatestWeeklyRestEndTime(sessions)) ||
                    sessions.get(i).getStartTime().isBefore(getLatestWeeklyRestEndTime(sessions))) {

                //check the total time of the previous day (the first day that week) and terminate.
                if (dailyTime.isLongerThan(STANDARD_DAY_SESSION)) {
                    numberOfExtendedDays++;
                    //TODO catch days longer than allowed and report?
                }
                return numberOfExtendedDays;
            }

            //Reset time and start over with the next day.
            dailyTime = new Duration(ZERO_DURATION);
        }
        //No weekly break where found. Means that the history contains no weekly breaks yet.
        //So terminate with what we found so far.
        return numberOfExtendedDays;
        */
    }

    /**
     * Returns the total number of reduced daily rests since the last weekly rest.
     *
     * @return total reduced rests.
     */
    public int getNumberOfReducedDailyRestsThisWeek() {
        //TODO: Regulation 7.2.2
        int numberOfReducedDailyRests = 0;

        for(Session session : sessions){
            //If 'session' is in this week (since "if after LAST weeks end", we're in this week.)
            if(getLatestWeeklyRestEndTime(sessions).isBefore(session.getStartTime().getMillis())) {
                if (session.getSessionType() == SessionType.RESTING) {
                    //If it's longer than 9 hours and shorter than 11 hours it's a reduced daily rest.
                    if (session.getDuration().isLongerThan(REDUCED_DAILY_REST) && session.getDuration().isShorterThan(STANDARD_DAILY_REST)) {
                        //We also need to make sure the rest isn't in progress.
                        if (!session.isActive()) {
                            numberOfReducedDailyRests++;
                        }
                    }
                }
            }
        }

        return numberOfReducedDailyRests;

        //TODO Walkthrough with Pegelow
        /*
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
        */
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
        Session session;

        /*for(Session session : sessions){*/
        for(int i = 0; i < sessions.size(); i++){
            session = sessions.get(i);
            //If session is of type rest.
            if(session.getSessionType() == SessionType.RESTING){
                //If the session is longer than or equal to 9 hours.
                if(!session.getDuration().isShorterThan(REDUCED_DAILY_REST)){

                    //If the rest is a standard daily rest.
                    if(!session.getDuration().isShorterThan(STANDARD_DAILY_REST)){
                        return new Instant(session.getEndTime()); //TODO Should be EndTime not StartTime? (Applies to the ones below aswell)
                    } else {
                        //If not, it's a reduced one, check if that's valid. Since we can only have 3 per week.
                        if(getNumberOfReducedDailyRestsThisWeek() < 2) { //TODO Should be 3?   →    ^
                            return new Instant(session.getEndTime());
                        } else /* It's not valid, check for the last valid one */{

                            Session temp_session;
                            int validReducedBreakIn = getNumberOfReducedDailyRestsThisWeek() - 2;
                            for(int j = i; j < sessions.size(); j++){

                                temp_session = sessions.get(j);
                                //If session is a resting-session
                                if(temp_session.getSessionType() == SessionType.RESTING){

                                    //If session is a standard daily rest, we can return that.
                                    if(temp_session.getDuration().isLongerThan(STANDARD_DAILY_REST)){
                                        return new Instant(temp_session.getEndTime());
                                    } else {
                                        //If not, count down untill we have a valid reduced daily break.
                                        validReducedBreakIn--;
                                        if(validReducedBreakIn <= 0){
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

        //TODO Throw exception instead.
        return NO_RECORD_FOUND;

        //TODO Walkthrough with Pegelow
        /*
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
        */
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

        Session session;

        for(int i = 0; i < sessions.size(); i++){
            session = sessions.get(i);
            //Look through all rests sessions
            if(session.getSessionType() == SessionType.RESTING){
                //If we found a reduced (or standard) weekly rest.
                if(!session.getDuration().isShorterThan(REDUCED_WEEKLY_REST)){
                    weeklyRest1 = session.getDuration();

                    Session temp_session;
                    for(int j = i; j < sessions.size(); j++) {
                        temp_session = sessions.get(j);
                        if(!temp_session.getDuration().isShorterThan(REDUCED_WEEKLY_REST)){
                            weeklyRest2 = temp_session.getDuration();
                            if(!weeklyRest1.isShorterThan(STANDARD_WEEKLY_REST)){
                                return new Instant(session.getEndTime());
                            } else if(!weeklyRest2.isShorterThan(STANDARD_WEEKLY_REST)){
                                return new Instant(temp_session.getEndTime());
                            } else {
                                //TODO This operation doesn't seem valid (in Pegelows code)
                                //     But im way to tired to see if it's actually valid or not,
                                //     I'll have someone else look at it or do it later. Goodnight.
                            }
                        }
                    }

                    return new Instant(session.getEndTime());
                }
            }
        }
        //No weekly rests found
        //TODO Throw exception
        return NO_RECORD_FOUND;

        /*
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
        */
    }

    //TODO Rewrite to fit new SessionHistory class.
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
                subSessions = sessions.subList(i + 1, sessions.size() - 1);
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
