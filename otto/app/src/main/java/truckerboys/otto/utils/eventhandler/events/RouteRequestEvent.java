package truckerboys.otto.utils.eventhandler.events;

import android.location.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikael Malmqvist on 2014-10-03.
 */
public class RouteRequestEvent extends Event {
    Address finalDestion;
    ArrayList<Address> checkpoints = new ArrayList<Address>();

    public RouteRequestEvent(Address finalDestination, List<Address> checkpoints) {
        this.finalDestion = finalDestination;
        this.checkpoints.addAll(checkpoints);
    }

    public Address getFinalDestion() {
        return finalDestion;
    }

    public ArrayList<Address> getCheckpoints() {
        return checkpoints;
    }
}
