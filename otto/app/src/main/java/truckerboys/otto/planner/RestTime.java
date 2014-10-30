package truckerboys.otto.planner;

/**
 * Created by Simon Petersson on 2014-10-08.
 *
 * A truck driver can take three different types of breaks; weekly, daily and session breaks.
 * This class is used to store the three different times, more specifically the remaining times
 * for finishing a break of type weekly, daily or session.
 */
public class RestTime {

    private TimeLeft weeklyRestTimeLeft;
    private TimeLeft dailyRestTimeLeft;
    private TimeLeft sessionRestTimeLeft;

    public RestTime(){ }

    public RestTime(TimeLeft weeklyRestTimeLeft, TimeLeft dailyRestTimeLeft, TimeLeft sessionRestTimeLeft) {
        this.weeklyRestTimeLeft = weeklyRestTimeLeft;
        this.dailyRestTimeLeft = dailyRestTimeLeft;
        this.sessionRestTimeLeft = sessionRestTimeLeft;
    }

    public TimeLeft getWeeklyRestTimeLeft() {
        return weeklyRestTimeLeft;
    }

    public void setWeeklyRestTimeLeft(TimeLeft weeklyRestTimeLeft) {
        this.weeklyRestTimeLeft = weeklyRestTimeLeft;
    }

    public TimeLeft getDailyRestTimeLeft() {
        return dailyRestTimeLeft;
    }

    public void setDailyRestTimeLeft(TimeLeft dailyRestTimeLeft) {
        this.dailyRestTimeLeft = dailyRestTimeLeft;
    }

    public TimeLeft getSessionRestTimeLeft() {
        return sessionRestTimeLeft;
    }

    public void setSessionRestTimeLeft(TimeLeft sessionRestTimeLeft) {
        this.sessionRestTimeLeft = sessionRestTimeLeft;
    }
}
