package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by root on 2014-10-13.
 */
public class DistanceByFuelEvent extends Event {
    private double distanceByFuel;

    public DistanceByFuelEvent(double distanceByFuel) {
        this.distanceByFuel = distanceByFuel;
    }

    public double getDistanceByFuel () {
        return distanceByFuel;
    }
}
