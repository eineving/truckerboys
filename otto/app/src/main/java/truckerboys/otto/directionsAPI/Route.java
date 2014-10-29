package truckerboys.otto.directionsAPI;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

import java.util.ArrayList;

import truckerboys.otto.utils.positions.RouteLocation;


public class Route {
    private RouteLocation finalDestination;
    private Duration eta;
    private int distance;
    private ArrayList<LatLng> overviewPolyline;
    private ArrayList<LatLng> detailedPolyline;
    private ArrayList<RouteLocation> checkpoints;


    /**
     * Creates a new route
     *
     * @param finalDestination target destination
     * @param eta              time till estimated arrival
     * @param overviewPolyline polyline overview
     * @param detailedPolyline polyline details (mush larger than overview)
     * @param distance         distance to final destination in meters
     * @param checkpoints      checkpoints
     */
    public Route(RouteLocation finalDestination, Duration eta, int distance, ArrayList<LatLng> overviewPolyline,
                 ArrayList<LatLng> detailedPolyline, ArrayList<RouteLocation> checkpoints) {
        this.finalDestination = finalDestination;
        this.eta = eta;
        this.overviewPolyline = overviewPolyline;
        this.detailedPolyline = detailedPolyline;
        this.distance = distance;
        this.checkpoints = checkpoints;
    }

    /**
     * Copy constructor.
     *
     * @param other Route to copy.
     */
    public Route(Route other) {
        this.finalDestination = new RouteLocation(other.finalDestination);
        this.eta = new Duration(other.eta);
        this.distance = other.distance;

        this.overviewPolyline = new ArrayList<LatLng>();
        for (LatLng point : other.overviewPolyline) {
            this.overviewPolyline.add(new LatLng(point.latitude, point.longitude));
        }

        this.detailedPolyline = new ArrayList<LatLng>();
        for (LatLng point : other.detailedPolyline) {
            this.detailedPolyline.add(new LatLng(point.latitude, point.longitude));
        }

        this.checkpoints = new ArrayList<RouteLocation>();
        for (RouteLocation temp : other.checkpoints) {
            this.checkpoints.add(new RouteLocation(temp));
        }
    }


    /**
     * Get target destination
     *
     * @return target destination
     */
    public RouteLocation getFinalDestination() {
        return finalDestination;
    }

    /**
     * Get time left until arrival
     *
     * @return time left until arrival
     */
    public Duration getEta() {
        return eta;
    }

    /**
     * Get a polyline with not so many coordinates as points to print to map
     *
     * @return rough polyline
     */
    public ArrayList<LatLng> getOverviewPolyline() {
        return overviewPolyline;
    }

    /**
     * Get a polyline with a lot of  coordinates as points to print to map
     *
     * @return detailed polyline
     */
    public ArrayList<LatLng> getDetailedPolyline() {
        return detailedPolyline;
    }

    /**
     * Get the distance to final destination in meters
     *
     * @return distance to final destination in meters
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Get checkpoints for the route
     *
     * @return route checkpoints
     */
    public ArrayList<RouteLocation> getCheckpoints() {
        return checkpoints;
    }


}
