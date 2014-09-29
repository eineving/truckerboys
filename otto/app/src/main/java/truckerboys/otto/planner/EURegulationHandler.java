package truckerboys.otto.planner;

import org.joda.time.Duration;

import java.util.List;

import truckerboys.otto.driver.Session;
import truckerboys.otto.driver.SessionHistory;

/**
 * Created by Daniel on 2014-09-17.
 */
public class EURegulationHandler implements IRegulationHandler {

    private final Duration ZERO_DURATION = new Duration(0);

    private final Duration MAX_SESSION_LENGTH = Duration.standardMinutes(270);

    private final Duration MAX_EXTENDED_SESSION_LENGTH = Duration.standardMinutes(330);

    private final Duration MAX_DAY_LENGTH = Duration.standardMinutes(540);

    private final Duration STANDARD_DAILY_REST = Duration.standardMinutes(660);
    private final Duration STANDARD_SESSION_BREAK = Duration.standardMinutes(45);

    @Override
    public TimeLeft getThisSessionTL(SessionHistory history) {
        //TODO PEGELOW Fill out  the function to handle all edge-cases.
        //TODO PEGELOW If you need to ask the history something, you create a method in the SessionHistory class.
        //TODO in the style of the getActiveTimeSinceBreakLongerThan(Duration) method.

        //Find the active time since the last valid break, expand to handle split breaks aswell...
        Duration sinceLast45 = history.getActiveTimeSinceBreakLongerThan(STANDARD_SESSION_BREAK);

        Duration TL = MAX_SESSION_LENGTH.minus(sinceLast45); // Calculate

        Duration TLToday = getThisDayTL(history).getTimeLeft();

        //Cap the TL to the time left of the day.
        TL = (TL.isLongerThan(TLToday) ? TLToday : TL);

        //I don't know how negative durations is handled in jodatime so the TL is so the minimum is set to zero.
        return (TL.isLongerThan(ZERO_DURATION) ? new TimeLeft(TL, ZERO_DURATION) : new TimeLeft(ZERO_DURATION, ZERO_DURATION));
    }

    @Override
    public TimeLeft getNextSessionTL(SessionHistory history) {
        //TODO PEGELOW
        return null;
    }

    @Override
    public TimeLeft getThisDayTL(SessionHistory history) {

        Duration timeSinceDailyBreak = history.getActiveTimeSinceBreakLongerThan(STANDARD_DAILY_REST);

        Duration TL = MAX_DAY_LENGTH.minus(timeSinceDailyBreak);

        Duration TLThisWeek = getThisWeekTL(history).getTimeLeft();

        TL = (TL.isLongerThan(TLThisWeek) ? TLThisWeek : TL);

        return (TL.isLongerThan(ZERO_DURATION) ? new TimeLeft(TL, ZERO_DURATION) : new TimeLeft(ZERO_DURATION, ZERO_DURATION));
    }

    @Override
    public TimeLeft getNextDayTL(SessionHistory history) {
        return null;
    }

    @Override
    public TimeLeft getNextDayTL(List<Session> history) {
        return null;
    }

    @Override
    public TimeLeft getThisWeekTL(SessionHistory history) {
        return null;
    }

    @Override
    public TimeLeft getNextWeekTL(SessionHistory history) {
        return null;
    }

    @Override
    public TimeLeft getThisTwoWeekTL(SessionHistory history) {
        return null;
    }

    @Override
    public TimeLeft getNextTwoWeekTL(SessionHistory history) {
        return null;
    }

    @Override
    public TimeLeft getTimeLeftOnBreak(SessionHistory history) {
        return null;
    }

}
