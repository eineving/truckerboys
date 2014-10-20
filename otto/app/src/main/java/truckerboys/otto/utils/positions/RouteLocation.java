package truckerboys.otto.utils.positions;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a rest location
 */
public class RouteLocation extends MapLocation {
    private List<String> type;
    private String name;
    private Duration eta;
    private Instant timeOfArival;
    private String address;
    private int distance;

    /**
     * Create a new rest location
     *
     * @param latLng   coordinates
     * @param address  the address of the location
     * @param eta      estimated time til arrival
     * @param distance distance to location in meters
     */
    public RouteLocation(LatLng latLng, String address, Duration eta, Instant timeOfArival , int distance) {
        super(latLng);
        this.address = address;
        this.eta = eta;
        this.distance = distance;
        this.timeOfArival = timeOfArival;
        this.type = new ArrayList<String>();
    }

    /**
     * Copy constructor
     *
     * @param other RouteLocation to copy
     */
    public RouteLocation(RouteLocation other) {
        super(other);
        this.type = other.type;
        this.name = other.name;
        this.eta = new Duration(other.eta);
        this.timeOfArival = new Instant(other.timeOfArival);
        this.address = other.address;
        this.distance = other.distance;
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
     *
     * @return estimated time til arrival
     */
    public Duration getEta() {
        return eta;
    }

    /**
     * Get the address of the location
     *
     * @return address of the location
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the distance to the location.
     *
     * @return distance to location in meters.
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Set what type of RouteLocation this is
     *
     * @param type type of location
     */
    public void setType(List<String> type) {
        this.type = type;
    }

    /**
     * Set name of the RouteLocation
     *
     * @param name name of the RouteLocation
     */
    public void setName(String name) {
        this.name = name;
    }

    public Instant getTimeOfArival() {
        return timeOfArival;
    }
}
