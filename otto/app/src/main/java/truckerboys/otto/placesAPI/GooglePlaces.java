package truckerboys.otto.placesAPI;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import truckerboys.otto.utils.GoogleRequesterHandler;
import truckerboys.otto.utils.positions.GasStation;
import truckerboys.otto.utils.positions.MapLocation;
import truckerboys.otto.utils.positions.RestLocation;

public class GooglePlaces implements IPlaces {
    private static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final String GOOGLE_KEY = "AIzaSyDEzAa31Uxan5k_06udZBkMRkZb1Ju0aSk";


    @Override
    public List<String> getSuggestedAddresses(String input) {
        return getSuggestedAddresses(input, null);
    }

    @Override
    public List<String> getSuggestedAddresses(String input, MapLocation currentLocation) {
        String response;
        Log.w("Input", input);

        input = replaceSwedishLetters(input);
        //Creating a request string
        String request = PLACES_URL + "autocomplete/json?input=" + input;

        //Adding location to search from
        if (currentLocation != null) {
            request += "&location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        }


        //Adding key ending to String
        request += "&key=" + GOOGLE_KEY;
        Log.w("Request", request);

        try {
            response = new GoogleRequesterHandler().execute(request).get();
            Log.w("Response", response);
            return GooglePlacesJSONDecoder.getAutoCompleteList(response);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<GasStation> getNearbyGasStations(LatLng position) {
        String response;

        String request = PLACES_URL + "nearbysearch/json?location=" + position.latitude + "," + position.longitude + "&&radius=3000types=gas_station" + "&key=" + GOOGLE_KEY;

        try {
            response = new GoogleRequesterHandler().execute(request).get();
            return GooglePlacesJSONDecoder.getGasStations(response);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public ArrayList<RestLocation> getNearbyRestLocations(LatLng position) {
        String response;

        //TODO What types to search for?  https://developers.google.com/places/documentation/search
        String request = PLACES_URL + "nearbysearch/json?location=" + position.latitude + "," + position.longitude + "&radius=3000&types=parking" + "&key=" + GOOGLE_KEY;

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

    /**
     * Returns a string where å, ä and ö is replaced by a, a and o
     *
     * @param input String that needs swedish letters replaced
     * @return input String with the swedish letters replaced
     */
    private String replaceSwedishLetters(String input) {
        input = input.replace("å", "a");
        input = input.replace("Å", "A");
        input = input.replace("ä", "a");
        input = input.replace("Ä", "A");
        input = input.replace("ö", "o");
        input = input.replace("Ö", "O");
        return input;
    }


}
