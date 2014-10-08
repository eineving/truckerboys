package truckerboys.otto.planner;

import org.joda.time.Duration;

import java.util.List;

import truckerboys.otto.driver.CurrentlyNotOnBreakException;
import truckerboys.otto.driver.Session;
import truckerboys.otto.driver.SessionHistory;

/**
 * Created by Daniel on 2014-09-17.
 */
public class EURegulationHandler implements IRegulationHandler {

    private final Duration ZERO_DURATION = new Duration(0);

    private final Duration MAX_SESSION_LENGTH = Duration.standardMinutes(270);

    private final Duration MAX_DAY_LENGTH = Duration.standardHours(9);
    private final Duration MAX_DAY_LENGTH_EXTENDED = Duration.standardHours(6);

    private final Duration STANDARD_SESSION_REST = Duration.standardMinutes(45);

    private final Duration STANDARD_DAILY_REST = Duration.standardHours(11);
    private final Duration REDUCED_DAILY_REST = Duration.standardHours(9);

    private final Duration REDUCED_WEEKLY_REST = Duration.standardHours(24);
    private final Duration STANDARD_WEEKLY_REST = Duration.standardHours(45);

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
    public TimeLeft getNextSessionTL(SessionHistory history) {
        return null;
    }

    @Override
    public TimeLeft getThisDayTL(SessionHistory history) {

        Duration timeSinceDailyBreak = history.getActiveTimeSinceLastDailyBreak();
        Duration TL;
        Duration extendedTL = new Duration(ZERO_DURATION);
        Duration TLThisWeek = new Duration(getThisWeekTL(history).getTimeLeft().plus(getThisWeekTL(history).getExtendedTimeLeft()));


        TL = MAX_DAY_LENGTH.minus(timeSinceDailyBreak);

        //Avoid negative timeLeft
        TL = (TL.isShorterThan(ZERO_DURATION) ? ZERO_DURATION : TL);

        //Cap week
        TL = (TL.isLongerThan(TLThisWeek) ? TLThisWeek : TL);

        //The same thing as above but do it as the max day is 10 hours and calculate the difference.
        if (history.getNumberOfExtendedDaysThisWeek() < 2) {
            //If you are allowed to take an extended day.

            extendedTL = MAX_DAY_LENGTH_EXTENDED.minus(timeSinceDailyBreak);
            extendedTL = (TL.isLongerThan(TLThisWeek) ? TLThisWeek : extendedTL);

            //Calculate the difference of the TL and the extendedTL which will be the net extended time.
            extendedTL = extendedTL.minus(TL);

            //Avoid negative extendedTL
            TL = (extendedTL.isShorterThan(ZERO_DURATION) ? ZERO_DURATION : extendedTL);
        }

        return (TL.isLongerThan(ZERO_DURATION) ? new TimeLeft(TL, extendedTL) : new TimeLeft(ZERO_DURATION, ZERO_DURATION));
    }

    @Override
    public TimeLeft getNextDayTL(SessionHistory history) {
        return null;
    }

    @Override
    public TimeLeft getThisWeekTL(SessionHistory history) {
        //Calculate TimeLeft
        Duration TL = new Duration(MAX_WEEKLY_LENGTH.minus((history.getActiveTimeSinceLastWeeklyBreak())));

        //Avoid negative timeLeft
        TL = (TL.isShorterThan(ZERO_DURATION) ? ZERO_DURATION : TL);
        Duration TLThisTwoWeek = new Duration(getThisWeekTL(history).getTimeLeft().plus(getThisWeekTL(history).getExtendedTimeLeft()));

        //Cap week
        TL = (TL.isLongerThan(TLThisTwoWeek) ? TL : TLThisTwoWeek);

        return new TimeLeft(TL, ZERO_DURATION);
    }

    @Override
    public TimeLeft getNextWeekTL(SessionHistory history) {
        return null;
    }

    @Override
    public TimeLeft getThisTwoWeekTL(SessionHistory history) {
        //Calculate TimeLeft
        Duration TL = new Duration(MAX_TWOWEEK_LENGTH.minus((history.getActiveTimeSinceWeeklyBreakTwoWeeksAgo())));

        //Avoid negative timeLeft
        TL = (TL.isShorterThan(ZERO_DURATION) ? ZERO_DURATION : TL);

        return new TimeLeft(TL, ZERO_DURATION);

    }

    @Override
    public TimeLeft getNextTwoWeekTL(SessionHistory history) {
        return null;
    }

    @Override
    public TimeLeft getTimeLeftOnBreak(SessionHistory history) throws CurrentlyNotOnBreakException {
        //If currently on break
        if(!history.isDriving()) {
            if (getThisWeekTL(history).getTimeLeft().isEqual(Duration.ZERO)) /* There is no time left to drive this week. */ {
                //TODO Logic for weekly break time
            }

            if (getThisDayTL(history).getTimeLeft().isEqual(Duration.ZERO)) /* There is no time left to drive today. */ {
                //TODO Logic for daily break time.
            }

            //TODO Rasten kan ersättas med dygns eller veckovila
            //Check if there was a break of atleast 15 minutes in the last 4 hours 30 minutes.
            if (history.existBreakLonger(Duration.standardMinutes(15), MAX_SESSION_LENGTH)) {
                //Return "30 minutes" - "current break time"
                return new TimeLeft(Duration.standardMinutes(30).minus(history.getTimeSinceBreakStart()), Duration.ZERO);
            } else /* else check time left until current break is 45 minutes long. */ {
                //Return "45 minutes" - "current break time"
                return new TimeLeft(STANDARD_SESSION_REST.minus(history.getTimeSinceBreakStart()), Duration.ZERO);
            }

        } else {
            throw new CurrentlyNotOnBreakException("Driver currently isn't on a break, can't call getTimeLeftOnBreak");
            //TODO Alternatively return new TimeLeft(Duration.ZERO, Duration.ZERO);
        }
    }

}
