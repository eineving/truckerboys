package truckerboys.otto.clock;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.Iterator;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TimeLeft;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.positions.MapLocation;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * The model class for the clock that handles the logic.
 */
public class ClockModel {

    private Instant lastTimeUpdate, timeNow;

    private Duration timeLeftDuration, timeLeftExtendedDuration;
    private TimeLeft timeLeft;
    private MapLocation recStop, nextDestination;
    private long timeDifference;

    private TripPlanner tripPlanner;
    private Route route;

    private ArrayList<MapLocation> altStops;

    public ClockModel(TripPlanner tripPlanner) {

        this.tripPlanner = tripPlanner;

        lastTimeUpdate = new Instant();
        timeLeftDuration = new Duration(Duration.ZERO);
        timeLeftExtendedDuration = new Duration(Duration.ZERO);
        timeLeft = new TimeLeft(timeLeftDuration, timeLeftExtendedDuration);

        altStops = new ArrayList<MapLocation>();
    }

    /**
     * Updates the ETAs of the stops and the time left until violation
     */
    public void update() {
        timeNow = new Instant();
        timeDifference = timeNow.getMillis() - lastTimeUpdate.getMillis();
        timeLeftDuration = timeLeftDuration.minus(timeDifference);
        timeLeftExtendedDuration = timeLeftExtendedDuration.minus(timeDifference);
        if(recStop!=null)
        recStop.setEta(recStop.getEta().minus(timeDifference));
        if(altStops!=null) {
            for (MapLocation stop : altStops) {
                if (stop != null) {
                    stop.setEta(stop.getEta().minus(timeDifference));
                }
            }
        }

        timeLeft = new TimeLeft(timeLeftDuration, timeLeftExtendedDuration);

        lastTimeUpdate = timeNow;
    }

    /**
     * Sets the time left until violation and the stops.
     * Called when the route is changed.
     */
    private void processChangedRoute() {

        timeLeft = route.getTimeLeftOnSession();
        timeLeftDuration = timeLeft.getTimeLeft();
        timeLeftExtendedDuration = timeLeft.getExtendedTimeLeft();

        recStop = route.getRecommendedStop();
        altStops = route.getAlternativeStops();

        if(route.getCheckpoints() == null || route.getCheckpoints().size() == 0){
            nextDestination = route.getFinalDestination();
        }else{
            nextDestination = route.getCheckpoints().get(0);
        }
    }

    /**
     * Returns the recommended stop
     * @return The recommended stop
     */
    public MapLocation getRecommendedStop(){
        return recStop;
    }

    /**
     * Returns the alternative stops
     * @return A list of the alternative stops
     */
    public ArrayList<MapLocation> getAltStops(){
        return altStops;
    }

    public MapLocation getNextDestination(){
        return nextDestination;
    }

    /**
     * Returns the time left until violation
     * @return The time left until violation
     */
    public TimeLeft getTimeLeft() {
        return timeLeft;
    }

    /**
     * Sets the route in clock
     * @param newRoute The route
     */
    public void setRoute(Route newRoute){
        route = newRoute;
        processChangedRoute();
    }

    public Route getRoute(){
        return route;
    }
}
