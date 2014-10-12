package truckerboys.otto.placeSuggestion;

import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;

import truckerboys.otto.placesAPI.GooglePlacesJSONDecoder;
import truckerboys.otto.utils.GoogleRequesterHandler;
import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.MapLocation;

public class GooglePlacesAutoComplete implements IPlacesAutoComplete {
    private static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final String GOOGLE_KEY = "AIzaSyDEzAa31Uxan5k_06udZBkMRkZb1Ju0aSk";

    @Override
    public List<String> getSuggestedAddresses(String input) throws InvalidRequestException, NoConnectionException {
        return getSuggestedAddresses(input, null);
    }

    @Override
    public List<String> getSuggestedAddresses(String input, MapLocation currentLocation) throws InvalidRequestException, NoConnectionException {
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
        }catch (ExecutionException e) {
            e.printStackTrace();
            throw new NoConnectionException(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new NoConnectionException(e.getMessage());
        }
        Log.w("Response", response);
        return GooglePlacesJSONDecoder.getAutoCompleteList(response);
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
