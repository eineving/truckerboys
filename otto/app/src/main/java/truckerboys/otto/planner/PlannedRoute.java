package truckerboys.otto.planner;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

import java.util.ArrayList;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.utils.positions.RouteLocation;

/**
 * A route that has
 */
public class PlannedRoute extends Route {
    private RouteLocation recommendedStop;
    private ArrayList<RouteLocation> alternativeStops;

    /**
     * Creates a new PlannedRoute
     *
     * @param finalDestination target destination
     * @param eta              time till estimated arrival
     * @param distance         distance to final destination in meters
     * @param overviewPolyline polyline overview
     * @param detailedPolyline polyline details (mush larger than overview)
     * @param checkpoints      checkpoints
     * @param recommendedStop  recommended stop
     * @param alternativeStops alternative stops
     */
    public PlannedRoute(RouteLocation finalDestination, Duration eta, int distance, ArrayList<LatLng> overviewPolyline, ArrayList<LatLng> detailedPolyline,
                        ArrayList<RouteLocation> checkpoints, RouteLocation recommendedStop, ArrayList<RouteLocation> alternativeStops) {
        super(finalDestination, eta, distance, overviewPolyline, detailedPolyline, checkpoints);
        this.recommendedStop = recommendedStop;
        this.alternativeStops = alternativeStops;
    }

    /**
     * @param originalRoute    draft of route
     * @param recommendedStop  recommended stop
     * @param alternativeStops alternative stops
     */
    public PlannedRoute(Route originalRoute, RouteLocation recommendedStop, ArrayList<RouteLocation> alternativeStops) {
        this(originalRoute.getFinalDestination(), originalRoute.getEta(), originalRoute.getDistance(),
                originalRoute.getOverviewPolyline(), originalRoute.getDetailedPolyline(),
                originalRoute.getCheckpoints(), recommendedStop, alternativeStops);
    }

    /**
     * Copy constructor.
     *
     * @param other PlannedRoute to copy.
     */
    public PlannedRoute(PlannedRoute other) {
        super(other);
        this.recommendedStop = other.recommendedStop;
        this.alternativeStops = other.alternativeStops;
    }


    /**
     * Get the recommended stop for the route.
     *
     * @return recommended stop (null if no stop is recommended).
     */
    public RouteLocation getRecommendedStop() {
        return recommendedStop;
    }

    /**
     * Get the alternative stops for this route.
     *
     * @return alternative stops.
     */
    public ArrayList<RouteLocation> getAlternativeStops() {
        return alternativeStops;
    }
}
