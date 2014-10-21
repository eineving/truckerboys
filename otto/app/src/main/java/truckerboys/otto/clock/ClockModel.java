package truckerboys.otto.clock;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.driver.User;
import truckerboys.otto.planner.IRegulationHandler;
import truckerboys.otto.planner.PlannedRoute;
import truckerboys.otto.planner.TimeLeft;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.exceptions.NoActiveRouteException;
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.MapLocation;
import truckerboys.otto.utils.positions.RouteLocation;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * The model class for the clock that handles the logic.
 */
public class ClockModel {

    private Duration timeLeftDuration, timeLeftExtendedDuration;
    private TimeLeft timeLeft;
    private RouteLocation recStop, nextDestination;

    private TripPlanner tripPlanner;
    private IRegulationHandler regulationHandler;
    private User user;
    private PlannedRoute route;

    private ArrayList<RouteLocation> altStops;

    public ClockModel(TripPlanner tripPlanner, IRegulationHandler regulationHandler, User user) {

        this.tripPlanner = tripPlanner;
        this.regulationHandler = regulationHandler;
        this.user = user;

        timeLeft = new TimeLeft(Duration.ZERO, Duration.ZERO);

        altStops = new ArrayList<RouteLocation>();
    }

    /**
     * Sets the time left until violation and the stops.
     * Called when the route is changed or updated.
     */
    public void processChangedRoute() {

        try {
            route = tripPlanner.getRoute();
            recStop = route.getRecommendedStop();
            altStops = route.getAlternativeStops();

            if(route.getCheckpoints() == null || route.getCheckpoints().size() == 0){
                nextDestination = route.getFinalDestination();
            }else{
                nextDestination = route.getCheckpoints().get(0);
            }
        }catch (NoActiveRouteException e){
            route = null;
        }
        timeLeft = regulationHandler.getThisSessionTL(user.getHistory());
    }

    /**
     * Returns the recommended stop
     * @return The recommended stop
     */
    public RouteLocation getRecommendedStop(){
        return recStop;
    }

    /**
     * Returns the alternative stops
     * @return A list of the alternative stops
     */
    public ArrayList<RouteLocation> getAltStops(){
        return altStops;
    }

    public RouteLocation getNextDestination(){
        return nextDestination;
    }

    public boolean setChosenStop(RouteLocation stop){
        try {
            tripPlanner.setChoosenStop(stop);
            return true;
        }catch (InvalidRequestException e){
            return false;
        }catch (NoConnectionException e){
            return false;
        }
    }

    /**
     * Returns the time left until violation
     * @return The time left until violation
     */
    public TimeLeft getTimeLeft() {
        return timeLeft;
    }

    public Route getRoute(){
        return route;
    }
}
