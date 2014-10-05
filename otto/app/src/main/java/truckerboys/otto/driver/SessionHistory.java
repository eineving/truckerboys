package truckerboys.otto.driver;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;


/**
 * Created by Martin on 24/09/2014.
 */
public class SessionHistory {

    private final Duration ZERO_DURATION = new Duration(0);
    private final Duration STANDARD_DAY_SESSION = Duration.standardHours(9);

    private final Duration STANDARD_DAILY_REST = Duration.standardHours(11);
    private final Duration REDUCED_DAILY_REST = Duration.standardHours(9);

    private final Duration REDUCED_WEEKLY_REST = Duration.standardHours(24);
    private final Duration STANDARD_WEEKLY_REST = Duration.standardHours(45);


    /**
     * The list of past sessions, sorted with the last session first.
     */
    private List<Session> sessions = new ArrayList<Session>();

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
        //If the first sessions is active, add the time so far, dealt by the propoty of session it self.
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
            if (sessions.get(i).getStartTime().equals(getLatestWeeklyRestEndTime())) {
                //getLatestWeeklyRestEndTime might return null but that should be okay.

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
        int numberOfExtendedDays = 0;

        if (!sessions.get(0).isActive()) {

            //If the time since last session is a reduced daily break.
            Duration ongoingBreak = new Duration(sessions.get(0).getEndTime(), new Instant());
            if (ongoingBreak.isLongerThan(REDUCED_DAILY_REST) && ongoingBreak.isShorterThan(STANDARD_DAILY_REST)) {
                numberOfExtendedDays++;
            }
        }
        //Loop through the sessions to find breaks longer than 9h and shorter than 11h.
        //Stop when you find a weekly break.
        for (int i = 0; i < sessions.size() - 2; i++) {

            Duration tempBreak = new Duration(sessions.get(i).getStartTime(), sessions.get(i + 1).getEndTime());
            if (tempBreak.isLongerThan(REDUCED_DAILY_REST) && tempBreak.isShorterThan(STANDARD_DAILY_REST)) {
                //TODO Check for split daily break, regulation 7.2.2
                numberOfExtendedDays++;
            }
            //Look for weekly rests.
            //If the current session in the loop is before the latest weekly rest.
            if (getLatestWeeklyRestEndTime().isAfter(sessions.get(i + 1).getEndTime())) {
                return numberOfExtendedDays;
            }
        }

        return numberOfExtendedDays;
    }

    /**
     * Returns the instant of which the last weekly break ended.
     * If there have been no valid weekly breaks the method will return epoch.
     *
     * @return the instant of the last weekly break end.
     */
    public Instant getLatestWeeklyRestEndTime() {
        //TODO: Regulation 9.6

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
                            return new Instant(sessions.get(i + 1).getEndTime());
                        }
                        //if its not but second one is no further actions is needed.
                        else if (weeklyRest2.isLongerThan(STANDARD_WEEKLY_REST)) {
                            return new Instant(sessions.get(i + 1).getEndTime());
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
                return new Instant(sessions.get(i + 1).getEndTime());
            }
        }
        //No weekly rests found
        //return epoch TODO: Thing about this a bit more, don't forget javadoc.
        return new Instant(0);
    }
    /**
     * Returns the instant of which the last daily break ended.
     *
     * @return the instant of the last daily break end.
     */
    public Instant getLatestDailyRestEndTime() {
        //TODO: implement

        return null;
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
        return getActiveTimeSince(getLatestWeeklyRestEndTime());
    }


}
