package truckerboys.otto.directionsAPI;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

import truckerboys.otto.utils.GoogleRequesterHandler;
import truckerboys.otto.utils.positions.Location;


public class GoogleDirections implements IDirections {
    private GoogleRequesterHandler requesterHandler;

    private static final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/";
    private static final String GOOGLE_KEY = "AIzaSyDEzAa31Uxan5k_06udZBkMRkZb1Ju0aSk";

    @Override
    public Route getRoute(LatLng currentPosition, Location finalDestination, RoutePreferences preferences,
                          Location... checkpoint) throws Exception {
        String response = new GoogleRequesterHandler().execute(DIRECTIONS_URL + jsonStringCreator(currentPosition, finalDestination.getLatLng(), null, null)).get();
        return GoogleDirectionsJSONDecoder.stringToRoute(response);
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
    public Duration getETA(LatLng currentPosition, LatLng finalDestination) throws Exception {
        String response = new GoogleRequesterHandler().execute(DIRECTIONS_URL + jsonStringCreator(currentPosition, finalDestination, null, null)).get();
        return GoogleDirectionsJSONDecoder.etaToDestination(response);
    }

    private String jsonStringCreator(LatLng currentPosition, LatLng finalDestination,
                                     RoutePreferences preferences, Location[] checkpoint) {

        String returnValue = "json?origin=" + currentPosition.latitude + "," + currentPosition.longitude +
                "&destination=" + finalDestination.latitude + "," + finalDestination.longitude;

        if (checkpoint != null) {
            returnValue += "&waypoints=";
            for (int i = 0; i < checkpoint.length; i++) {
                returnValue += checkpoint[i].getLatLng().latitude + "," + checkpoint[i].getLatLng().longitude;
                if (i != checkpoint.length - 1) {
                    returnValue += "|";
                }
            }
        }
        returnValue += "&key=" + GOOGLE_KEY;
        Log.w("GoogleDirections", returnValue);
        return returnValue;
    }


}
