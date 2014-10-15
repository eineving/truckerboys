package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by root on 2014-10-13.
 */
public class TotalDistanceEvent extends Event {
    private double totalDistance;

    public TotalDistanceEvent(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalDistance() {
        return totalDistance;
    }
}
