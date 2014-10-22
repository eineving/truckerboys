package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.eventhandler.EventType;

/**
 * Created by root on 2014-10-13.
 */
public class DistanceByFuelEvent extends Event {
    private double distanceByFuel;

    public DistanceByFuelEvent(double distanceByFuel) {
        super(EventType.STATISTICS);
        this.distanceByFuel = distanceByFuel;
    }

    public double getDistanceByFuel () {
        return distanceByFuel;
    }
}
