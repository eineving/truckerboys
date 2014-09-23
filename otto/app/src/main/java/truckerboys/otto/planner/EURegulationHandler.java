package truckerboys.otto.planner;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.List;

import truckerboys.otto.driver.Session;

/**
 * Created by Daniel on 2014-09-17.
 */
public class EURegulationHandler implements IRegulationHandler {

    @Override
    public TimeLeft getThisSessionTL(List<Session> history, Session activeSession) {

        //System.out.println(calculateHoursToday());
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

    /**
     * Returns the total driving time of this calender day.
     *
     * @param history All sessions from at least 30 days back
     * @return Total time driven today
     */
    private Duration calculateDrivingHoursOfToday(List<Session> history) {
        return calculateDrivingHoursOfToday(history, null);
    }

    /**
     * Returns the total driving time of this calender day.
     *
     * @param history       All sessions from at least 30 days back
     * @param activeSession Current active driving session
     * @return Total time driven today
     */
    private Duration calculateDrivingHoursOfToday(List<Session> history, Session activeSession) {
        Duration totalTime = new Duration(0);

        DateTime now = new DateTime();

        LocalDate today = now.toLocalDate();
        LocalDate tomorrow = today.plusDays(1);

        DateTime startOfToday = today.toDateTimeAtStartOfDay(now.getZone());
        DateTime endOfToday = tomorrow.toDateTimeAtStartOfDay(now.getZone());

        for (Session session : history) {
            if (session.getStartTime().isAfter(startOfToday) && session.getStartTime().isBefore(endOfToday)) {
                totalTime.plus(session.getDuration());
            }
        }
        if (activeSession == null) {
            return totalTime;
        }
        if (activeSession.getStartTime().isAfter(startOfToday) && activeSession.getStartTime().isBefore(endOfToday)) {
            totalTime.plus(activeSession.getDuration());
        }

        return totalTime;
    }
}
