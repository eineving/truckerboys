package truckerboys.otto.planner;

import truckerboys.otto.directionsAPI.IDirections;
import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.driver.User;
import truckerboys.otto.utils.positions.Location;

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
     * Calculate a new route based on a start location and end location provided
     *
     * @param startLocation The location that the route should start from.
     * @param endLocation The location that the route should end at.
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