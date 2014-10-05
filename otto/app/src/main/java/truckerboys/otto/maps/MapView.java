package truckerboys.otto.maps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.LocationChangedEvent;
import truckerboys.otto.utils.eventhandler.events.NewRouteEvent;
import truckerboys.otto.utils.positions.MapLocation;
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
        EventTruck.getInstance().subscribe(this);

        //Get map from Google.
        googleMap = getMap();

        //TODO Optimize this so that it doens't lagg every time we need to reload the map tab.
        //If we could receive map from Google, modify freely to our needs.
        if(googleMap != null){

            //Set all gestures disabled, truckdriver shouldn't be able to move the map.
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            //TODO Read from settings if the user wants Hybrid or Normal map type.
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //TODO Remove this and draw a custom marker on map at current location instead.
            googleMap.setMyLocationEnabled(true);

            //Set default LatLng (0, 0), zoom, tilt and bearing.
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        mapPresenter = new MapPresenter(this.tripPlanner);

        return rootView;
    }

    @Override
    public void performEvent(Event event) {
        if(event.isType(LocationChangedEvent.class)){
            adjustCamera(((LocationChangedEvent) event).getNewPosition());
        }
        if(event.isType(NewRouteEvent.class)){
            NewRouteEvent routeEvent = (NewRouteEvent) event;
            paintRoute(routeEvent.getNewRoute());
        }
    }

    /**
     * Adjust the camera position and bearing to match the location provided.
     * @param location The location to adjust adjust accordingly to.
     */
    private void adjustCamera(MapLocation location){
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

    /**
     * Paint the route provided to this views Google Map.
     * @param route The route to paint.
     */
    private void paintRoute(Route route){
        PolylineOptions polylineOptions = new PolylineOptions().addAll(route.getDetailedPolyline());
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

    public void setTripPlanner(TripPlanner tripPlanner) {
        this.tripPlanner = tripPlanner;
    }
}
