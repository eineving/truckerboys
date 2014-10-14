package truckerboys.otto.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.events.GPSUpdateEvent;
import truckerboys.otto.utils.positions.MapLocation;

/**
 * Created by Simon Petersson on 2014-10-03.
 *
 * Class that holds the device current position and fires an LocationChangedEvent when
 * Googles Location API fires that our location has changed.
 */
public class LocationHandler implements GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        LocationListener {

    private static final int LOCATION_REQUEST_INTERVAL_MS = 500;

    private LocationClient locationClient;

    private static MapLocation currentLocation;
    private static boolean connected = false;

    public LocationHandler(Context context){
        locationClient = new LocationClient(context, this, this);
        locationClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Setup the location request with intervals and accuracy.
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL_MS);

        //Request new location updates to this LocationListener.
        locationClient.requestLocationUpdates(locationRequest, this);

        this.currentLocation = new MapLocation(locationClient.getLastLocation());

        //Define that the LocationHandler is connected to the GPS.
        connected = true;
    }

    @Override
    public void onDisconnected() {
        connected = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(isMoreAccurate(location)) {
            EventTruck.getInstance().newEvent(new GPSUpdateEvent(new MapLocation(location), getCurrentLocationAsMapLocation()));
            this.currentLocation = new MapLocation(location);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Checks if a location is more accurate and trushworthy then the last one that was set in
     * the LocationHandler. Firing this when in onLocationChanged will make sure the current
     * saved position is always the most accurate.
     * @param newLocation The location to check against the last one set.
     * @return True if the new location is more accurate.
     */
    private boolean isMoreAccurate(Location newLocation) {
        //TODO Implement method to check if the new location is more accurate than the last one that was set.
        return true;
    }

    public static boolean isConnected() {
        return connected;
    }

    public static MapLocation getCurrentLocationAsMapLocation() {
        return currentLocation;
    }

    public static LatLng getCurrentLocationAsLatLng() {
        return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    }
}
