package truckerboys.otto.utils.positions;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

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
    public GasStation(LatLng coordinate,String address, Duration eta, String name ) {
        super(coordinate);
        this.name = name;
        setAddress(address);
        setEta(eta);
    }

    /**
     * Get the name of the gas station
     * @return name of the gas station
     */
    public String getName() {
        return name;
    }
}
