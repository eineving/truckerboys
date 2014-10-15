package truckerboys.otto.placesAPI;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import truckerboys.otto.utils.GoogleRequesterHandler;
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.GasStation;
import truckerboys.otto.utils.positions.MapLocation;
import truckerboys.otto.utils.positions.RestLocation;

public class GooglePlaces implements IPlaces {
    private static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final String GOOGLE_KEY = "AIzaSyDEzAa31Uxan5k_06udZBkMRkZb1Ju0aSk";

    @Override
    public ArrayList<GasStation> getNearbyGasStations(LatLng position) throws NoConnectionException {
        String response;

        String request = PLACES_URL + "nearbysearch/json?location=" + position.latitude + "," + position.longitude + "&&radius=3000types=gas_station" + "&key=" + GOOGLE_KEY;

        try {
            response = new GoogleRequesterHandler().execute(request).get();
            return GooglePlacesJSONDecoder.getGasStations(response);

        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new NoConnectionException(e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new NoConnectionException(e.getMessage());
        }
    }


    @Override
    public ArrayList<MapLocation> getNearbyRestLocations(LatLng position) {
        String response;

        //TODO What types to search for?  https://developers.google.com/places/documentation/search
        String request = PLACES_URL + "nearbysearch/json?location=" + position.latitude + "," + position.longitude + "&radius=3000&types=gas_station" + "&key=" + GOOGLE_KEY;

        try {
            response = new GoogleRequesterHandler().execute(request).get();
            return GooglePlacesJSONDecoder.getRestLocations(response);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
