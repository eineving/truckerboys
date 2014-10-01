package truckerboys.otto.maps;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapModel implements GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        LocationListener {
    private GoogleMap googleMap;
    private LocationClient locationClient;
    private TripPlanner tripPlanner;
    private Route currentRoute;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private Location truckPosition;

    public MapModel(Context context, GoogleMap googleMap, TripPlanner tripPlanner) {
        this.tripPlanner = tripPlanner;
        this.googleMap = googleMap;

        locationClient = new LocationClient(context, this, this);
        locationClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500);
        locationClient.requestLocationUpdates(locationRequest, this);

        //TODO Add a timer listener that does this every X second, if outside route. Calc new.
        this.currentRoute = tripPlanner.getNewRouteTo(null);
        //TODO Send old/new route instead of null
        propertyChangeSupport.firePropertyChange("newRoute", null, this.currentRoute);
    }

    @Override
    public void onDisconnected() {
        //GPS Signal was disconnected
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Connection to the GPS failed.
    }

    @Override
    public void onLocationChanged(Location location) {
        propertyChangeSupport.firePropertyChange("locationUpdate", getTruckPosition(), location);
        setTruckPosition(location);
    }

    public Location getTruckPosition() {
        return truckPosition;
    }

    public void setTruckPosition(Location truckPosition) {
        this.truckPosition = truckPosition;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener){
        this.propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener){
        this.propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }
}
