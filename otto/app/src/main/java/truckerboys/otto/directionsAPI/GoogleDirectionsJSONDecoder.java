package truckerboys.otto.directionsAPI;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.HashMap;

import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.positions.RouteLocation;

/**
 * Help class to decode a Google Direction JSON response
 *
 * @author Daniel Eineving
 */
public class GoogleDirectionsJSONDecoder {
    /**
     * Creates a route from a Google Direction JSON response
     * <p/>
     * OBS: This method uses very much typecasting,
     * think twice before changing anything
     *
     * @param response JSON response
     * @return a decoded Route
     */
    public static Route stringToRoute(String response) throws InvalidRequestException {

        RouteLocation finalDestination;
        Duration eta;
        int distance = 0;
        ArrayList<LatLng> overviewPolyline;
        ArrayList<LatLng> detailedPolyline = new ArrayList<LatLng>();
        ArrayList<RouteLocation> checkPoints = new ArrayList<RouteLocation>();
        try {

            //Creating a HashMap from from the whole response
            HashMap<String, Object> mapResponse = (HashMap<String, Object>) new Gson().fromJson(response, HashMap.class);

            //Making all routes into HashMaps
            ArrayList<LinkedTreeMap<String, Object>> routes = (ArrayList<LinkedTreeMap<String, Object>>) mapResponse.get("routes");

            ArrayList<LinkedTreeMap<String, Object>> allLegs = new ArrayList<LinkedTreeMap<String, Object>>();
            ArrayList<LinkedTreeMap<String, Object>> allSteps = new ArrayList<LinkedTreeMap<String, Object>>();

            //Combines all the legs to one common array
            for (LinkedTreeMap<String, Object> route : routes) {
                for (LinkedTreeMap<String, Object> leg : (ArrayList<LinkedTreeMap<String, Object>>) route.get("legs")) {
                    allLegs.add(leg);
                }
            }

            //Combines all steps to one common array
            for (LinkedTreeMap<String, Object> leg : allLegs) {
                for (LinkedTreeMap<String, Object> step : (ArrayList<LinkedTreeMap<String, Object>>) leg.get("steps")) {
                    allSteps.add(step);
                }
            }

            //Creating ETA and distance
            int etaSeconds = 0;
            for (LinkedTreeMap<String, Object> leg : allLegs) {
                //Some shady typecasting here, take care when changing
                etaSeconds += ((LinkedTreeMap<String, Double>) leg.get("duration")).get("value");
                distance += ((LinkedTreeMap<String, Double>) leg.get("distance")).get("value");
            }
            eta = new Duration(etaSeconds * 1000);


            //Creating big polyline
            for (LinkedTreeMap<String, Object> step : allSteps) {
                for (LatLng temp : polylineDecoder(((LinkedTreeMap<String, String>) step.get("polyline")).get("points"))) {
                    detailedPolyline.add(temp);
                }
            }
            //Getting the overview polyline
            overviewPolyline = polylineDecoder(((LinkedTreeMap<String, String>) routes.get(0).get("overview_polyline")).get("points"));

            //Getting checkpoints if there are any
            long checkpointETA = 0;
            int checkpointDistance = 0;
            for (LinkedTreeMap<String, Object> leg : allLegs) {
                LinkedTreeMap<String, Double> endLocation = (LinkedTreeMap<String, Double>) leg.get("end_location");
                LatLng coordinate = new LatLng(endLocation.get("lat"), endLocation.get("lng"));
                String address = (String) leg.get("end_address");
                checkpointETA += ((LinkedTreeMap<String, Double>) leg.get("duration")).get("value")*1000;
                checkpointDistance += ((LinkedTreeMap<String, Double>) leg.get("distance")).get("value");
                Duration tempEta = new Duration(checkpointETA);
                checkPoints.add(new RouteLocation(coordinate, address, tempEta, Instant.now().plus(tempEta), checkpointDistance));
            }

            //Creating the final destination
            finalDestination = checkPoints.get(checkPoints.size()-1);

        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidRequestException(e.getMessage());
        }
        return new Route(finalDestination, eta, distance, overviewPolyline, detailedPolyline, checkPoints);
    }

    /**
     * Decodes a polyline into an array of LatLng
     * <p/>
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

    /**
     * Creates a ETA duration from a Google Direction JSON response
     *
     * @param response Google Direction JSON
     * @return ETA to requested location
     */
    public static Duration etaToDestination(String response) throws InvalidRequestException {
        int etaSeconds = 0;
        //Some shady typecasting here, take care when changing

        try {
            //Creating a HashMap from from the whole response
            HashMap<String, Object> mapResponse = (HashMap<String, Object>) new Gson().fromJson(response, HashMap.class);

            //Making all routes into HashMaps
            ArrayList<LinkedTreeMap<String, Object>> routes = (ArrayList<LinkedTreeMap<String, Object>>) mapResponse.get("routes");

            ArrayList<LinkedTreeMap<String, Object>> allLegs = new ArrayList<LinkedTreeMap<String, Object>>();
            ArrayList<LinkedTreeMap<String, Object>> allSteps = new ArrayList<LinkedTreeMap<String, Object>>();

            //Combines all the legs to one common array
            for (LinkedTreeMap<String, Object> route : routes) {
                for (LinkedTreeMap<String, Object> leg : (ArrayList<LinkedTreeMap<String, Object>>) route.get("legs")) {
                    allLegs.add(leg);
                }
            }

            //Creating ETA

            for (LinkedTreeMap<String, Object> leg : allLegs) {
                etaSeconds += ((LinkedTreeMap<String, Double>) leg.get("duration")).get("value");
            }
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }

        return new Duration(etaSeconds * 1000);
    }
}