package truckerboys.otto.utils.positions;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Daniel on 2014-09-18.
 */
public class MapLocation extends Location{
    private String address = "";

    public MapLocation(Location location){
        super(location);
    }

    public MapLocation(LatLng latLng) {
        super("provider"); //wut?
        setLatitude(latLng.latitude);
        setLongitude(latLng.longitude);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
