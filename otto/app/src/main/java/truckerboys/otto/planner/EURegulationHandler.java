package truckerboys.otto.planner;

import org.joda.time.Duration;
import org.joda.time.Instant;

import truckerboys.otto.driver.CurrentlyNotOnRestException;
import truckerboys.otto.driver.NoValidBreakFound;
import truckerboys.otto.driver.Session;

import truckerboys.otto.driver.SessionHistory;
import truckerboys.otto.driver.SessionType;

/**
 * Created by Daniel on 2014-09-17.
 */
public class EURegulationHandler implements IRegulationHandler {

    private final Duration MAX_SESSION_LENGTH = Duration.standardMinutes(270);

    private final Duration ZERO_DURATION = new Duration(0);

    private final Duration MAX_DAY_LENGTH = Duration.standardHours(9);
    private final Duration STANDARD_SESSION_REST = Duration.standardMinutes(45);

    private final Duration REDUCED_WEEKLY_REST = Duration.standardHours(24);
    private final Duration STANDARD_WEEKLY_REST = Duration.standardHours(45);

    private final Duration MAX_DAY_LENGTH_EXTENDED = Duration.standardHours(10);
    private final Duration DAY = Duration.standardDays(1);
    private final Duration STANDARD_DAILY_REST = Duration.standardHours(11);
    private final Duration REDUCED_DAILY_REST = Duration.standardHours(9);

    private final Duration MAX_WEEKLY_LENGTH = Duration.standardHours(56);
    private final Duration MAX_TWOWEEK_LENGTH = Duration.standardHours(90);

    @Override
    public TimeLeft getThisSessionTL(SessionHistory history) {

        //Find the active time since the last valid break, expand to handle split breaks aswell...
        Duration sinceLast45 = history.getActiveTimeSinceBreakLongerThan(STANDARD_SESSION_REST);

        Duration TL = MAX_SESSION_LENGTH.minus(sinceLast45); // Calculate

        Duration TLToday = getThisDayTL(history).getTimeLeft().plus(getThisDayTL(history).getExtendedTimeLeft());

        //Cap the TL to the time left of the day.
        TL = (TL.isLongerThan(TLToday) ? TLToday : TL);

        //I don't know how negative durations is handled in jodatime so the TL is so the minimum is set to zero.

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
                maxTimeMarker = history.getLatestDailyRestEndTime().plus(Duration.standardDays(1)).minus(REDUCED_DAILY_REST);
            } else {
                maxTimeMarker = history.getLatestDailyRestEndTime().plus(Duration.standardDays(1)).minus(STANDARD_DAILY_REST);
            }
        } catch (NoValidBreakFound e) {
            maxTimeMarker = Instant.now().plus(Duration.standardHours(24));

        }

        Duration timeSinceDailyBreak = history.getActiveTimeSinceLastDailyBreak();
        Duration TL;
        Duration extendedTL = new Duration(ZERO_DURATION);
        Duration TLThisWeek = new Duration(getThisWeekTL(history).getTimeLeft().plus(getThisWeekTL(history).getExtendedTimeLeft()));

        TL = MAX_DAY_LENGTH.minus(timeSinceDailyBreak);

        //Avoid negative timeLeft
        TL = (TL.isShorterThan(ZERO_DURATION) ? ZERO_DURATION : TL);

        //Cap week
        TL = (TL.isLongerThan(TLThisWeek) ? TLThisWeek : TL);

        //Check that the TL wont breach the 24h limit
        if (new Instant().plus(TL).isAfter(maxTimeMarker)) {
            TL = new Duration(new Instant(), maxTimeMarker);
        }

        //The same thing as above but do it as the max day is 10 hours and calculate the difference.
        if (history.getNumberOfExtendedDaysThisWeek() < 3) {
            //If you are allowed to take an extended day.

            extendedTL = MAX_DAY_LENGTH_EXTENDED.minus(timeSinceDailyBreak);
            extendedTL = (TL.isLongerThan(TLThisWeek) ? TLThisWeek : extendedTL);

            //Calculate the difference of the TL and the extendedTL which will be the next extended time.
            extendedTL = extendedTL.minus(TL);

            //Avoid negative extendedTL
            extendedTL = (extendedTL.isShorterThan(ZERO_DURATION) ? ZERO_DURATION : extendedTL);

            //Check that the TL + extendedTL wont breach the 24h limit
            if (new Instant().plus(TL).plus(extendedTL).isAfter(maxTimeMarker)) {
                Duration timeUntilMarker = new Duration(new Instant(), maxTimeMarker);
                if (timeUntilMarker.isShorterThan(TL)) {
                    TL = timeUntilMarker;
                    extendedTL = Duration.ZERO;
                } else {
                    extendedTL = timeUntilMarker.minus(TL);
                }
            }
        }

        return (TL.isLongerThan(ZERO_DURATION) ? new TimeLeft(TL, extendedTL) : new TimeLeft(ZERO_DURATION, ZERO_DURATION));
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
        Duration TL = MAX_TWOWEEK_LENGTH.minus(getThisWeekTL(history).getTimeLeft().plus(getThisWeekTL(history).getExtendedTimeLeft()));

        //Cap week mx time
        TL = (TL.isShorterThan(MAX_WEEKLY_LENGTH) ? TL : MAX_WEEKLY_LENGTH);

        //Avoid negative timeLeft
        TL = (TL.isShorterThan(ZERO_DURATION) ? ZERO_DURATION : TL);

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

                    //And then loop backwards reducing by one minute.
                    while (true) {
                        s = new Session(SessionType.RESTING, new Instant(), new Instant().plus(Duration.standardMinutes(i)));
                        tempHistory.addSession(s);
                        if (getThisSessionTL(tempHistory).getTimeLeft().equals(Duration.ZERO)) {
                            return new TimeLeft(s.getDuration().plus(Duration.standardMinutes(1)), Duration.ZERO);
                        }
                        tempHistory.removeLastSession();
                        i--;
                    }
                }
                tempHistory.removeLastSession();
                i += 10;
            }

        } else {
            throw new CurrentlyNotOnRestException("Driver currently isn't on a break, can't call getTimeLeftOnBreak");
            //TODO Alternatively return new TimeLeft(Duration.ZERO, Duration.ZERO);
        }
    }

}
