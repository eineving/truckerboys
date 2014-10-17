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

    /**
     * Checks if the locations have the same coordinates.
     * @param rhs Map location to compare with.
     * @return true if the locations have the same coordinates.
     */
    public boolean equalCoordinates(MapLocation rhs) {
        return (rhs.getLatitude() == getLatitude() && rhs.getLongitude() == getLongitude());

    }
    
    public LatLng getLatLng(){
        return new LatLng(getLatitude(), getLongitude());
    }
}
