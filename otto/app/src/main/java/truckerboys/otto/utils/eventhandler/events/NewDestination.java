package truckerboys.otto.utils.eventhandler.events;

import android.location.Address;

/**
 * Created by root on 2014-10-03.
 */
public class NewDestination extends Event {
    Address location;

    public NewDestination(Address location) {
        this.location = location;
    }

    public Address getLocation() {
        return location;
    }
}
