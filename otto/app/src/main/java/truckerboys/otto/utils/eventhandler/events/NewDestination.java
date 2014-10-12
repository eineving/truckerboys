package truckerboys.otto.utils.eventhandler.events;

import android.location.Address;

import java.util.ArrayList;

/**
 * Created by root on 2014-10-03.
 */
public class NewDestination extends Event {
    Address location;
    ArrayList<Address> checkpoints;

    public NewDestination(Address location, ArrayList<Address> checkpoints) {
        this.location = location;
        this.checkpoints = checkpoints;
    }

    public NewDestination(Address location) {
        this.location = location;
    }

    public Address getLocation() {
        return location;
    }

    public ArrayList<Address> getCheckpoints() {
        return checkpoints;
    }
}
