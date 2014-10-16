package truckerboys.otto.utils.positions;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

import java.util.List;

/**
 * Class representing a rest location
 */
public class RouteLocation extends MapLocation {
    private List<String> type;
    private String name;
    private Duration eta;
    private String address;

    /**
     * Create a new rest location
     *
     * @param latLng  coordinates
     * @param name    name of the rest location
     * @param address the address of the location
     * @param type    type of rest location
     * @param eta     estimated time til arrival
     */
    public RouteLocation(LatLng latLng, String name, String address, List<String> type, Duration eta) {
        super(latLng);
        this.name = name;
        this.address = address;
        this.type = type;
        this.eta = eta;
    }

    /**
     * What type of rest location this is
     *
     * @return type of rest location
     */
    public List<String> getType() {
        return type;
    }

    /**
     * The name of the rest location
     *
     * @return name of the rest location
     */
    public String getName() {
        return name;
    }

    /**
     * Get the estimated time til arrival
     * @return estimated time til arrival
     */
    public Duration getEta() {
        return eta;
    }

    /**
     * Get the address of the location
     * @return address of the location
     */
    public String getAddress() {
        return address;
    }
}
