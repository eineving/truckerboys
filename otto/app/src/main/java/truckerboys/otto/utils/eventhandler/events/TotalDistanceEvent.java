package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by root on 2014-10-13.
 */
public class TotalDistanceEvent extends Event {
    private long totalDistance;

    public TotalDistanceEvent(long totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getTotalDistance() {
        return totalDistance;
    }
}
