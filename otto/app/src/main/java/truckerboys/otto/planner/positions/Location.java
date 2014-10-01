package truckerboys.otto.planner.positions;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Daniel on 2014-09-18.
 */
public class Location {
    private LatLng latLng;
    private String address = "";

    public Location(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
