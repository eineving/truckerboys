package truckerboys.otto.utils.positions;

import com.google.android.gms.maps.model.LatLng;

/**
 * Representing a gas station
 */
public class GasStation extends MapLocation {
    private String name;

    /**
     * Create a new gas station
     * @param coordinate coordinates
     * @param name name of the gas station
     */
    public GasStation(LatLng coordinate, String name) {
        super(coordinate);
        this.name = name;
    }

    /**
     * Get the name of the gas station
     * @return name of the gas station
     */
    public String getName() {
        return name;
    }
}
