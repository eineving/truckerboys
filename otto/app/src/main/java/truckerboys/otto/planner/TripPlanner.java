package truckerboys.otto.planner;

import java.util.List;

import truckerboys.otto.driver.User;
import truckerboys.otto.mapAPI.IMap;
import truckerboys.otto.planner.positions.Location;

public class TripPlanner {
    private User user;
    private IRegulationHandler regulationHandler;
    private IMap mapProvider;

    private Location finalDestination;

    public TripPlanner(IRegulationHandler regulationHandler, IMap mapProvider, User user) {
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
        addFinalDestination(finalDestination);
        mapProvider.calculateRoute();

        //TODO Eineving create a check if the truck has enough fuel to reach target

        List<Location> restLocations = mapProvider.getRestLocationsAlongPlannedRoute();

        //TODO Eineving Move this check to another method

        //if (regulationHandler.getNextSessionTL(user.getHistory()).isShorterThan(mapProvider.getETAFinalDestination())) {
        //TODO Eineving implement adding a break location to the route
        //}
    }

    private void addFinalDestination(Location finalDestination) {
        this.finalDestination = finalDestination;
        mapProvider.setFinalDestination(finalDestination);
    }


}
