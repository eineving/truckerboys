package truckerboys.otto.newroute;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.utils.eventhandler.EventBus;
import truckerboys.otto.utils.eventhandler.events.RouteRequestEvent;
import truckerboys.otto.utils.eventhandler.events.RefreshHistoryEvent;

/**
 * Created by Mikael Malmqvist on 2014-10-06.
 * Class for handling some logic for the RouteActivity.
 */
public class RoutePresenter {

    /**
     * Sends the selected address (string) to the EventTruck by converting
     * it to an actual address location.
     * @param nameOfLocation the string selected.
     */
    public void sendLocation(String nameOfLocation, List<String> checkpoints, Geocoder coder){

        final Geocoder geocoder = coder;
        final String str = nameOfLocation;
        final List<String> checks = checkpoints;


        new Thread(new Runnable() {
            @Override
            public void run() {
                int maxResults = 1;

                try {
                    // Creates an array of addresses (of length 1) based on the string selected
                    // and gets the first result
                    List<Address> locations = geocoder.getFromLocationName(str, maxResults);

                    ArrayList<Address> checkpointsArray = new ArrayList<Address>();

                    for(String checkpoint : checks) {

                        checkpointsArray.add((geocoder.getFromLocationName(checkpoint, maxResults)).get(0));
                    }

                    if(locations.size() > 0) {
                        Address location = locations.get(0);
                        //Address checkPoint = checkPoints.get(0);
                        EventBus.getInstance().newEvent(new RouteRequestEvent(location, checkpointsArray));
                    }

                } catch (IOException e) {
                    System.out.println("Something went horribly wrong regarding you request!");
                }
            }
        }).start();



    }

    /**
     * Method for loading latest destinations.
     */
    public void loadHistory(SharedPreferences history) {
        String place1 = history.getString("place1", ""); // Latest place
        String place2 = history.getString("place2", "");
        String place3 = history.getString("place3", "");


        EventBus.getInstance().newEvent(new RefreshHistoryEvent(place1, place2, place3));
    }

    /**
     * Method for saving destination history.
     */
    public void saveHistory(SharedPreferences history, String locationName) {
        String place1 = history.getString("place1", ""); // Latest place
        String place2 = history.getString("place2", "");
        String place3;

        // Switch them places
        place3 = place2;
        place2 = place1;
        place1 = locationName;

        SharedPreferences.Editor editor = history.edit();
        editor.putString("place1", place1);
        editor.putString("place2", place2);
        editor.putString("place3", place3);

        editor.apply();


    }

}
