package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.eventhandler.EventType;

/**
 * Created by root on 2014-10-13.
 */
public class TotalDistanceEvent extends Event {
    private long totalDistance;

    public TotalDistanceEvent(long totalDistance) {
        super(EventType.STATISTICS);
        this.totalDistance = totalDistance;
    }

    public long getTotalDistance() {
        return totalDistance;
    }
}
