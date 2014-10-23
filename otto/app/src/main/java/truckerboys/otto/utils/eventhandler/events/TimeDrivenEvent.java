package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.eventhandler.EventType;

/**
 * Created by root on 2014-10-08.
 */
public class TimeDrivenEvent extends Event {
    double timeToday;
    double timeTotal;

    public TimeDrivenEvent(double timeToday, double timeTotal) {
        super(EventType.STATISTICS);
        this.timeToday = timeToday;
        this.timeTotal = timeTotal;
    }

    public double getTimeToday() {
        return timeToday;
    }

    public double getTimeTotal() {
        return timeTotal;
    }
}
