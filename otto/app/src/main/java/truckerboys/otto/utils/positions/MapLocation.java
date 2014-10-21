package truckerboys.otto.utils.positions;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Duration;

/**
 * Created by Daniel on 2014-09-18.
 */
public class MapLocation extends Location{
    public MapLocation(Location location){
        super(location);
    }

    public MapLocation(LatLng latLng) {
        super("provider"); //wut?
        setLatitude(latLng.latitude);
        setLongitude(latLng.longitude);
    }

    public MapLocation(MapLocation other) {
        super(other);
    }


    /**
     * Compares two coordinates and checks if they are within 5 meters of eachother.
     *
     * @param rhs The location to compare with.
     * @return True if distanceTo rhs is less than 5 meters.
     */
    public boolean equalCoordinates(MapLocation rhs) {
        return distanceTo(rhs) < 10;
    }
    
    public LatLng getLatLng(){
        return new LatLng(getLatitude(), getLongitude());
    }
}
