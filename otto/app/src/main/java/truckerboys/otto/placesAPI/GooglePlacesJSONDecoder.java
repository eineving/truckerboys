package truckerboys.otto.placesAPI;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Help class to decode a Google Direction JSON response
 * @author Daniel Eineving
 */
public class GooglePlacesJSONDecoder {

    /**
     * Creates an autocomplete list from the Google Places API
     *
     * OBS: This method uses very much typecasting,
     * think twice before changing anything
     *
     * @param response JSON response
     * @return a list of suggestions
     */
    public static List<String> getAutoCompleteList(String response) {
        ArrayList<String> decoded = new ArrayList<String>();

        //Creating a HashMap from from the whole response
        HashMap<String, Object> mapResponse = (HashMap<String, Object>) new Gson().fromJson(response, HashMap.class);

        //Making all predictions into HashMaps
        ArrayList<LinkedTreeMap<String, Object>> predictions = (ArrayList<LinkedTreeMap<String, Object>>) mapResponse.get("predictions");

        for(LinkedTreeMap<String, Object> prediction : predictions) {
            decoded.add((String) prediction.get("description")) ;
        }
        return decoded;
    }
}
