package truckerboys.otto.placesAPI;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public interface IPlaces {
    /**
     * Get a suggested address (location) from a user input String
     * @param input user input
     * @return suggestedAddresses
     */
    public List<String> getSuggestedAddresses(String input);

    /**
     * Get the closest rest locations to given position
     * @param position target position to calculate from
     * @return rest locations nearby given positions
     */
    public ArrayList<Location> getNearbyRestLocations(LatLng position);

    /**
     * Get the closest gas stations to given position
     * @param position target position to calculate from
     * @return gas stations nearby given positions
     */
    public ArrayList<Location> getNearbyGasStations(LatLng position);
}
