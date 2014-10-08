package truckerboys.otto.planner;

import org.joda.time.Duration;
import org.joda.time.Instant;

import truckerboys.otto.driver.CurrentlyNotOnRestException;
import truckerboys.otto.driver.SessionHistory;

/**
 * Created by Daniel on 2014-09-17.
 */
public class EURegulationHandler implements IRegulationHandler {

    private final Duration ZERO_DURATION = new Duration(0);

    private final Duration MAX_SESSION_LENGTH = Duration.standardMinutes(270);

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

        //First check maximum TL today before you have to take a break, so that you will
        //have time to finish your break before the 24h time limit runs out.
        Instant maxTimeMarker = history.getLatestDailyRestEndTime().plus(Duration.standardDays(1)).minus(STANDARD_DAILY_REST);


        if (history.getNumberOfReducedDailyRestsThisWeek() < 2) {
            maxTimeMarker = history.getLatestDailyRestEndTime().plus(Duration.standardDays(1)).minus(REDUCED_DAILY_REST);
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
        if (history.getNumberOfExtendedDaysThisWeek() < 2) {
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

        //Calculate TimeLeft
        Duration TL = new Duration(maxTimeAllowedThisWeek.minus((history.getActiveTimeSinceLastWeeklyBreak())));

        //Avoid negative timeLeft
        TL = (TL.isShorterThan(ZERO_DURATION) ? ZERO_DURATION : TL);


        Duration TLThisTwoWeek = new Duration(getThisWeekTL(history).getTimeLeft().plus(getThisWeekTL(history).getExtendedTimeLeft()));
        //Cap week
        TL = (TL.isLongerThan(TLThisTwoWeek) ? TL : TLThisTwoWeek);

        return new TimeLeft(TL, ZERO_DURATION);
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
        if (!history.isDriving()) {
            if (getThisWeekTL(history).getTimeLeft().isEqual(Duration.ZERO)) /* There is no time left to drive this week. */ {
                //TODO Logic for weekly break time
            }

            if (getThisDayTL(history).getTimeLeft().isEqual(Duration.ZERO)) /* There is no time left to drive today. */ {
                //TODO Logic for daily break time.
            }

            //TODO Rasten kan ersättas med dygns eller veckovila
            //Check if there was a break of atleast 15 minutes in the last 4 hours 30 minutes.
            if (history.existRestLonger(Duration.standardMinutes(15), MAX_SESSION_LENGTH)) {
                //Return "30 minutes" - "current break time"
                return new TimeLeft(Duration.standardMinutes(30).minus(history.getTimeSinceRestStart()), Duration.ZERO);
            } else /* else check time left until current break is 45 minutes long. */ {
                //Return "45 minutes" - "current break time"
                return new TimeLeft(STANDARD_SESSION_REST.minus(history.getTimeSinceRestStart()), Duration.ZERO);
            }

        } else {
            throw new CurrentlyNotOnRestException("Driver currently isn't on a break, can't call getTimeLeftOnBreak");
            //TODO Alternatively return new TimeLeft(Duration.ZERO, Duration.ZERO);
        }
    }

}
