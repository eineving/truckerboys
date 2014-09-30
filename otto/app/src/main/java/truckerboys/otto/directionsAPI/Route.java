package truckerboys.otto.directionsAPI;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.internal.bind.ArrayTypeAdapter;

import org.apache.http.HttpResponse;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.planner.positions.Location;

/**
 * Created by Daniel on 2014-09-24.
 */
public class Route {
    private Location finalLocation;
    private Duration eta;
    private int distance;
    private ArrayList<LatLng> overviewPolyline;
    private ArrayList<LatLng> detailedPolyline;
    //private List<Location> checkpoints;

    /**
     *
     * @param finalLocation
     * @param eta time till estimated arrival
     * @param overviewPolyline
     * @param detailedPolyline
     * @param distance distance to final destination in meters
     */
    public Route(Location finalLocation, Duration eta, int distance, ArrayList<LatLng> overviewPolyline, ArrayList<LatLng> detailedPolyline) {
        this.finalLocation = finalLocation;
        this.eta = eta;
        this.overviewPolyline = overviewPolyline;
        this.detailedPolyline = detailedPolyline;
        this.distance = distance;
    }

    public Location getFinalLocation() {
        return finalLocation;
    }


    public Duration getEta() {
        return eta;
    }

    public ArrayList<LatLng> getOverviewPolyline() {
        return overviewPolyline;
    }

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
