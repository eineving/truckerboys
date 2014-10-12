package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.directionsAPI.Route;

/**
 * Created by simon on 2014-10-06.
 */
public class UpdatedRouteEvent extends Event {
    private Route remainingRoute;

    public UpdatedRouteEvent(Route remainingRoute) {
        super();
        this.remainingRoute = remainingRoute;
    }

    public Route getRemainingRoute() {
        return remainingRoute;
    }
}
