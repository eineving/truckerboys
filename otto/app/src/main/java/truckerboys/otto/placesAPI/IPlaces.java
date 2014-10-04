package truckerboys.otto.placesAPI;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.utils.positions.GasStation;
import truckerboys.otto.utils.positions.RestLocation;

public interface IPlaces {
    /**
     * Get a suggested address (location) from a user input String
     * @param input user input
     * @return suggested addresses
     */
    public List<String> getSuggestedAddresses(String input);

    /**
     *
     * Get a suggested address (location) from a user input String
     * @param input user input
     * @param currentLocation location to focus the searches from
     * @return suggested addresses
     */
    public List<String> getSuggestedAddresses(String input, Location currentLocation);

    /**
     * Get the closest rest locations to given position
     * @param position target position to calculate from
     * @return rest locations nearby given positions
     */
    public ArrayList<RestLocation> getNearbyRestLocations(LatLng position);

    /**
     * Get the closest gas stations to given position
     * @param position target position to calculate from
     * @return gas stations nearby given positions
     */
    public ArrayList<GasStation> getNearbyGasStations(LatLng position);
}
