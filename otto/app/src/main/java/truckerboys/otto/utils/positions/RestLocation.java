package truckerboys.otto.utils.positions;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Class representing a rest location
 */
public class RestLocation extends MapLocation {
    private List<String> type;
    private String name;

    /**
     * Create a new rest location
     * @param latLng coordinates
     * @param name name of the rest location
     * @param type type of rest location
     */
    public RestLocation(LatLng latLng, String name, List<String> type) {
        super(latLng);
        this.name=name;
        this.type=type;
    }

    /**
     * What type of rest location this is
     * @return type of rest location
     */
    public List<String> getType() {
        return type;
    }

    /**
     * The name of the rest location
     * @return name of the rest location
     */
    public String getName() {
        return name;
    }
}
