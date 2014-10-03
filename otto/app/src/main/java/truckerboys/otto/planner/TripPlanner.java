package truckerboys.otto.planner;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;

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
     */
    public Route calculateRoute(Location startLocation, Location endLocation) {
        try {
            Route route = directionsProvider.getRoute(startLocation.getLatLng(), endLocation);





            return route;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Calculates the optimal times to take a break depending on the ETA to the destination
     * and the driving regulations.
     * @param ETA The ETA to the final destination.
     * @return
     */
    private List<Duration> getOptimalBreaks(Duration ETA){
        ArrayList<Duration> breaks = new ArrayList<Duration>();

        Duration legTL = regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft();
        Duration totalTL = ETA;

        while(ETA.isLongerThan(legTL)){
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