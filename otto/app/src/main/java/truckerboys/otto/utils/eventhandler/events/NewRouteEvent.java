package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.directionsAPI.Route;

/**
 * Created by Simon Petersson on 2014-10-02.
 */
public class NewRouteEvent extends Event {
    private Route oldRoute;
    private Route newRoute;

    public NewRouteEvent(Route oldRoute, Route newRoute){
        this.oldRoute = oldRoute;
        this.newRoute = newRoute;
    }

    public Route getOldRoute() {
        return oldRoute;
    }

    public void setOldRoute(Route oldRoute) {
        this.oldRoute = oldRoute;
    }

    public Route getNewRoute() {
        return newRoute;
    }

    public void setNewRoute(Route newRoute) {
        this.newRoute = newRoute;
    }
}
