package truckerboys.otto.planner;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.directionsAPI.IDirections;
import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.driver.User;
import truckerboys.otto.placesAPI.IPlaces;
import truckerboys.otto.utils.positions.MapLocation;

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
     */
    public Route calculateRoute(MapLocation startLocation, MapLocation endLocation) {
        try {
            return directionsProvider.getRoute(startLocation, endLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     *
     * Get a suggested address (location) from a user input String
     * @param input user input
     * @param currentLocation location to focus the searches from
     * @return suggested addresses
     */
    public Object getAddressSuggestion(String input, MapLocation currentLocation){
        return placesProvider.getSuggestedAddresses(input, currentLocation);
    }

    /**
     *
     * Get a suggested address (location) from a user input String
     * @param input user input
     * @return suggested addresses
     */
    public Object getAddressSuggestion(String input){
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


}