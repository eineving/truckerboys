package truckerboys.otto.directionsAPI;

import android.util.Log;

import org.joda.time.Duration;

import java.util.concurrent.ExecutionException;

import truckerboys.otto.utils.GoogleRequesterHandler;
import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.MapLocation;


public class GoogleDirections implements IDirections {
    private GoogleRequesterHandler requesterHandler;

    private static final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/";
    private static final String GOOGLE_KEY = "AIzaSyDEzAa31Uxan5k_06udZBkMRkZb1Ju0aSk";

    @Override
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination, RoutePreferences preferences,
                          MapLocation... checkpoint) throws NoConnectionException, InvalidRequestException {

        String response;
        try {
            response = new GoogleRequesterHandler().execute(DIRECTIONS_URL + jsonStringCreator(currentPosition, finalDestination, null, checkpoint)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new NoConnectionException("InterruptedException");
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new NoConnectionException("ExecutionException");
        }
        return GoogleDirectionsJSONDecoder.stringToRoute(response);
    }

    @Override
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination, RoutePreferences preferences) throws NoConnectionException, InvalidRequestException {
        return getRoute(currentPosition, finalDestination, preferences, null);
    }

    @Override
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination, MapLocation... checkpoint) throws NoConnectionException, InvalidRequestException {
        return getRoute(currentPosition, finalDestination, null, checkpoint);
    }

    @Override
    public Route getRoute(MapLocation currentPosition, MapLocation finalDestination) throws NoConnectionException, InvalidRequestException {
        return getRoute(currentPosition, finalDestination, null, null);
    }

    @Override
    public Duration getETA(MapLocation currentPosition, MapLocation finalDestination) throws NoConnectionException, InvalidRequestException {
        String response;
        try {
            response = new GoogleRequesterHandler().execute(DIRECTIONS_URL + jsonStringCreator(currentPosition, finalDestination, null, null)).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new NoConnectionException("InterruptedException");
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new NoConnectionException("ExecutionException");
        }
        return GoogleDirectionsJSONDecoder.etaToDestination(response);
    }

    private String jsonStringCreator(MapLocation currentPosition, MapLocation finalDestination,
                                     RoutePreferences preferences, MapLocation[] checkpoint) {

        String returnValue = "json?origin=" + currentPosition.getLatitude() + "," + currentPosition.getLongitude() +
                "&destination=" + finalDestination.getLatitude() + "," + finalDestination.getLongitude();

        if (checkpoint != null) {
            returnValue += "&waypoints=";
            for (int i = 0; i < checkpoint.length; i++) {
                returnValue += checkpoint[i].getLatitude() + "," + checkpoint[i].getLongitude();
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
