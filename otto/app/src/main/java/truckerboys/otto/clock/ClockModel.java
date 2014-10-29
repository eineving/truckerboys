package truckerboys.otto.clock;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.driver.CurrentlyNotOnRestException;
import truckerboys.otto.driver.User;
import truckerboys.otto.planner.IRegulationHandler;
import truckerboys.otto.planner.PlannedRoute;
import truckerboys.otto.planner.TimeLeft;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.exceptions.NoActiveRouteException;
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.RouteLocation;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * The model class for the clock that handles the logic.
 */
public class ClockModel {

    private TimeLeft timeLeft;
    private RouteLocation recStop, nextDestination;

    private TripPlanner tripPlanner;
    private IRegulationHandler regulationHandler;
    private User user;
    private PlannedRoute route;
    private boolean isOnBreak, nextDestinationIsFinal;

    private List<RouteLocation> altStops;

    public ClockModel(TripPlanner tripPlanner, IRegulationHandler regulationHandler, User user) {

        this.tripPlanner = tripPlanner;
        this.regulationHandler = regulationHandler;
        this.user = user;

        timeLeft = regulationHandler.getThisSessionTL(user.getHistory());
        if(timeLeft.getTimeLeft().getMillis()<=0){
            try {
                timeLeft = regulationHandler.getTimeLeftOnBreak(user.getHistory());
                isOnBreak = false;
            }catch (CurrentlyNotOnRestException e){
                timeLeft = regulationHandler.getThisSessionTL(user.getHistory());
                isOnBreak = true;
            }
        }

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

            if(route.getCheckpoints() == null || route.getCheckpoints().size() <= 2){
                nextDestination = route.getFinalDestination();
                nextDestinationIsFinal = true;
            }else{
                nextDestination = route.getCheckpoints().get(0);
                nextDestinationIsFinal = false;
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
    public List<RouteLocation> getAltStops(){
        return altStops;
    }

    /**
     * Returns the next destination. Could be a checkpoint or the final destination.
     * @return The next destination.
     */
    public RouteLocation getNextDestination(){
        return nextDestination;
    }

    /**
     * Sets the chosen stop.
     * @param stop The stop to be chosen.
     */
    public void setChosenStop(final RouteLocation stop){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tripPlanner.setChoosenStop(stop);
                }catch (InvalidRequestException e){
                    //Will never be thrown
                }catch (NoConnectionException e){
                    //Will never be thrown because if the app has no connection
                    // the view will have changed and the button to
                    // choose a stop won't be displayed
                }
            }
        }).start();
    }

    /**
     * Updates the time left. If there is no driving time left and the driver is on break, it displays the time left on break.
     */
    public void updateTL(){
        timeLeft = regulationHandler.getThisSessionTL(user.getHistory());
        if(timeLeft.getTimeLeft().getMillis()<=0){
            try {
                timeLeft = regulationHandler.getTimeLeftOnBreak(user.getHistory());
                isOnBreak = false;
            }catch (CurrentlyNotOnRestException e){
                timeLeft = regulationHandler.getThisSessionTL(user.getHistory());
                isOnBreak = true;
            }
        }
    }

    /**
     * Returns if the driver is on break.
     * @return Boolean, true if driver is on break.
     */
    public boolean isOnBreak(){
        return isOnBreak;
    }

    /**
     * Returns if the next destination is the final destination.
     * @return Boolean, true if the next destinaton is final.
     */
    public boolean isNextDestinationFinal(){
        return nextDestinationIsFinal;
    }

    /**
     * Returns the time left until violation
     * @return The time left until violation
     */
    public TimeLeft getTimeLeft() {
        return timeLeft;
    }

    /**
     * Returns the current route.
     * @return The current route
     */
    public Route getRoute(){
        return route;
    }
}
