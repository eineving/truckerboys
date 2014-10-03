package truckerboys.otto.placesAPI;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import truckerboys.otto.utils.GoogleRequesterHandler;

public class GooglePlaces implements IPlaces {
    private static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final String GOOGLE_KEY = "AIzaSyDEzAa31Uxan5k_06udZBkMRkZb1Ju0aSk";


    @Override
    public List<String> getSuggestedAddresses(String input) {
        //Creating a request string
        String request = PLACES_URL;
        String response;

        try {
            response = new GoogleRequesterHandler().execute(request).get();
            return GooglePlacesJSONDecoder.getAutoCompleteList(response);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Location> getNearbyRestLocations(LatLng position) {
        return null;
    }

    @Override
    public ArrayList<Location> getNearbyGasStations(LatLng position) {
        return null;
    }
}
