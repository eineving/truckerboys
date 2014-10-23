package truckerboys.otto.utils.eventhandler;

/**
 * Created by Simon Petersson on 2014-10-22.
 *
 * Represents a specific type of event. This is very useful in the aspect of optimizing the
 * eventbuss, since it makes sure that a listener to the eventbuss only subscribes to a specific'
 * type of events. Making it faster to send all events.
 */
public enum EventType {
    CONNECTION, BUTTON_CLICKED, STATISTICS, SETTINGS, CLOCK, ROUTE, GPS_UPDATE
}
