package truckerboys.otto.directionsAPI;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

import java.util.ArrayList;

import truckerboys.otto.utils.positions.MapLocation;
import truckerboys.otto.utils.positions.RouteLocation;


public class Route {
    private MapLocation finalDestination;
    private Duration eta;
    private int distance;
    private ArrayList<LatLng> overviewPolyline;
    private ArrayList<LatLng> detailedPolyline;
    private ArrayList<RouteLocation> checkpoints;
    private RouteLocation recommendedStop;
    private ArrayList<RouteLocation> alternativeStops;
    private Duration etaToFirstCheckpoint = null;


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
    public Route(MapLocation finalDestination, Duration eta, int distance, ArrayList<LatLng> overviewPolyline,
                 ArrayList<LatLng> detailedPolyline, ArrayList<RouteLocation> checkpoints) {
        this.finalDestination = finalDestination;
        this.eta = eta;
        this.overviewPolyline = overviewPolyline;
        this.detailedPolyline = detailedPolyline;
        this.distance = distance;
        this.checkpoints = checkpoints;
        this.etaToFirstCheckpoint = eta;
    }

    /**
     * Creates a new route
     *
     * @param finalDestination     target destination
     * @param eta                  time till estimated arrival
     * @param overviewPolyline     polyline overview
     * @param detailedPolyline     polyline details (mush larger than overview)
     * @param distance             distance to final destination in meters
     * @param checkpoints          checkpoints
     * @param etaToFirstCheckpoint ETA to the first checkpoint
     */
    public Route(MapLocation finalDestination, Duration eta, int distance, ArrayList<LatLng> overviewPolyline,
                 ArrayList<LatLng> detailedPolyline, ArrayList<RouteLocation> checkpoints, Duration etaToFirstCheckpoint) {
        this(finalDestination, eta, distance, overviewPolyline, detailedPolyline, checkpoints);
        if (etaToFirstCheckpoint == null) {
            this.etaToFirstCheckpoint = eta;
        } else {
            this.etaToFirstCheckpoint = etaToFirstCheckpoint;
        }
    }

    /**
     * Get target destination
     *
     * @return target destination
     */
    public MapLocation getFinalDestination() {
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

    /**
     * Get the recommended stop for the route.
     *
     * @return recommended stop (null if no stop is recommended).
     */
    public MapLocation getRecommendedStop() {
        return recommendedStop;
    }

    /**
     * Set the recommended stop for the route.
     *
     * @param recommendedStop the recommended stop for the route.
     */
    public void setRecommendedStop(RouteLocation recommendedStop) {
        this.recommendedStop = recommendedStop;
    }

    /**
     * Get the alternative stops for this route.
     *
     * @return alternative stops.
     */
    public ArrayList<RouteLocation> getAlternativeStops() {
        return alternativeStops;
    }

    /**
     * Set the alternative stops for this route.
     *
     * @param alternativeStops the alternative stops for the route.
     */
    public void setAlternativeStops(ArrayList<RouteLocation> alternativeStops) {
        this.alternativeStops = alternativeStops;
    }

    /**
     * Get the time to the first checkpoint on the route
     *
     * @return ETA to first checkpoint. ETA to final destination if no checkpoint exists
     */
    public Duration getEtaToFirstCheckpoint() {
        return etaToFirstCheckpoint;
    }
}
