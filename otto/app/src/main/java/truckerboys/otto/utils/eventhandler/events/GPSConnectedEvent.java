package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by Simon Petersson on 2014-10-21.
 *
 * Event that is fired when the GPS changes conectivity status.
 */
public class GPSConnectedEvent extends Event {

    public GPSConnectedEvent() {
        super(EventType.CONNECTION);
    }
}
