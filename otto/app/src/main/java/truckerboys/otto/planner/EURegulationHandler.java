package truckerboys.otto.planner;

import org.joda.time.Duration;
import org.joda.time.Instant;

import truckerboys.otto.driver.CurrentlyNotOnRestException;
import truckerboys.otto.driver.NoValidBreakFound;
import truckerboys.otto.driver.Session;

import truckerboys.otto.driver.SessionHistory;
import truckerboys.otto.driver.SessionType;

/**
 * Class which handles all the Regulations regarding driving times in the EU.
 * All answers will be based on the history represented by SessionHistory
 * and all answers will be based on the current time, non of the methods will return
 * the same value twice if called repeatedly.
 * Created by Daniel on 2014-09-17.
 */
public class EURegulationHandler implements IRegulationHandler {

    private final Duration MAX_SESSION_LENGTH = Duration.standardMinutes(270);

    private final Duration ZERO_DURATION = new Duration(0);

    private final Duration MAX_DAY_LENGTH = Duration.standardHours(9);
    private final Duration STANDARD_SESSION_REST = Duration.standardMinutes(45);

    private final Duration SPLIT_SESSION_REST_15 = Duration.standardMinutes(15);
    private final Duration SPLIT_SESSION_REST_30 = Duration.standardMinutes(30);

    private final Duration MAX_DAY_LENGTH_EXTENDED = Duration.standardHours(10);
    private final Duration DAY = Duration.standardDays(1);
    private final Duration STANDARD_DAILY_REST = Duration.standardHours(11);
    private final Duration REDUCED_DAILY_REST = Duration.standardHours(9);

    private final Duration MAX_WEEKLY_LENGTH = Duration.standardHours(56);
    private final Duration MAX_TWOWEEK_LENGTH = Duration.standardHours(90);

    @Override
    public TimeLeft getThisSessionTL(SessionHistory history) {

        Instant last45;
        Instant last30;
        Instant last15;

        //Find the active time since the last valid break.(Standard scenario)
        Duration activeTimeSinceLastSessionRest = history.getActiveTimeSinceBreakLongerThan(STANDARD_SESSION_REST);

        try {
            last45 = history.getEndTimeOfRestLongerThan(STANDARD_SESSION_REST);
        } catch (NoValidBreakFound e) {
            //There has been now standard session rest yet.
            last45 = new Instant(0);
        }

        //First look for a rest longer than 30min and shorter than 45min.
        //If it doesn't exist, the TLThisSession will be calculated from last45
        // even if there is a 15min break since last45
        try {

            last30 = history.getEndTimeOfRestInTheInterval(SPLIT_SESSION_REST_30, STANDARD_SESSION_REST);
            //check that last30 is after last45
            if (last30.isAfter(last45)) {
                //Check if there is a break longer than 15min and shorter than 45 between them.
                try {

                    last15 = history.getEndTimeOfRestInTheInterval(SPLIT_SESSION_REST_15, STANDARD_SESSION_REST);
                    //check that last15 is after last45
                    if (last15.isAfter(last45)) {
                        //Check that last15 is before last30
                        if (last15.isBefore(last30)) {
                            //Valid split rest found!
                            //Calculate from last30
                            activeTimeSinceLastSessionRest = history.getActiveTimeSince(last30);

                        }
                    }
                } catch (NoValidBreakFound e) {
                }

            } else {
                //Calculate TL since last45
                activeTimeSinceLastSessionRest = history.getActiveTimeSinceBreakLongerThan(STANDARD_SESSION_REST);

            }

        } catch (NoValidBreakFound e) {
            //No rest longer than 30min and shorter than 45
            //This means there is no valid split rest since last 45rest
            //Calculate from last45.
            activeTimeSinceLastSessionRest = history.getActiveTimeSinceBreakLongerThan(STANDARD_SESSION_REST);
        }

        Duration TL = MAX_SESSION_LENGTH.minus(activeTimeSinceLastSessionRest); // Calculate

        Duration TLToday = getThisDayTL(history).getTimeLeft().plus(getThisDayTL(history).getExtendedTimeLeft());

        //Cap the TL to the time left of the day.
        TL = (TL.isLongerThan(TLToday) ? TLToday : TL);


        return (TL.isLongerThan(ZERO_DURATION) ? new TimeLeft(TL, ZERO_DURATION) : new TimeLeft(ZERO_DURATION, ZERO_DURATION));
    }

    @Override
    public TimeLeft getThisDayTL(SessionHistory history) {

        //Get latest daily break, if no break was found. We are in first week ever. Search all sessions.
        Instant maxTimeMarker;
        try {
            //Check maximum TL today before you have to take a break, so that you will
            //have time to finish your break before the 24h time limit runs out.
            if (history.getNumberOfReducedDailyRestsThisWeek() < 3) {
                maxTimeMarker = history.getLatestDailyRestEndTime().plus(Duration.standardDays(1).minus(REDUCED_DAILY_REST));
            } else {
                maxTimeMarker = history.getLatestDailyRestEndTime().plus(Duration.standardDays(1).minus(STANDARD_DAILY_REST));
            }
        } catch (NoValidBreakFound e) {
            maxTimeMarker = new Instant().plus(Duration.standardHours(24));

        }


        Duration timeSinceDailyBreak = history.getActiveTimeSinceLastDailyBreak();
        Duration TL;
        Duration extendedTL = Duration.ZERO;
        Duration TLThisWeek = new Duration(getThisWeekTL(history).getTimeLeft().plus(getThisWeekTL(history).getExtendedTimeLeft()));

        TL = MAX_DAY_LENGTH.minus(timeSinceDailyBreak);
        TL = (TL.isShorterThan(Duration.ZERO) ? Duration.ZERO : TL);

        //Cap week
        TL = (TL.isLongerThan(TLThisWeek) ? TLThisWeek : TL);


        //The same thing as above but do it as the max day is 10 hours and calculate the difference.
        if (history.getNumberOfExtendedDaysThisWeek() < 2) {
            //If you are allowed to take an extended day.

            extendedTL = MAX_DAY_LENGTH_EXTENDED.minus(timeSinceDailyBreak);
            extendedTL = (extendedTL.isLongerThan(TLThisWeek) ? TLThisWeek : extendedTL);

            //Calculate the difference of the TL and the extendedTL which will be the next extended time.
            extendedTL = extendedTL.minus(TL);

        }

        return (new TimeLeft(TL, extendedTL));
    }


    @Override
    public TimeLeft getThisWeekTL(SessionHistory history) {
        //Calculate max time allowed this week based on last week.
        //MaxTimeTwoWeeks - Active time last week.
        Duration maxTimeAllowedThisWeek = MAX_TWOWEEK_LENGTH.minus(history.getActiveTimeSinceWeeklyBreakTwoWeeksAgo().
                minus(history.getActiveTimeSinceLastWeeklyBreak()));

        //Cap maxTimeAllowed based on the max time for one week according to regulation.
        maxTimeAllowedThisWeek = (maxTimeAllowedThisWeek.isLongerThan(MAX_WEEKLY_LENGTH) ? MAX_WEEKLY_LENGTH : maxTimeAllowedThisWeek);

        return new TimeLeft(new Duration(maxTimeAllowedThisWeek.minus((history.getActiveTimeSinceLastWeeklyBreak()))), Duration.ZERO);
    }

    @Override
    public TimeLeft getNextWeekTL(SessionHistory history) {
        Duration TL = new Duration(MAX_TWOWEEK_LENGTH.minus(history.getActiveTimeSinceLastWeeklyBreak()));

        //Cap time left on max time for one week
        TL = (TL.isLongerThan(MAX_WEEKLY_LENGTH) ? MAX_WEEKLY_LENGTH : TL);
        return new TimeLeft(TL, Duration.ZERO);
    }

    @Override
    public TimeLeft getThisTwoWeekTL(SessionHistory history) {
        Duration TL = MAX_TWOWEEK_LENGTH.minus(MAX_WEEKLY_LENGTH.minus(getThisWeekTL(history).getTimeLeft().plus(getThisWeekTL(history).getExtendedTimeLeft())));

        return new TimeLeft(TL, ZERO_DURATION);

    }

    @Override
    public TimeLeft getNextTwoWeekTL(SessionHistory history) {
        return new TimeLeft(Duration.standardHours(90), Duration.ZERO);
    }

    @Override
    public TimeLeft getTimeLeftOnBreak(SessionHistory history) throws CurrentlyNotOnRestException {

        //If currently on break
        if (history.isResting()) {
            SessionHistory tempHistory = new SessionHistory(history.getSessions());

            //10 minutes.
            int i = 10;
            Session s;
            while (true) {
                //Add a rest session, make it longer and longer in each loop.
                //When the rest is long enough getTLThisSession will return something other than zero.
                //Then we know the required length off the break.
                s = new Session(SessionType.RESTING, new Instant(), new Instant().plus(Duration.standardMinutes(i)));
                //To be a bit more efficient we add 10 minutes each time.

                tempHistory.addSession(s);
                if (!getThisSessionTL(tempHistory).getTimeLeft().equals(Duration.ZERO)) {
                    tempHistory.removeLastSession();
                    i -= 10;
                    //And then loop backwards reducing by one minute.
                    while (true) {
                        s = new Session(SessionType.RESTING, new Instant(), new Instant().plus(Duration.standardMinutes(i)));
                        tempHistory.addSession(s);
                        if (!getThisSessionTL(tempHistory).getTimeLeft().equals(Duration.ZERO)) {
                            return new TimeLeft(s.getDuration(), Duration.ZERO);
                        }
                        tempHistory.removeLastSession();
                        i++;
                    }
                }
                tempHistory.removeLastSession();
                i += 10;
            }

        } else {
            throw new CurrentlyNotOnRestException("Driver currently isn't on a break, can't call getTimeLeftOnBreak");
        }
    }

}
