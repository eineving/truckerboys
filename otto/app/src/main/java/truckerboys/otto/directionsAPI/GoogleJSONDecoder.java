package truckerboys.otto.directionsAPI;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
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
        Location finalLocation;
        Duration eta;
        String overviewPolyline = "";

        ArrayList<Location> checkpoints = new ArrayList<Location>();
        //ArrayList<LatLng> tempPositions = new ArrayList<LatLng>();

        //Creating a HashMap from from the whole response
        HashMap<String, Object> mapResponse = (HashMap<String, Object>)new Gson().fromJson(response, HashMap.class);

        //Making all routes into HashMaps
        ArrayList<Object> routes = new Gson().fromJson((String)mapResponse.get("routes"), ArrayList.class);



        Log.w("HashMap", routes.get(0).toString());
        return null;
    }

    public static LatLng[] polylineDecoder(String polyline){
        return null;
    }
}