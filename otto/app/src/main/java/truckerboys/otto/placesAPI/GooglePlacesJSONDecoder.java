package truckerboys.otto.placesAPI;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.positions.RouteLocation;

/**
 * Help class to decode a Google Direction JSON response
 *
 * @author Daniel Eineving
 */
public class GooglePlacesJSONDecoder {

    /**
     * Creates an autocomplete list from the Google Places API
     * <p/>
     * OBS: This method uses very much typecasting,
     * think twice before changing anything
     *
     * @param response JSON response
     * @return a list of suggestions
     */
    public static List<String> getAutoCompleteList(String response) throws InvalidRequestException {
        try {
            ArrayList<String> decoded = new ArrayList<String>();

            //Creating a HashMap from from the whole response
            HashMap<String, Object> mapResponse = (HashMap<String, Object>) new Gson().fromJson(response, HashMap.class);

            //Making all predictions into HashMaps
            ArrayList<LinkedTreeMap<String, Object>> predictions = (ArrayList<LinkedTreeMap<String, Object>>) mapResponse.get("predictions");

            for (LinkedTreeMap<String, Object> prediction : predictions) {
                decoded.add((String) prediction.get("description"));
            }
            return decoded;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidRequestException(e.getMessage());
        }
    }

    public static ArrayList<RouteLocation> getRouteLocations(String encoded) {
        ArrayList<RouteLocation> decoded = new ArrayList<RouteLocation>();

        //Creating a HashMap from from the whole response
        HashMap<String, Object> mapResponse = (HashMap<String, Object>) new Gson().fromJson(encoded, HashMap.class);

        //Making all results into HashMaps
        ArrayList<LinkedTreeMap<String, Object>> results = (ArrayList<LinkedTreeMap<String, Object>>) mapResponse.get("results");

        for (LinkedTreeMap<String, Object> location : results) {
            LinkedTreeMap<String, Double> coordinate = (LinkedTreeMap<String, Double>) ((LinkedTreeMap<String, Object>) location.get("geometry")).get("location");
            LatLng position = new LatLng(coordinate.get("lat"), coordinate.get("lng"));
            ArrayList<String> types = (ArrayList<String>) location.get("types");
            RouteLocation temp = new RouteLocation(position, "", null, null, -1);
            temp.setName((String) location.get("name"));
            temp.setType(types);
            decoded.add(temp);
        }

        return decoded;
    }
}
