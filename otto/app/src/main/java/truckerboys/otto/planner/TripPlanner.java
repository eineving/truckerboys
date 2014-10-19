package truckerboys.otto.planner;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import truckerboys.otto.utils.positions.RouteLocation;

public class
        TripPlanner {
    private final Duration MARGINAL = Duration.standardMinutes(10);
    private User user;
    private IRegulationHandler regulationHandler;
    private IDirections directionsProvider;
    private IPlaces placesProvider;


    //Route preferences
    private PlannedRoute activeRoute;
    private MapLocation startLocation;
    private MapLocation finalDestination;
    private List<MapLocation> checkpoints;

    private RouteLocation chosenStop;
    private RouteLocation recommendedStop;


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
    public PlannedRoute getRoute() throws NoActiveRouteException {
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
    public void setChoosenStop(RouteLocation chosenStop) throws InvalidRequestException, NoConnectionException {
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
        if (checkpoints != null) {
            ArrayList<MapLocation> temp = new ArrayList<MapLocation>();
            for (MapLocation checkpoint : checkpoints) {
                if (passedCheckpoint.equalCoordinates(checkpoint)) {
                    checkpointFound = true;
                } else {
                    temp.add(checkpoint);
                }
            }
            checkpoints = temp;
        }

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
    public void setNewRoute(MapLocation startLocation, MapLocation finalDestination, List<MapLocation> checkpoints) throws InvalidRequestException, NoConnectionException {
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
    private PlannedRoute getCalculatedRoute() throws NoConnectionException, InvalidRequestException {
        Route optimalRoute;
        Duration sessionTimeLeft = regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft();
        ArrayList<RouteLocation> alternativeLocations = new ArrayList<RouteLocation>();
        RouteLocation displayedRecommended;

        Route directRoute = directionsProvider.getRoute(currentLocation, finalDestination, checkpoints);

        //TODO Implement check if gas is enough for this session
        if (chosenStop != null) {
            //Setting the recommended as it will be in alternative stops
            getOptimizedRoute(directRoute, Duration.standardMinutes(5));

            ArrayList<MapLocation> tempCheckpoints = new ArrayList<MapLocation>();
            tempCheckpoints.add(chosenStop);
            if (checkpoints != null) {
                tempCheckpoints.addAll(checkpoints);
            }
            optimalRoute = directionsProvider.getRoute(currentLocation, finalDestination, tempCheckpoints);

            displayedRecommended = chosenStop;

            alternativeLocations.add(recommendedStop);
            alternativeLocations.addAll(calculateAlternativeStops(directRoute,
                    directRoute.getCheckpoints().get(0).getEta().dividedBy(2),
                    directRoute.getCheckpoints().get(0).getEta().dividedBy(3)));

        } else {

            //Returns the direct route if ETA is shorter than the time you have left to drive
            if (directRoute.getCheckpoints().get(0).getEta().isShorterThan(sessionTimeLeft)) {

                optimalRoute = directRoute;
                alternativeLocations = (calculateAlternativeStops(directRoute,
                        directRoute.getCheckpoints().get(0).getEta().dividedBy(2), directRoute.getCheckpoints().get(0).getEta().dividedBy(4)));
            }

            //If there is no time left on this session
            else if (sessionTimeLeft.isEqual(Duration.ZERO)) {
                optimalRoute = getOptimizedRoute(directRoute, Duration.standardMinutes(5));
                alternativeLocations = calculateAlternativeStops(directRoute, Duration.standardMinutes(10), Duration.standardMinutes(15));
            }

            //If the location is within reach this day but not this session
            else if (!directRoute.getCheckpoints().get(0).getEta().isShorterThan(sessionTimeLeft) &&
                    directRoute.getCheckpoints().get(0).getEta().isShorterThan(regulationHandler.getThisDayTL(user.getHistory()).getTimeLeft())) {

                //If the ETA/2 is longer than time left on session
                if (directRoute.getCheckpoints().get(0).getEta().dividedBy(2).isLongerThan(sessionTimeLeft)) {
                    optimalRoute = getOptimizedRoute(directRoute, regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft());
                    alternativeLocations = calculateAlternativeStops(directRoute, sessionTimeLeft.dividedBy(2), sessionTimeLeft.dividedBy(4));
                } else {
                    optimalRoute = getOptimizedRoute(directRoute, directRoute.getCheckpoints().get(0).getEta().dividedBy(2));
                    alternativeLocations = calculateAlternativeStops(directRoute, sessionTimeLeft, sessionTimeLeft.dividedBy(2));
                }
            }

            //If the location is not within reach this day (drive maximum distance)
            else if (!directRoute.getCheckpoints().get(0).getEta().isShorterThan(regulationHandler.getThisDayTL(user.getHistory()).getTimeLeft())) {
                optimalRoute = getOptimizedRoute(directRoute, regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft().minus(MARGINAL));
                alternativeLocations = calculateAlternativeStops(directRoute, sessionTimeLeft.dividedBy(2), sessionTimeLeft.dividedBy(4));
            } else {
                throw new InvalidRequestException("Something is not right here");
            }

            if (optimalRoute.getCheckpoints().size() > 0) {
                displayedRecommended = optimalRoute.getCheckpoints().get(0);
            } else {
                displayedRecommended = optimalRoute.getFinalDestination();
            }
            alternativeLocations.add(recommendedStop);

        }
        return new PlannedRoute(optimalRoute, displayedRecommended, alternativeLocations);
    }

    /**
     * Get a list of one stop location close to each wanted ETA.
     *
     * @param directRoute fastest route without rest locations added.
     * @param stopsETA    times that stops are wanted in.
     * @return list if stop locations
     */
    private ArrayList<RouteLocation> calculateAlternativeStops(Route directRoute, Duration... stopsETA)
            throws InvalidRequestException, NoConnectionException {
        ArrayList<RouteLocation> incompleteInfo = new ArrayList<RouteLocation>();
        ArrayList<RouteLocation> completeInfo = new ArrayList<RouteLocation>();


        for (Duration eta : stopsETA) {
            LatLng tempCoordinate = findLatLngWithinDuration(directRoute, eta, Duration.standardMinutes(10));
            ArrayList<RouteLocation> response = placesProvider.getNearbyRestLocations(tempCoordinate);

            for (RouteLocation location : response) {
                if (directionsProvider.getETA(currentLocation, location).
                        isShorterThan(regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft())) {
                    incompleteInfo.add(location);
                    break; //Can not afford to do more calls to check the optimum one
                }
            }
        }

        //Creates new RouteLocations with all variables set
        for (RouteLocation incompleteLocation : incompleteInfo) {
            Route tempRoute = directionsProvider.getRoute(currentLocation, incompleteLocation);
            completeInfo.add(new RouteLocation(new LatLng(incompleteLocation.getLatitude(), incompleteLocation.getLongitude()),
                    tempRoute.getFinalDestination().getAddress(), tempRoute.getEta(), tempRoute.getDistance()));
        }
        return completeInfo;
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
        ArrayList<RouteLocation> closeLocations;

        optimalLatLong = findLatLngWithinDuration(directRoute, within, Duration.standardMinutes(5));
        closeLocations = placesProvider.getNearbyRestLocations(optimalLatLong);

        if (closeLocations.size() == 0) {
            Route tempRoute = directionsProvider.getRoute(currentLocation, new MapLocation(optimalLatLong), null, null);
            RouteLocation forcedLocation = new RouteLocation(optimalLatLong, "", tempRoute.getEta(), tempRoute.getDistance());
            forcedLocation.setName("No name location");
            closeLocations.add(forcedLocation);
        }

        //Just calculating the five best matches from Google
        for (int i = 0; i < 5 && i < closeLocations.size(); i++) {
            //Temporary creation
            LinkedList<MapLocation> tempList = new LinkedList<MapLocation>();

            tempList.add(closeLocations.get(i));
            if (checkpoints != null) {
                tempList.addAll(checkpoints);
            }

            //Checks if the restLocation is a possible stop and is faster than the previous
            Route temp = directionsProvider.getRoute(startLocation, finalDestination, tempList);

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
                etaToCoordinate.isLongerThan(timeLeft)) {
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