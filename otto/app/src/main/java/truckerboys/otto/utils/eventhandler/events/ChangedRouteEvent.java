package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.eventhandler.EventType;

/**
 * Created by Simon Petersson on 2014-10-02.
 *
 * Should be fired when the route has changed somehow. This indicates that the listener needs
 * to get the current route from the tripplanner. Since it was updated somehow.
 */
public class ChangedRouteEvent extends Event {
    public ChangedRouteEvent(){
        super(EventType.ROUTE);
    }
}
