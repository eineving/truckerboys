package truckerboys.otto.planner;

import org.joda.time.Duration;

import java.util.List;

import truckerboys.otto.driver.Session;

/**
 * Created by Daniel on 2014-09-17.
 */
public class EURegulationHandler implements IRegulationHandler {

    @Override
    public TimeLeft getThisSessionTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public TimeLeft getNextSessionTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public TimeLeft getNextSessionTL(List<Session> history) {
        return null;
    }

    @Override
    public TimeLeft getThisDayTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public TimeLeft getThisDayTL(List<Session> history) {
        return null;
    }

    @Override
    public TimeLeft getNextDayTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public TimeLeft getNextDayTL(List<Session> history) {
        return null;
    }

    @Override
    public TimeLeft getThisWeekTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public TimeLeft getThisWeekTL(List<Session> history) {
        return null;
    }

    @Override
    public TimeLeft getNextWeekTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public TimeLeft getNextWeekTL(List<Session> history) {
        return null;
    }

    @Override
    public TimeLeft getThisTwoWeekTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public TimeLeft getThisTwoWeekTL(List<Session> history) {
        return null;
    }

    @Override
    public TimeLeft getNextTwoWeekTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public TimeLeft getNextTwoWeekTL(List<Session> history) {
        return null;
    }

    @Override
    public TimeLeft getTimeLeftOnBreak(List<Session> history) {
        return null;
    }

}
