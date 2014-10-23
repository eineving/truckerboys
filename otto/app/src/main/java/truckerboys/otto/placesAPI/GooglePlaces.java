package truckerboys.otto.placesAPI;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import truckerboys.otto.utils.GoogleRequesterHandler;
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.RouteLocation;

public class GooglePlaces implements IPlaces {
    private static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final String GOOGLE_KEY = "AIzaSyDEzAa31Uxan5k_06udZBkMRkZb1Ju0aSk";

    @Override
    public ArrayList<RouteLocation> getNearbyGasStations(LatLng position) throws NoConnectionException {
        String response;

        String request = PLACES_URL + "nearbysearch/json?location=" + position.latitude + "," + position.longitude + "&&radius=5000types=gas_station" + "&key=" + GOOGLE_KEY;

        try {
            response = new GoogleRequesterHandler().execute(request).get();
            return GooglePlacesJSONDecoder.getRouteLocations(response);

        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new NoConnectionException(e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new NoConnectionException(e.getMessage());
        }
    }


    @Override
    public ArrayList<RouteLocation> getNearbyRestLocations(LatLng position) {
        String response;

        //TODO What types to search for?  https://developers.google.com/places/documentation/search
        String request = PLACES_URL + "nearbysearch/json?location=" + position.latitude + "," + position.longitude + "&radius=5000&types=gas_station" + "&key=" + GOOGLE_KEY;

        try {
            response = new GoogleRequesterHandler().execute(request).get();
            return GooglePlacesJSONDecoder.getRouteLocations(response);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
