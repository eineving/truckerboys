package truckerboys.otto.planner;

import truckerboys.otto.directionsAPI.IDirections;
import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.driver.User;
import truckerboys.otto.planner.positions.Location;

public class TripPlanner {
    private User user;
    private IRegulationHandler regulationHandler;
    private IDirections directionsProvider;

    private Location finalDestination;

    public TripPlanner(IRegulationHandler regulationHandler, IDirections directionsProvider, User user) {
        this.regulationHandler = regulationHandler;
        this.directionsProvider = directionsProvider;
        this.user = user;
    }

    /**
     * Calculates a new route for when the driver is not driving
     *
     * @param endLocation target location to drive to
     * @required the truck needs to be standing still
     */
    public Route calculateRoute(Location startLocation, Location endLocation) {
        try {
            return directionsProvider.getRoute(startLocation.getLatLng(), endLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}