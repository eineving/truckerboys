package truckerboys.otto.planner;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.directionsAPI.IDirections;
import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.driver.User;
import truckerboys.otto.placesAPI.IPlaces;
import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.GasStation;
import truckerboys.otto.utils.positions.MapLocation;
import truckerboys.otto.utils.positions.RestLocation;

public class
        TripPlanner {
    private final Duration MARGINAL = Duration.standardMinutes(10);
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
     * @return optimized route with POIs along the route (null if connection to Google is not established)
     */
    public Route getNewRoute(MapLocation startLocation, MapLocation endLocation, MapLocation... checkpoints) throws InvalidRequestException, NoConnectionException {
        Route optimalRoute = null;

        Route directRoute = directionsProvider.getRoute(startLocation, endLocation, checkpoints);

        //Returns the direct route if ETA is shorter than the time you have left to drive
        if (directRoute.getEta().isShorterThan(regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft())) {
            optimalRoute = directRoute;
        }

        //If there is no time left on this session
        else if(regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft().isEqual(Duration.ZERO)) {
            //TODO implement finding closest stop in the right direction
        }

        //If the location is within reach this day but not this session
        else if (!directRoute.getEta().isShorterThan(regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft()) &&
                directRoute.getEta().isShorterThan(regulationHandler.getThisDayTL(user.getHistory()).getTimeLeft())) {

            //If the ETA/2 is longer than time left on session
            if(directRoute.getEta().dividedBy(2).isLongerThan(regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft())){
                optimalRoute = getOptimizedRoute(startLocation, directRoute, regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft().dividedBy(2));
            }else {
                optimalRoute = getOptimizedRoute(startLocation, directRoute, regulationHandler.getThisDayTL(user.getHistory()).getTimeLeft().dividedBy(2));
            }


        }

        //If the location is not within reach this day (drive maximum distance)
        else if (!directRoute.getEta().isShorterThan(regulationHandler.getThisDayTL(user.getHistory()).getTimeLeft())) {
            optimalRoute = getOptimizedRoute(startLocation, directRoute, regulationHandler.getThisDayTL(user.getHistory()).getTimeLeft().minus(MARGINAL));
        }

        //Adds POIs along route to Route object
        for (GasStation temp : getGasSationsAlongRoute(optimalRoute)) {
            optimalRoute.addGasStationAlongRoute(temp);
        }
        for (RestLocation temp : getRestLocationsAlongRoute(optimalRoute)) {
            optimalRoute.addRestLocationAlongRoute(temp);
        }
        return optimalRoute;
    }


    /**
     * Get optimized route with one rest location as a checkpoint.
     *
     * @param startLocation current position.
     * @param directRoute   Route from Google Directions without any rest or gas stops.
     * @param within        Within what time a rest should be made.
     * @return An optimized route with the most suitable rest location as a checkpoint.
     */
    private Route getOptimizedRoute(MapLocation startLocation, Route directRoute, Duration within) {
        Route optimalRoute = null;
        LatLng optimalLatLong = findLatLngWithinDuration(directRoute, within);
        ArrayList<RestLocation> closeLocations = placesProvider.getNearbyRestLocations(optimalLatLong);
        Log.w("NBRofCloseLocations", closeLocations.size() + "");

        //Just calculating the ten best matches from Google
        for (int i = 0; i < 10 && i < closeLocations.size(); i++) {
            try {
                //Checks if the restLocation is a possible stop and is faster than the previous
                Route temp = directionsProvider.getRoute(startLocation, finalDestination, new MapLocation(closeLocations.get(i)));

                if (temp.getCheckpoints().get(0).getEta().isShorterThan(regulationHandler.getThisSessionTL(user.getHistory()).getTimeLeft())) {
                    if (optimalRoute == null) {
                        optimalRoute = temp;
                    } else if (temp.getEta().isShorterThan(optimalRoute.getEta())) {
                        optimalRoute = temp;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return optimalRoute;
    }

    /**
     * Find the coordinate on the polyline that have the smallest delta value from the time left.
     *
     * @param directRoute Route from Google Directions without any rest or gas stops.
     * @param timeLeft    ETA that the coordinate should be close to.
     * @return The coordinate that matches time left the best.
     */
    private LatLng findLatLngWithinDuration(Route directRoute, Duration timeLeft) {
        try {
            ArrayList<LatLng> coordinates = directRoute.getOverviewPolyline();

            int topIndex = coordinates.size() - 1;
            int bottomIndex = 0;

            Duration etaToCoordinate = directionsProvider.getETA(new MapLocation(directRoute.getOverviewPolyline().get(0)),
                    new MapLocation(coordinates.get((topIndex + bottomIndex) / 2)));


            while (etaToCoordinate.isShorterThan(timeLeft.minus(Duration.standardMinutes(2))) ||
                    etaToCoordinate.isLongerThan(timeLeft.plus(Duration.standardMinutes(2)))) {
                if (etaToCoordinate.isLongerThan(timeLeft)) {
                    topIndex = (topIndex + bottomIndex) / 2;
                } else {
                    bottomIndex = (topIndex + bottomIndex) / 2;
                }

                //Just to be safe
                if(topIndex == bottomIndex){
                    break;
                }

                etaToCoordinate = directionsProvider.getETA(new MapLocation(directRoute.getOverviewPolyline().get(0)),
                        new MapLocation(coordinates.get((topIndex + bottomIndex) / 2)));
            }
            return coordinates.get((topIndex + bottomIndex) / 2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calculate a new route based on a start location and end location provided
     *
     * @param startLocation The location that the route should start from.
     * @param endLocation   The location that the route should end at.
     */
    public Route calculateRoute(MapLocation startLocation, MapLocation endLocation) throws InvalidRequestException, NoConnectionException {
        return getNewRoute(startLocation, endLocation, null);
    }

    /**
     * Get all gas stations withing 3km from every point in the polyline overview
     *
     * @param route route to measure from
     * @return list of all gas stations within 3km from points in the route
     */
    private ArrayList<GasStation> getGasSationsAlongRoute(Route route) throws NoConnectionException {
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