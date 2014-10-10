package truckerboys.otto.directionsAPI;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

import java.util.ArrayList;

import truckerboys.otto.utils.positions.GasStation;
import truckerboys.otto.utils.positions.MapLocation;
import truckerboys.otto.utils.positions.RestLocation;

/**
 * Created by Daniel on 2014-09-24.
 */
public class Route {
    private MapLocation finalDestination;
    private Duration eta;
    private int distance;
    private ArrayList<LatLng> overviewPolyline;
    private ArrayList<LatLng> detailedPolyline;
    private ArrayList<MapLocation> checkpoints;
    private ArrayList<GasStation> gasStations = new ArrayList<GasStation>();
    private ArrayList<RestLocation> restLocations = new ArrayList<RestLocation>();



    /**
     *
     * @param finalDestination target destination
     * @param eta time till estimated arrival
     * @param overviewPolyline polyline overview
     * @param detailedPolyline polyline details (mush larger than overview)
     * @param distance distance to final destination in meters
     */
    public Route(MapLocation finalDestination, Duration eta, int distance, ArrayList<LatLng> overviewPolyline,
                 ArrayList<LatLng> detailedPolyline, ArrayList<MapLocation> checkpoints) {
        this.finalDestination = finalDestination;
        this.eta = eta;
        this.overviewPolyline = overviewPolyline;
        this.detailedPolyline = detailedPolyline;
        this.distance = distance;
        this.checkpoints = checkpoints;
    }

    /**
     * Get target destination
     * @return target destination
     */
    public MapLocation getFinalDestination() {
        return finalDestination;
    }

    /**
     * Get time left until arrival
     * @return time left until arrival
     */
    public Duration getEta() {
        return eta;
    }

    /**
     * Get a polyline with not so many coordinates as points to print to map
     * @return rough polyline
     */
    public ArrayList<LatLng> getOverviewPolyline() {
        return overviewPolyline;
    }

    /**
     * Get a polyline with a lot of  coordinates as points to print to map
     * @return detailed polyline
     */
    public ArrayList<LatLng> getDetailedPolyline() {
        return detailedPolyline;
    }

    /**
     * Get the distance to final destination in meters
     * @return distance to final destination in meters
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Get checkpoints for the route
     * @return route checkpoints
     */
    public ArrayList<MapLocation> getCheckpoints() {
        return checkpoints;
    }

    /**
     * Get gas stations along route
     * @return gas stations along route
     */
    public ArrayList<GasStation> getGasStationsAlongRoute() {
        return gasStations;
    }

    /**
     * Get rest locations along route
     * @return rest locations along route
     */
    public ArrayList<RestLocation> getRestLocationsAlongRoute() {
        return restLocations;
    }

    /**
     * Add gas station along route
     * @param gasStation a gas station close to the route
     */
    public void addGasStationAlongRoute(GasStation gasStation){
        gasStations.add(gasStation);
    }

    /**
     * Add rest location along the route
     * @param restLocation a rest location close to the route
     */
    public void addRestLocationAlongRoute(RestLocation restLocation){
        restLocations.add(restLocation);
    }
}
