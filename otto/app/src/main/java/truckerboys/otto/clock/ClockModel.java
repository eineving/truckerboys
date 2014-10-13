package truckerboys.otto.clock;

import org.joda.time.Duration;
import org.joda.time.Instant;

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
    private MapLocation recStop, firstAltStop, secAltStop;
    private long timeDifference;

    private TripPlanner tripPlanner;
    private Route route;

    public ClockModel(TripPlanner tripPlanner) {

        this.tripPlanner = tripPlanner;

        lastTimeUpdate = new Instant();
        timeLeftDuration = new Duration(Duration.ZERO);
        timeLeftExtendedDuration = new Duration(Duration.ZERO);
        timeLeft = new TimeLeft(timeLeftDuration, timeLeftExtendedDuration);

    }

    /**
     * Updates the ETAs of the stops and until violation
     */
    public void update() {
        timeNow = new Instant();
        timeDifference = timeNow.getMillis() - lastTimeUpdate.getMillis();
        timeLeftDuration = timeLeftDuration.minus(timeDifference);
        timeLeftExtendedDuration = timeLeftExtendedDuration.minus(timeDifference);
        if(recStop!=null)
        recStop.setEta(recStop.getEta().minus(timeDifference));
        if(firstAltStop!=null)
        firstAltStop.setEta(firstAltStop.getEta().minus(timeDifference));
        if(secAltStop!=null)
        secAltStop.setEta(secAltStop.getEta().minus(timeDifference));

        timeLeft = new TimeLeft(timeLeftDuration, timeLeftExtendedDuration);

        lastTimeUpdate = timeNow;
    }

    private void processChangedRoute() {

        timeLeft = route.getTimeLeftOnSession();
        timeLeftDuration = timeLeft.getTimeLeft();
        timeLeftExtendedDuration = timeLeft.getExtendedTimeLeft();

        recStop = route.getRecommendedStop();
        if(route.getAlternativeStops()!=null) {
            Iterator it = route.getAlternativeStops().iterator();

            if (it.hasNext()) {
                firstAltStop = (MapLocation) it.next();
            }
            if (it.hasNext()) {
                secAltStop = (MapLocation) it.next();
            }
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
     * Returns the first alternative stop
     * @return The first alternative stop
     */
    public MapLocation getFirstAltStop() {
        return firstAltStop;
    }

    /**
     * Returns the second alternative stop
     * @return The second alternative stop
     */
    public MapLocation getSecondAltStop() {
        return secAltStop;
    }

    public TimeLeft getTimeLeft() {
        return timeLeft;
    }

    public void setRoute(Route newRoute){
        route = newRoute;
        processChangedRoute();
    }
}
