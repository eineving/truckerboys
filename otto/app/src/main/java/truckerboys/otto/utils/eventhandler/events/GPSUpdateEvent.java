package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.eventhandler.EventType;
import truckerboys.otto.utils.positions.MapLocation;

/**
 * Created by Simon Petersson on 2014-10-02.
 *
 * Represents an event that indicates that the device position has changed.
 */
public class GPSUpdateEvent extends Event {
    private MapLocation newPosition;

    public GPSUpdateEvent(MapLocation newPosition){
        super(EventType.GPS_UPDATE);
        this.newPosition = newPosition;
    }

    public MapLocation getNewPosition() {
        return newPosition;
    }
}
