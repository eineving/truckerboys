package truckerboys.otto.newroute;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.NewDestination;
import truckerboys.otto.utils.eventhandler.events.RefreshHistoryEvent;

/**
 * Created by root on 2014-10-06.
 */
public class RoutePresenter implements IEventListener {

    public RoutePresenter() {
        EventTruck.getInstance().subscribe(this);
    }

    /**
     * Sends the selected address (string) to the EventTruck by converting
     * it to an actual address location.
     * @param nameOfLocation the string selected.
     */
    public void sendLocation(String nameOfLocation, Geocoder coder){

        int maxResults = 1;

        try {
            // Creates an array of addresses (of length 1) based on the string selected
            // and gets the first result
            List<Address> locations = coder.getFromLocationName(nameOfLocation, maxResults);

            if(locations.size() > 0) {
                Address location = locations.get(0);
                EventTruck.getInstance().newEvent(new NewDestination(location));
            }

        } catch (IOException e) {
            System.out.println("Something went horribly wrong regarding you request!");
        }

    }

    /**
     * Method for loading latest destinations.
     */
    public void loadHistory(SharedPreferences history) {
        String place1 = history.getString("place1", ""); // Latest place
        String place2 = history.getString("place2", "");
        String place3 = history.getString("place3", "");


        EventTruck.getInstance().newEvent(new RefreshHistoryEvent(place1, place2, place3));
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

        editor.commit();


    }

    @Override
    public void performEvent(Event event) {

    }
}
