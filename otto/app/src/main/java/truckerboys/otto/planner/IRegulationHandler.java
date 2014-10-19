package truckerboys.otto.planner;

import truckerboys.otto.driver.CurrentlyNotOnRestException;
import truckerboys.otto.driver.SessionHistory;

/**
 * Interface to make it possible to have multiple regulations setup but still the same trip planner
 */
public interface IRegulationHandler {
    //Single Sessions

    /**
     * Time left to drive this session
     *
     * @param history All sessions from at least 30 days back
     * @return Time left to drive this session
     */
    public TimeLeft getThisSessionTL(SessionHistory history);

    /**
     * Time left to drive this day
     *
     * @param history All sessions from at least 30 days back.
     * @return Time left to drive this day
     */
    public TimeLeft getThisDayTL(SessionHistory history);

    /**
     * Time left to drive this week
     *
     * @param history All sessions from at least 30 days back
     * @return Time left to drive this week
     */
    public TimeLeft getThisWeekTL(SessionHistory history);

    /**
     * Maximum time to drive the next week
     *
     * @param history All sessions from at least 30 days back
     * @return maximum time to drive the next week
     */
    public TimeLeft getNextWeekTL(SessionHistory history);

    //End week

    //Two week

    /**
     * Time left to drive this two week period
     *
     * @param history All sessions from at least 30 days back
     * @return Time left to drive this two week period
     */
    public TimeLeft getThisTwoWeekTL(SessionHistory history);


    /**
     * Maximum time to drive the next two week period after this week
     *
     * @param history All sessions from at least 30 days back
     * @return maximum time to drive the next two week period after this week
     */
    public TimeLeft getNextTwoWeekTL(SessionHistory history);
    //End two week

    /**
     * The time left on your break and you are allowed to drive again.
     * Returns the time with the error margin of max 1 minute.
     *
     * @param history All sessions from at least 30 days back
     * @return The time left before you are allowed to drive again.
     */
    public TimeLeft getTimeLeftOnBreak(SessionHistory history) throws CurrentlyNotOnRestException;
}
