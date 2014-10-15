package truckerboys.otto.planner;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import truckerboys.otto.directionsAPI.IDirections;
import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.driver.User;
import truckerboys.otto.placesAPI.IPlaces;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.events.ChangedRouteEvent;
import truckerboys.otto.utils.exceptions.CheckpointNotFoundException;
import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.exceptions.NoActiveRouteException;
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.MapLocation;

public class
        TripPlanner {
    private final Duration MARGINAL = Duration.standardMinutes(10);
    private User user;
    private IRegulationHandler regulationHandler;
    private IDirections directionsProvider;
    private IPlaces placesProvider;


    //Route preferences
    private Route activeRoute;
    private MapLocation startLocation;
    private MapLocation finalDestination;
    private MapLocation[] checkpoints;

    private MapLocation chosenStop;
    private MapLocation recommendedStop;


    private int nbrOfDirCalls = 0;
    private MapLocation currentLocation;

    public TripPlanner(IRegulationHandler regulationHandler, IDirections directionsProvider, IPlaces placesProvider, User user) {
        this.regulationHandler = regulationHandler;
        this.directionsProvider = directionsProvider;
        this.placesProvider = placesProvider;
        this.user = user;
    }

    /**
     * Get an updated version of the route
     *
     * @param currentLocation Location of the device.
     * @throws InvalidRequestException
     * @throws NoConnectionException
     */
    public void updateRoute(MapLocation currentLocation) throws InvalidRequestException, NoConnectionException {
        this.currentLocation = currentLocation;
        activeRoute = getCalculatedRoute();
        EventTruck.getInstance().newEvent(new ChangedRouteEvent());
    }

    /**
     * Get the active route
     *
     * @return the active route
     * @throws NoActiveRouteException
     */
    public Route getRoute() throws NoActiveRouteException {
        if (activeRoute == null) {
            throw new NoActiveRouteException("There is no active route");
        }
        return activeRoute;
    }

    /**
     * Returns a route to the same destination with chosen stop added to map
     *
     * @param chosenStop Where the driver wants to take  a break.
     * @throws InvalidRequestException
     * @throws NoConnectionException
     */
    public void setChoosenStop(MapLocation chosenStop) throws InvalidRequestException, NoConnectionException {
        this.chosenStop = chosenStop;
        activeRoute = getCalculatedRoute();
        EventTruck.getInstance().newEvent(new ChangedRouteEvent());
    }

    /**
     * Removes checkpoint from the route, and finishes the route if the checkpoint is the final destination.
     *
     * @param passedCheckpoint checkpoint that has been passed.
     * @throws CheckpointNotFoundException
     */
    public void passedCheckpoint(MapLocation passedCheckpoint) throws CheckpointNotFoundException {
        boolean checkpointFound = false;

        if (passedCheckpoint.equalCoordinates(chosenStop)) {
            chosenStop = null;
            checkpointFound = true;
        }

        if (passedCheckpoint.equalCoordinates(recommendedStop)) {
            recommendedStop = null;
            checkpointFound = true;
        }

        if (passedCheckpoint.equalCoordinates(finalDestination)) {
            activeRoute = null;
            checkpointFound = true;
        }
        ArrayList<MapLocation> temp = new ArrayList<MapLocation>();
        for (MapLocation checkpoint : checkpoints) {
            if (passedCheckpoint.equalCoordinates(checkpoint)) {
                checkpointFound = true;
            } else {
                temp.add(checkpoint);
            }
        }
        checkpoints = temp.toArray(checkpoints);

        if (!checkpointFound) {
            throw new CheckpointNotFoundException("does not exist");
        }
    }

    /**
     * Calculate a new route based on a start location and end location provided.
     *
     * @param startLocation    The location that the route should start from.
     * @param finalDestination The location that the route should end at.
     * @param checkpoints      Checkpoints to visit before the end location
     * @throws InvalidRequestException
     * @throws NoConnectionException
     */
    public void setNewRoute(MapLocation startLocation, MapLocation finalDestination, MapLocation... checkpoints) throws InvalidRequestException, NoConnectionException {
        this.startLocation = startLocation;
        this.finalDestination = finalDestination;
        this.checkpoints = checkpoints;
        this.chosenStop = null;
        this.currentLocation = startLocation;
        updateRoute(startLocation);
    }

    /**
     * Calculates a new route and uses the instance variables to decide what route,
     * and should also write to those variables.
     *
     * @return a route.
     * @throws NoConnectionException
     * @throws InvalidRequestException
     */
    private Route getCalculatedRoute() throws NoConnectionException, InvalidRequestException {
        Route optimalRoute;
        Duration sessionTimeLeft = regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft();

        Route directRoute = directionsProvider.getRoute(currentLocation, finalDestination, checkpoints);
        nbrOfDirCalls++;

        ArrayList<MapLocation> alternativeLocations = new ArrayList<MapLocation>();

        //TODO Implement check if gas is enough for this session

        System.out.println("timeLeft: " + regulationHandler.getThisDayTL(user.getHistory()).getTimeLeft().getMillis());
        System.out.println("timeLeft Extended: " + regulationHandler.getThisDayTL(user.getHistory()).getExtendedTimeLeft().getMillis());

        //Returns the direct route if ETA is shorter than the time you have left to drive
        if (directRoute.getEtaToFirstCheckpoint().isShorterThan(sessionTimeLeft)) {
            optimalRoute = directRoute;
            alternativeLocations = (calculateAlternativeStops(directRoute,
                    directRoute.getEtaToFirstCheckpoint().dividedBy(2), directRoute.getEtaToFirstCheckpoint().dividedBy(4)));
        }

        //If there is no time left on this session
        else if (sessionTimeLeft.isEqual(Duration.ZERO)) {
            //TODO implement finding closest stop in the right direction
            optimalRoute = directRoute;
        }

        //If the location is within reach this day but not this session
        else if (!directRoute.getEtaToFirstCheckpoint().isShorterThan(sessionTimeLeft) &&
                directRoute.getEtaToFirstCheckpoint().isShorterThan(regulationHandler.getThisDayTL(user.getHistory()).getTimeLeft())) {

            //If the ETA/2 is longer than time left on session
            if (directRoute.getEtaToFirstCheckpoint().dividedBy(2).isLongerThan(sessionTimeLeft)) {
                optimalRoute = getOptimizedRoute(directRoute, regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft());
                alternativeLocations = (calculateAlternativeStops(directRoute, sessionTimeLeft.dividedBy(2), sessionTimeLeft.dividedBy(4)));
            } else {
                optimalRoute = getOptimizedRoute(directRoute, directRoute.getEtaToFirstCheckpoint().dividedBy(2));
                alternativeLocations = (calculateAlternativeStops(directRoute, sessionTimeLeft, sessionTimeLeft.dividedBy(2)));
            }
        }

        //If the location is not within reach this day (drive maximum distance)
        else if (!directRoute.getEtaToFirstCheckpoint().isShorterThan(regulationHandler.getThisDayTL(user.getHistory()).getTimeLeft())) {
            optimalRoute = getOptimizedRoute(directRoute, regulationHandler.getThisDayTL(user.getHistory()).getTimeLeft().minus(MARGINAL));
            alternativeLocations = calculateAlternativeStops(directRoute, sessionTimeLeft.dividedBy(2), sessionTimeLeft.dividedBy(4));
        } else {
            throw new InvalidRequestException("Something is not right here");
        }

        if (chosenStop == null) {
            if (optimalRoute.getCheckpoints().size() > 0) {
                optimalRoute.setRecommendedStop(optimalRoute.getCheckpoints().get(0));
            } else {
                optimalRoute.setRecommendedStop(optimalRoute.getFinalDestination());
            }
            //Adds ETA to recommended stop
            optimalRoute.getRecommendedStop().setEta(directionsProvider.getETA(currentLocation, optimalRoute.getRecommendedStop()));
        } else {
            //Adds ETA to chosen stop
            chosenStop.setEta(directionsProvider.getETA(currentLocation, chosenStop));
            optimalRoute.setRecommendedStop(chosenStop);
            alternativeLocations.add(recommendedStop);
        }
        optimalRoute.setAlternativeStops(alternativeLocations);
        return optimalRoute;
    }

    /**
     * Get a list of one stop location close to each wanted ETA.
     *
     * @param directRoute fastest route without rest locations added.
     * @param stopsETA    times that stops are wanted in.
     * @return list if stop locations
     */
    private ArrayList<MapLocation> calculateAlternativeStops(Route directRoute, Duration... stopsETA) throws InvalidRequestException, NoConnectionException {
        ArrayList<MapLocation> alternativeStops = new ArrayList<MapLocation>();

        for (Duration eta : stopsETA) {
            LatLng tempCoordinate = findLatLngWithinDuration(directRoute, eta, Duration.standardMinutes(10));
            ArrayList<MapLocation> response = placesProvider.getNearbyRestLocations(tempCoordinate);
            if (response.size() > 0) {
                alternativeStops.add(response.get(0));
            }
        }
        return alternativeStops;
    }


    /**
     * Get optimized route with one rest location as a checkpoint.
     *
     * @param directRoute Route from Google Directions without any rest or gas stops.
     * @param within      Within what time a rest should be made.
     * @return An optimized route with the most suitable rest location as a checkpoint.
     */
    private Route getOptimizedRoute(Route directRoute, Duration within) throws InvalidRequestException, NoConnectionException {
        Route optimalRoute = null;
        LatLng optimalLatLong;
        ArrayList<MapLocation> closeLocations;

        optimalLatLong = findLatLngWithinDuration(directRoute, within, Duration.standardMinutes(5));
        closeLocations = placesProvider.getNearbyRestLocations(optimalLatLong);
        Log.w("NBRofCloseLocations", closeLocations.size() + "");

        if (closeLocations.size() == 0) {
            MapLocation forcedLocation = new MapLocation(optimalLatLong);
            forcedLocation.setAddress("Forced location");
            closeLocations.add(forcedLocation);
        }

        //Just calculating the five best matches from Google
        for (int i = 0; i < 5 && i < closeLocations.size(); i++) {
            //Temporary creation
            LinkedList<MapLocation> tempList = new LinkedList<MapLocation>();

            if (checkpoints != null) {
                Collections.addAll(tempList, checkpoints);
            }
            tempList.add(new MapLocation(closeLocations.get(i)));

            //Checks if the restLocation is a possible stop and is faster than the previous
            Route temp = directionsProvider.getRoute(startLocation, finalDestination, tempList.toArray(new MapLocation[tempList.size()]));

            if (temp.getCheckpoints().get(0).getEta().isShorterThan(regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft())) {
                if (optimalRoute == null) {
                    this.recommendedStop = closeLocations.get(i);
                    optimalRoute = temp;
                } else if (temp.getEta().isShorterThan(optimalRoute.getEta())) {
                    this.recommendedStop = closeLocations.get(i);
                    optimalRoute = temp;
                }
            }
        }
        return optimalRoute;
    }

    /**
     * Find the coordinate on the polyline that have the smallest delta value from the time left.
     *
     * @param directRoute Route from Google Directions without any rest or gas stops.
     * @param timeLeft    ETA that the coordinate should be close to.
     * @param timeDiff    time that the ETA to the LatLng could differ from timeLeft.
     * @return The coordinate that matches time left the best.
     */
    private LatLng findLatLngWithinDuration(Route directRoute, Duration timeLeft, Duration timeDiff) throws InvalidRequestException, NoConnectionException {
        ArrayList<LatLng> coordinates = directRoute.getOverviewPolyline();

        Log.w("PolylineSize", coordinates.size() + "");
        int topIndex = coordinates.size() - 1;
        int bottomIndex = 0;
        int currentIndex = (topIndex + bottomIndex) / 2;

        Duration etaToCoordinate = directionsProvider.getETA(new MapLocation(directRoute.getOverviewPolyline().get(0)),
                new MapLocation(coordinates.get(currentIndex)));
        nbrOfDirCalls++;


        while (etaToCoordinate.isShorterThan(timeLeft.minus(timeDiff)) ||
                etaToCoordinate.isLongerThan(timeLeft.plus(timeDiff))) {
            if (etaToCoordinate.isLongerThan(timeLeft)) {
                topIndex = currentIndex;
            } else {
                bottomIndex = currentIndex;
            }

            currentIndex = (topIndex + bottomIndex) / 2;
            //Just to be safe
            if (topIndex - bottomIndex < 2) {
                break;
            }
            Log.w("findLatLng", "topIndex: " + topIndex);
            Log.w("findLatLng", "bottomIndex: " + bottomIndex);
            etaToCoordinate = directionsProvider.getETA(new MapLocation(directRoute.getOverviewPolyline().get(0)),
                    new MapLocation(coordinates.get(currentIndex)));
            nbrOfDirCalls++;
            Log.w("nbrOfDirCalls", nbrOfDirCalls + "");

        }
        Log.w("nbrOfDirCalls", nbrOfDirCalls + "");
        return coordinates.get(currentIndex);
    }

    /**
     * Checks if a MapLocation already exists in an ArrayList
     *
     * @param location Location that you want to see if the list contains
     * @param list     List that might contain given location
     * @return True if list contains a location with the same coordinates as the location
     */
    private boolean locationExistsInList(MapLocation location, ArrayList<? extends
            MapLocation> list) {
        for (MapLocation temp : list) {
            if (temp.getLatitude() == location.getLatitude() && temp.getLongitude() == location.getLongitude()) {
                return true;
            }
        }
        return false;
    }
}