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
        String overviewPolyline = "";
        int nbrOfLegs;

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
        LinkedTreeMap<String, Object> lastLeg = allLegs.get(allLegs.size()-1);
        LatLng coordinate = new LatLng(((LinkedTreeMap<String, Double>)lastLeg.get("end_location")).get("lat"),
                ((LinkedTreeMap<String, Double>)lastLeg.get("end_location")).get("lng"));
        finalDestination = new Location(coordinate);
        finalDestination.setAddress((String)lastLeg.get("end_address"));

        //TODO Creating ETA
        
        //return new Route(finalDestination, )
        return null;
    }

    private static ArrayList<LatLng> polylineDecoder(String polyline) {
        return null;
    }
}