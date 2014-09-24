package truckerboys.otto.directionsAPI;

import android.net.http.AndroidHttpClient;

import org.joda.time.Duration;

import java.util.List;

import truckerboys.otto.planner.positions.Location;


public class GoogleDirections implements IDirections {
    private AndroidHttpClient httpClient;
    private static String DIRECTIONS_URL = "http://maps.googleapis.com/maps/api/directions/";

    public GoogleDirections(){
        //TODO Eineving check if userAgent "truckerboys" are valid or what should replace it
        httpClient = AndroidHttpClient.newInstance("truckerboys");
    }


    @Override
    public Route getRoute(Location finalDestination, Location... checkpoint) throws Exception {
        return null;
    }

    @Override
    public List<Location> getGasStationsAlongRoute() throws Exception {
        return null;
    }

    @Override
    public List<Location> getRestLocationsAlongRoute() throws Exception {
        return null;
    }

    @Override
    public Duration getETA(Location location) throws Exception {
        return null;
    }
}
