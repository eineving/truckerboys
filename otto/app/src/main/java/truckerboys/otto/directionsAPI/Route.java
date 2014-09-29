package truckerboys.otto.directionsAPI;

import org.apache.http.HttpResponse;
import org.joda.time.Duration;

import java.util.List;

import truckerboys.otto.planner.positions.Location;

/**
 * Created by Daniel on 2014-09-24.
 */
public class Route {
    private Location finalLocation;
    private Duration eta;
    private String polyline;
    private List<Location> checkpoints;

    public Route(Location finalLocation, Duration eta, String polyline, List<Location> checkpoints) {
        this.finalLocation = finalLocation;
        this.eta = eta;
        this.polyline = polyline;
        this.checkpoints = checkpoints;

    }

    public Location getFinalLocation() {
        return finalLocation;
    }

    public Duration getEta() {
        return eta;
    }

    public String getPolyline() {
        return polyline;
    }
}
