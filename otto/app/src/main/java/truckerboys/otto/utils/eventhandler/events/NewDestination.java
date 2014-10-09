package truckerboys.otto.utils.eventhandler.events;

import android.location.Address;

/**
 * Created by root on 2014-10-03.
 */
public class NewDestination extends Event {
    Address location;
    Address checkPoint;

    public NewDestination(Address location, Address checkPoint) {
        this.location = location;
        this.checkPoint = checkPoint;
    }

    public NewDestination(Address location) {
        this.location = location;
    }

    public Address getLocation() {
        return location;
    }

    public Address getCheckPoint() {
        return checkPoint;
    }
}
