package truckerboys.otto.maps;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;
import utils.IView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapView extends SupportMapFragment implements IView, PropertyChangeListener{
    private View rootView;
    private GoogleMap googleMap;
    private MapPresenter mapPresenter;

    private TripPlanner tripPlanner;

    public MapView() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);

        googleMap = getMap();
        MapsInitializer.initialize(getActivity());

        if(googleMap != null){ //Map is not null, modify freely.
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.zoomBy(15));
        }

        mapPresenter = new MapPresenter(getActivity(), this.googleMap, this.tripPlanner);
        mapPresenter.addListenerToModel(this);

        return rootView;
    }

    /**
     * Centers the map at a position in the world.
     * @param latitude Latitude to center the map at.
     * @param longitude Longitude to center the map at.
     */
    public void centerAtPosition(double latitude, double longitude){
        if(googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        }
    }

    public void paintRoute(Route route){
        PolylineOptions polylineOptions = new PolylineOptions().addAll(route.getOverviewPolyline());
        googleMap.addPolyline(polylineOptions);
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public String getName() {
        return "Map";
    }

    public TripPlanner getTripPlanner() {
        return tripPlanner;
    }

    public void setTripPlanner(TripPlanner tripPlanner) {
        this.tripPlanner = tripPlanner;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("locationUpdate")){
            Location newLocation = (Location) propertyChangeEvent.getNewValue();
            centerAtPosition(newLocation.getLatitude(), newLocation.getLongitude());
        }
        else if (propertyChangeEvent.getPropertyName().equals("newRoute")) {
            paintRoute((Route)propertyChangeEvent.getNewValue());
        }
    }
}
