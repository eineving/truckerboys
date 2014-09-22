package truckerboys.otto.planner;

import org.joda.time.*;
import java.util.List;

import truckerboys.otto.driver.Session;

/**
 * Interface to make it possible to have multiple regulations setup but still the same trip planner
 */
public interface IRegulationHandler {
    //Single Sessions

    /**
     * Time left to drive this session
     *
     * @param history       All sessions from at least 30 days back
     * @param activeSession Current active driving session
     * @return Time left to drive this session
     */
    public TimeLeft getThisSessionTL(List<Session> history, Session activeSession);

    /**
     * Maximum time to drive the next session
     *
     * @param history       All sessions from at least 30 days back
     * @param activeSession Current active driving session
     * @return maximum time to drive the next session
     */
    public TimeLeft getNextSessionTL(List<Session> history, Session activeSession);

    /**
     * Maximum time to drive the next session
     *
     * @param history All sessions from at least 30 days back
     * @return maximum time to drive the next session
     */
    public TimeLeft getNextSessionTL(List<Session> history);
    //End Single Sessions

    //Day

    /**
     * Time left to drive this day
     *
     * @param history       All sessions from at least 30 days back
     * @param activeSession Current active driving session
     * @return Time left to drive this day
     */
    public TimeLeft getThisDayTL(List<Session> history, Session activeSession);

    /**
     * Time left to drive this day
     *
     * @param history All sessions from at least 30 days back
     * @return Time left to drive this day
     */
    public TimeLeft getThisDayTL(List<Session> history);

    /**
     * Maximum time to drive the next day
     *
     * @param history       All sessions from at least 30 days back
     * @param activeSession Current active driving session
     * @return maximum time to drive the next day
     */
    public TimeLeft getNextDayTL(List<Session> history, Session activeSession);

    /**
     * Maximum time to drive the next day
     *
     * @param history All sessions from at least 30 days back
     * @return maximum time to drive the next day
     */
    public TimeLeft getNextDayTL(List<Session> history);
    //End Day

    //Week

    /**
     * Time left to drive this week
     *
     * @param history       All sessions from at least 30 days back
     * @param activeSession Current active driving session
     * @return Time left to drive this week
     */
    public TimeLeft getThisWeekTL(List<Session> history, Session activeSession);

    /**
     * Time left to drive this week
     *
     * @param history All sessions from at least 30 days back
     * @return Time left to drive this week
     */
    public TimeLeft getThisWeekTL(List<Session> history);

    /**
     * Maximum time to drive the next week
     *
     * @param history       All sessions from at least 30 days back
     * @param activeSession Current active driving session
     * @return maximum time to drive the next week
     */
    public TimeLeft getNextWeekTL(List<Session> history, Session activeSession);

    /**
     * Maximum time to drive the next week
     *
     * @param history All sessions from at least 30 days back
     * @return maximum time to drive the next week
     */
    public TimeLeft getNextWeekTL(List<Session> history);
    //End week

    //Two week

    /**
     * Time left to drive this two week period
     *
     * @param history       All sessions from at least 30 days back
     * @param activeSession Current active driving session
     * @return Time left to drive this two week period
     */
    public TimeLeft getThisTwoWeekTL(List<Session> history, Session activeSession);

    /**
     * Time left to drive this two week period
     *
     * @param history All sessions from at least 30 days back
     * @return Time left to drive this two week period
     */
    public TimeLeft getThisTwoWeekTL(List<Session> history);

    /**
     * Maximum time to drive the next two week period after this week
     *
     * @param history       All sessions from at least 30 days back
     * @param activeSession Current active driving session
     * @return maximum time to drive the next two week period after this week
     */
    public TimeLeft getNextTwoWeekTL(List<Session> history, Session activeSession);

    /**
     * Maximum time to drive the next two week period after this week
     *
     * @param history All sessions from at least 30 days back
     * @return maximum time to drive the next two week period after this week
     */
    public TimeLeft getNextTwoWeekTL(List<Session> history);
    //End two week

    /**
     * The time left on your break and you are allowed to drive again.
     *
     * @param history All sessions from at least 30 days back
     * @return The time left before you are allowed to drive again.
     */
    public TimeLeft getTimeLeftOnBreak(List<Session> history);
}
