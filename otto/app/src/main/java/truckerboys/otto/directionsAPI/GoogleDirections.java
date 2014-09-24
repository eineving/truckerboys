package truckerboys.otto.directionsAPI;

import android.net.http.AndroidHttpClient;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.client.methods.HttpGet;
import org.joda.time.Duration;
import java.util.List;

import truckerboys.otto.planner.positions.Location;


public class GoogleDirections implements IDirections {
    private AndroidHttpClient httpClient;

    //TODO Eineving hardcoded or read from class or .txt?
    private static final String DIRECTIONS_URL = "http://maps.googleapis.com/maps/api/directions/";
    private static final String GOOGLE_KEY = "AIzaSyDEzAa31Uxan5k_06udZBkMRkZb1Ju0aSk";

    public GoogleDirections() {
        //TODO Eineving check if userAgent "truckerboys" are valid or what should replace it
        httpClient = AndroidHttpClient.newInstance("truckerboys");
    }

    @Override
    public Route getRoute(LatLng currentPosition, Location finalDestination, RoutePreferences preferences,
                          Location... checkpoint) throws Exception {
        return new Route(httpClient.execute(new HttpGet(DIRECTIONS_URL + jsonStringCreator(currentPosition,
                finalDestination, preferences, checkpoint))));
    }

    @Override
    public Route getRoute(LatLng currentPosition, Location finalDestination, RoutePreferences preferences) throws Exception {
        return getRoute(currentPosition, finalDestination, preferences, null);
    }

    @Override
    public Route getRoute(LatLng currentPosition, Location finalDestination, Location... checkpoint) throws Exception {
        return getRoute(currentPosition, finalDestination, null, checkpoint);
    }

    @Override
    public Route getRoute(LatLng currentPosition, Location finalDestination) throws Exception {
        return getRoute(currentPosition, finalDestination, null, null);
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

    private String jsonStringCreator(LatLng currentPosition, Location finalDestination,
                                     RoutePreferences preferences, Location[] checkpoint) {
        String returnValue = "json?origin=" + currentPosition.latitude + "," + currentPosition.longitude +
                "&destination=" + finalDestination.getLongitude() + "," + finalDestination.getLatitude();

        if(checkpoint != null ) {
            returnValue += "&waypoints=";
            for (int i = 0; i < checkpoint.length; i++) {
                returnValue += checkpoint[i].getLatitude() + "," + checkpoint[i].getLongitude();
                if(i!= checkpoint.length-1){
                    returnValue += "|";
                }
            }
        }
        returnValue += "&key=" + GOOGLE_KEY;

        return returnValue;
    }
}
