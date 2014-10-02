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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.beans.PropertyChangeEvent;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.LocationChangedEvent;
import truckerboys.otto.utils.eventhandler.events.NewRouteEvent;
import utils.IView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapView extends SupportMapFragment implements IView, IEventListener{
    private View rootView;
    private GoogleMap googleMap;
    private MapPresenter mapPresenter;
    private CameraPosition cameraPosition = new CameraPosition(new LatLng(0, 0), 16f, 50, 0);

    private TripPlanner tripPlanner;

    public MapView() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);

        googleMap = getMap();
        //MapsInitializer.initialize(getActivity());

        if(googleMap != null){ //Map is not null, modify freely.
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(true);

            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        mapPresenter = new MapPresenter(getActivity(), this.googleMap, this.tripPlanner);
        EventTruck.getInstance().subscribe(this);

        return rootView;
    }

    /**
     * Center and rotates the camera accordingly to longitude, latitude and bearing of location.
     * @param location The location to adjust accordingly to.
     */
    public void centerAtPosition(Location location){
        if(googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(
                            new LatLng(location.getLatitude(), location.getLongitude()),
                            googleMap.getCameraPosition().zoom,
                            cameraPosition.tilt,
                            location.getBearing()
                    )));
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
    public void performEvent(Event event) {
        if(event.isType(LocationChangedEvent.class)){
            centerAtPosition(((LocationChangedEvent) event).getNewPosition());
        }
        if(event.isType(NewRouteEvent.class)){
            NewRouteEvent routeEvent = (NewRouteEvent) event;
            paintRoute(routeEvent.getNewRoute());
        }
    }
}
