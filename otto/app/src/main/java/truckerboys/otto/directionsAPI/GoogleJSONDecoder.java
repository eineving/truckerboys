package truckerboys.otto.directionsAPI;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import truckerboys.otto.planner.positions.Location;

/**
 * Created by Daniel on 2014-09-29.
 */
public class GoogleJSONDecoder {
    public static Route stringToRoute(String response) {
        Location finalLocation;
        Duration eta;
        String polyline = "";
        ArrayList<Location> checkpoints = new ArrayList<Location>();
        //ArrayList<LatLng> tempPositions = new ArrayList<LatLng>();

        final String ORIGINAL_RESPONSE = response;
        //Creating JSONObject from string
        JSONArray json = null;
        try {
            json = new JSONArray(response);


        for (int i = 0; i < json.length(); i++) {
            JSONArray jsonRoute = json.getJSONArray(i);
            //TODO specific route stuff here

            for (int j = 0; j < jsonRoute.length(); j++) {
                JSONArray jsonLegs = jsonRoute.getJSONArray(j);
                //eta = new Duration(Integer.getInteger(jsonLegs.getJSONObject("duration").getString("value"))*1000);
                for (int k = 0; k < jsonLegs.length(); k++) {
                    JSONArray jsonSteps = jsonLegs.getJSONArray(k);

                    for (int l = 0; l < jsonSteps.length(); l++) {
                        JSONObject step = jsonSteps.getJSONObject(l);

                        JSONObject start = step.getJSONObject("start_location");

                        // tempPositions.add(new LatLng(start.getDouble("lat"), start.getDouble("lng")));

                        polyline += step.getJSONObject("polyline").getString("points");
                    }
                }
            }
        }

        } catch (JSONException e) {
            Log.w("Decoder", e.getMessage());
            e.printStackTrace();
        }
        Log.w("polyline", polyline);
        return null;
    }
}
