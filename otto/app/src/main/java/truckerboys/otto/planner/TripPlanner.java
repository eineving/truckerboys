package truckerboys.otto.planner;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.directionsAPI.IDirections;
import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.driver.User;
import truckerboys.otto.placesAPI.IPlaces;
import truckerboys.otto.utils.positions.GasStation;
import truckerboys.otto.utils.positions.MapLocation;
import truckerboys.otto.utils.positions.RestLocation;

public class TripPlanner {
    private User user;
    private IRegulationHandler regulationHandler;
    private IDirections directionsProvider;
    private IPlaces placesProvider;
    private MapLocation finalDestination;


    public TripPlanner(IRegulationHandler regulationHandler, IDirections directionsProvider, IPlaces placesProvider, User user) {
        this.regulationHandler = regulationHandler;
        this.directionsProvider = directionsProvider;
        this.placesProvider = placesProvider;
        this.user = user;
    }

    /**
     * Calculate a new route based on a start location and end location provided
     *
     * @param startLocation The location that the route should start from.
     * @param endLocation   The location that the route should end at.
     * @param checkpoints   Checkpoints to visit before the end location
     */
    public Route getNewRoute(MapLocation startLocation, MapLocation endLocation, MapLocation... checkpoints) {
        //TODO This is where shit will go down, I guess!!

        Route directRoute;
        try {
            directRoute = directionsProvider.getRoute(startLocation, endLocation, checkpoints);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //TODO rough draft
        //Returns the direct route if ETA is shorter than the time you have left to drive
        if (directRoute.getEta().isShorterThan(regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft())) {
            for(GasStation temp : getGasSationsAlongRoute(directRoute)){
                directRoute.addGasStationAlongRoute(temp);
            }
            for(RestLocation temp : getRestLocationsAlongRoute(directRoute)){
                directRoute.addRestLocationAlongRoute(temp);
            }
            return directRoute;
        }

        return optimizeAndAddCheckpoints(directRoute);

    }

    private Route optimizeAndAddCheckpoints(Route directRoute) {
        return null;
    }

    /**
     * Calculate a new route based on a start location and end location provided
     *
     * @param startLocation The location that the route should start from.
     * @param endLocation   The location that the route should end at.
     */
    public Route calculateRoute(MapLocation startLocation, MapLocation endLocation) {
        return getNewRoute(startLocation, endLocation, null);
    }

    //TODO Eineving put this methods in a different class

    /**
     * Get a suggested address (location) from a user input String
     *
     * @param input           user input
     * @param currentLocation location to focus the searches from
     * @return suggested addresses
     */
    public List<String> getAddressSuggestion(String input, MapLocation currentLocation) {
        return placesProvider.getSuggestedAddresses(input, currentLocation);
    }

    //TODO Eineving put this methods in a different class

    /**
     * Get a suggested address (location) from a user input String
     *
     * @param input user input
     * @return suggested addresses
     */
    public List<String> getAddressSuggestion(String input) {
        return placesProvider.getSuggestedAddresses(input);
    }

    /**
     * Calculates the optimal times to take a break depending on the ETA to the destination
     * and the driving regulations.
     *
     * @param ETA The ETA to the final destination.
     * @return
     */
    private List<Duration> getOptimalBreaks(Duration ETA) {
        ArrayList<Duration> breaks = new ArrayList<Duration>();

        Duration legTL = regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft();
        Duration totalTL = ETA;

        while (ETA.isLongerThan(legTL)) {
            breaks.add(legTL.minus(Duration.standardMinutes(10)));
            totalTL = totalTL.minus(legTL);
            // How do we predict the allowed time for future sessions
            //The code below will not work as intended.
            legTL = Duration.standardHours(4).plus(Duration.standardMinutes(30));
        }

        //Should not only return times of the breaks but also the duration of the breaks.
        return breaks;
    }

    /**
     * Get all gas stations withing 3km from every point in the polyline overview
     *
     * @param route route to measure from
     * @return list of all gas stations within 3km from points in the route
     */
    private ArrayList<GasStation> getGasSationsAlongRoute(Route route) {
        ArrayList<GasStation> list = new ArrayList<GasStation>();
        for (LatLng position : route.getOverviewPolyline()) {

            //Getting each gas station within 3km from each point in the polyline
            for (GasStation gasStation : placesProvider.getNearbyGasStations(position)) {
                if (!locationExistsInList(gasStation, list)) {
                    list.add(gasStation);
                }
            }
        }
        return list;
    }

    /**
     * Get all rest locations withing 3km from every point in the polyline overview
     *
     * @param route route to measure from
     * @return list of all rest locations within 3km from points in the route
     */
    private ArrayList<RestLocation> getRestLocationsAlongRoute(Route route) {
        ArrayList<RestLocation> list = new ArrayList<RestLocation>();
        for (LatLng position : route.getOverviewPolyline()) {

            //Getting each gas station within 3km from each point in the polyline
            for (RestLocation restLocation : placesProvider.getNearbyRestLocations(position)) {
                if (!locationExistsInList(restLocation, list)) {
                    list.add(restLocation);
                }
            }
        }
        return list;
    }

    /**
     * Checks if a MapLocation already exists in an ArrayList
     *
     * @param location Location that you want to see if the list contains
     * @param list     List that might contain given locataion
     * @return True if list contains a location with the same coordinates as the location
     */
    private boolean locationExistsInList(MapLocation location, ArrayList<? extends MapLocation> list) {
        for (MapLocation temp : list) {
            if (temp.getLatitude() == location.getLatitude() && temp.getLongitude() == location.getLongitude()) {
                return true;
            }
        }
        return false;
    }
}