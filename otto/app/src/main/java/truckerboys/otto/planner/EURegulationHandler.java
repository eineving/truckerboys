package truckerboys.otto.planner;

import org.joda.time.Duration;

import java.util.List;

import truckerboys.otto.driver.Session;

/**
 * Created by Daniel on 2014-09-17.
 */
public class EURegulationHandler implements IRegulationHandler {

    @Override
    public Duration getThisSessionTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public Duration getNextSessionTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public Duration getNextSessionTL(List<Session> history) {
        return null;
    }

    @Override
    public Duration getThisDayTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public Duration getThisDayTL(List<Session> history) {
        return null;
    }

    @Override
    public Duration getNextDayTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public Duration getNextDayTL(List<Session> history) {
        return null;
    }

    @Override
    public Duration getThisWeekTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public Duration getThisWeekTL(List<Session> history) {
        return null;
    }

    @Override
    public Duration getNextWeekTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public Duration getNextWeekTL(List<Session> history) {
        return null;
    }

    @Override
    public Duration getThisTwoWeekTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public Duration getThisTwoWeekTL(List<Session> history) {
        return null;
    }

    @Override
    public Duration getNextTwoWeekTL(List<Session> history, Session activeSession) {
        return null;
    }

    @Override
    public Duration getNextTwoWeekTL(List<Session> history) {
        return null;
    }
}
