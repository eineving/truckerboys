package truckerboys.otto.planner;

import java.util.List;

import truckerboys.otto.driver.User;
import truckerboys.otto.directionsAPI.IDirections;
import truckerboys.otto.planner.positions.Location;

public class TripPlanner {
    private User user;
    private IRegulationHandler regulationHandler;
    private IDirections mapProvider;

    private Location finalDestination;

    public TripPlanner(IRegulationHandler regulationHandler, IDirections mapProvider, User user) {
        this.regulationHandler = regulationHandler;
        this.mapProvider = mapProvider;
        this.user = user;
    }

    /**
     * Calculates a new route for when the driver is not driving
     *
     * @param finalDestination target location to drive to
     * @required the truck needs to be standing still
     */
    public void calculateNewRouteTo(Location finalDestination) {
        /*
        //TODO Eineving Move this check to another method
        if (regulationHandler.getNextSessionTL(user.getHistory()).isShorterThan(mapProvider.getETAFinalDestination())) {
            //TODO Eineving implement adding a break location to the route
        }*/
    }
}