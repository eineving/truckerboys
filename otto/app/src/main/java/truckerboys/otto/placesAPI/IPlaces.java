package truckerboys.otto.placesAPI;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.RouteLocation;

public interface IPlaces {
    /**
     * Get the closest route locations to given position (gas stations included).
     *
     * @param position target position to calculate from.
     * @return rest locations nearby given positions (gas stations included) and contains null fields.
     */
    public ArrayList<RouteLocation> getNearbyRestLocations(LatLng position);

    /**
     * Get the closest route locations to given position.
     *
     * @param position target position to calculate from.
     * @return gas stations nearby given positions and contains null fields.
     */
    public ArrayList<RouteLocation> getNearbyGasStations(LatLng position) throws NoConnectionException;
}
