package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.positions.MapLocation;

/**
 * Created by Simon Petersson on 2014-10-02.
 *
 * Represents an event that indicates that the device position has changed.
 */
public class LocationChangedEvent extends Event {
    private MapLocation newPosition;
    private MapLocation oldPosition;

    public LocationChangedEvent(MapLocation newPosition, MapLocation oldPosition){
        this.newPosition = newPosition;
        this.oldPosition = oldPosition;
    }

    public MapLocation getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(MapLocation newPosition) {
        this.newPosition = newPosition;
    }

    public MapLocation getOldPosition() {
        return oldPosition;
    }

    public void setOldPosition(MapLocation oldPosition) {
        this.oldPosition = oldPosition;
    }
}
