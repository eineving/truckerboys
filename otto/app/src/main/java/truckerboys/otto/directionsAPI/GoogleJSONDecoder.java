package truckerboys.otto.directionsAPI;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.HashMap;

import truckerboys.otto.planner.positions.Location;

/**
 * Created by Daniel on 2014-09-29.
 */
public class GoogleJSONDecoder {
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
        Log.w("HashMap", mapResponse.toString());

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

        Log.w("finalDestination", finalDestination.getAddress());
        Log.w("ETA", eta.toStandardSeconds().getSeconds() + "");
        Log.w("distance", distance + "");
        Log.w("overviewPolyline", overviewPolyline);
        Log.w("detailedPolyline", detailedPolyline);


        return new Route(finalDestination, eta, distance, polylineDecoder(overviewPolyline), polylineDecoder(detailedPolyline));
    }

    private static ArrayList<LatLng> polylineDecoder(String polyline) {
        return null;
    }
}