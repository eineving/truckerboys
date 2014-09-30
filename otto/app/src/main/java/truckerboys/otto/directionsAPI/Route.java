package truckerboys.otto.directionsAPI;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

import java.util.ArrayList;

import truckerboys.otto.planner.positions.Location;

/**
 * Created by Daniel on 2014-09-24.
 */
public class Route {
    private Location finalDestination;
    private Duration eta;
    private int distance;
    private ArrayList<LatLng> overviewPolyline;
    private ArrayList<LatLng> detailedPolyline;
    //private List<Location> checkpoints;

    /**
     *
     * @param finalDestination target destination
     * @param eta time till estimated arrival
     * @param overviewPolyline polyline overview
     * @param detailedPolyline polyline details (mush larger than overview)
     * @param distance distance to final destination in meters
     */
    public Route(Location finalDestination, Duration eta, int distance, ArrayList<LatLng> overviewPolyline, ArrayList<LatLng> detailedPolyline) {
        this.finalDestination = finalDestination;
        this.eta = eta;
        this.overviewPolyline = overviewPolyline;
        this.detailedPolyline = detailedPolyline;
        this.distance = distance;
    }

    /**
     * Get target destination
     * @return target destination
     */
    public Location getFinalDestination() {
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
}
