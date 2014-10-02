package truckerboys.otto.utils.eventhandler.events;

import android.location.Location;

/**
 * Created by Simon Petersson on 2014-10-02.
 *
 * Represents an event that indicates that the device position has changed.
 */
public class LocationChangedEvent extends Event {
    private Location newPosition;
    private Location oldPosition;

    public LocationChangedEvent(Location newPosition, Location oldPosition){
        this.newPosition = newPosition;
        this.oldPosition = oldPosition;
    }

    public Location getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Location newPosition) {
        this.newPosition = newPosition;
    }

    public Location getOldPosition() {
        return oldPosition;
    }

    public void setOldPosition(Location oldPosition) {
        this.oldPosition = oldPosition;
    }
}
