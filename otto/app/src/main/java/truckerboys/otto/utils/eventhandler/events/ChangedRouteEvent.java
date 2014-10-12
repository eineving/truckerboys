package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.directionsAPI.Route;

/**
 * Created by Simon Petersson on 2014-10-02.
 */
public class ChangedRouteEvent extends Event {
    private Route newRoute;

    public ChangedRouteEvent(Route newRoute){
        this.newRoute = newRoute;
    }

    public Route getRoute() {
        return newRoute;
    }
}
