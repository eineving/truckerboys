package truckerboys.otto.directionsAPI;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.joda.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import truckerboys.otto.planner.positions.Location;

/**
 * Help class to decode a Google Direction JSON response
 * @author Daniel Eineving
 */
public class GoogleJSONDecoder {

    /**
     * Creates a route from a Google Direction JSON response
     *
     * OBS: This method uses very much typecasting,
     * think twice before changing anything
     *
     * @param response JSON response
     * @return a decoded Route
     */
    public static Route stringToRoute(String response) {
        Location finalDestination;
        Duration eta;
        int distance = 0;
        String overviewPolyline = "";
        String detailedPolyline = "";

        ArrayList<Location> checkpoints = new ArrayList<Location>();
        //ArrayList<LatLng> tempPositions = new ArrayList<LatLng>();

        //Creating a HashMap from from the whole response
        HashMap<String, Object> mapResponse = (HashMap<String, Object>) new Gson().fromJson(response, HashMap.class);

        //Making all routes into HashMaps
        ArrayList<LinkedTreeMap<String, Object>> routes = (ArrayList<LinkedTreeMap<String, Object>>) mapResponse.get("routes");

        ArrayList<LinkedTreeMap<String, Object>> allLegs = new ArrayList<LinkedTreeMap<String, Object>>();
        ArrayList<LinkedTreeMap<String, Object>> allSteps = new ArrayList<LinkedTreeMap<String, Object>>();

        //Combines all the legs to one common array
        for (LinkedTreeMap<String, Object> route : routes) {
            for (LinkedTreeMap<String, Object> leg : (ArrayList<LinkedTreeMap<String, Object>>) route.get("legs"))
                allLegs.add(leg);
        }

        //Combines all steps to one common array
        for (LinkedTreeMap<String, Object> leg : allLegs) {
            for (LinkedTreeMap<String, Object> step : (ArrayList<LinkedTreeMap<String, Object>>) leg.get("steps"))
                allSteps.add(step);
        }

        //TODO remove printouts
        Log.w("Routes", "" + routes.size());
        Log.w("Legs", "" + allLegs.size());
        Log.w("Steps", "" + allSteps.size());


        //Creating the final destination
        LinkedTreeMap<String, Object> lastLeg = allLegs.get(allLegs.size() - 1);
        LatLng coordinate = new LatLng(((LinkedTreeMap<String, Double>) lastLeg.get("end_location")).get("lat"),
                ((LinkedTreeMap<String, Double>) lastLeg.get("end_location")).get("lng"));
        finalDestination = new Location(coordinate);
        finalDestination.setAddress((String) lastLeg.get("end_address"));

        //Creating ETA and distance
        int etaSeconds = 0;
        for (LinkedTreeMap<String, Object> leg : allLegs) {
            //Some shady typecasting here, take care when changing
            etaSeconds +=  ((LinkedTreeMap<String, Double>) leg.get("duration")).get("value");
            distance +=  ((LinkedTreeMap<String, Double>) leg.get("distance")).get("value");
        }
        eta = new Duration(etaSeconds * 1000);

        //Creating big polyline String
        for (LinkedTreeMap<String, Object> step : allSteps) {
            detailedPolyline += ((LinkedTreeMap<String, String>) step.get("polyline")).get("points");
        }

        //Getting the overview polyline
        overviewPolyline = ((LinkedTreeMap<String, String>) routes.get(0).get("overview_polyline")).get("points");

        //TODO remove printouts
        Log.w("finalDestination", finalDestination.getAddress());
        Log.w("ETA", eta.toStandardSeconds().getSeconds() + "");
        Log.w("distance", distance + "");
        Log.w("overviewPolyline", overviewPolyline);
        Log.w("detailedPolyline", detailedPolyline);


        return new Route(finalDestination, eta, distance, polylineDecoder(overviewPolyline), polylineDecoder(detailedPolyline));
    }

    /**
     * Decodes a polyline into an array of LatLng
     *
     * Based on example from
     * http://wptrafficanalyzer.in/blog/drawing-driving-route-directions-between-two-locations-using-google-directions-in-google-map-android-api-v2/
     * Written by George Mathew
     *
     * @param encoded encoded polyline
     * @return decoded polyline as an ArrayList of LatLng
     */
    private static ArrayList<LatLng> polylineDecoder(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}