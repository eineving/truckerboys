package truckerboys.otto.maps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;

import truckerboys.otto.R;
import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.LocationChangedEvent;
import truckerboys.otto.utils.eventhandler.events.NewRouteEvent;
import truckerboys.otto.utils.math.Double2;
import truckerboys.otto.utils.positions.MapLocation;
import utils.IView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapView extends SupportMapFragment implements IView, IEventListener{
    private View rootView;
    private GoogleMap googleMap;
    private MapPresenter mapPresenter;

    private Marker posMarker;

    private TripPlanner tripPlanner;

    private int freq = 1; //The frequency of the updates;
    private int index; //The step of the interpolation.

    private LinkedList<Double2> positions = new LinkedList<Double2>();
    private LinkedList<Float> bearings = new LinkedList<Float>();

    public MapView() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        EventTruck.getInstance().subscribe(this);

        //Get map from Google.
        googleMap = getMap();

        //If we could receive map from Google, modify freely to our needs.
        if(googleMap != null){
            //Set all gestures disabled, truckdriver shouldn't be able to move the map.
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            //TODO Read from settings if the user wants Hybrid or Normal map type.
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            setupPositionMarker();
        }

        mapPresenter = new MapPresenter(this.tripPlanner);
        return rootView;
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
                            googleMap.getCameraPosition().tilt,
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

    /**
     * Setups the marker for our current position.
     * @requires LocationHandler is connected.
     */
    private void setupPositionMarker(){
        LatLng currentLocation = new LatLng(0,0);

        //Set current position and default zoom, tilt and bearing.
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentLocation, 16f, 50, 0)));

        posMarker = googleMap.addMarker(new MarkerOptions()
                //TODO Create a better looking current position arrow.
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.position_arrow))
                .position(currentLocation)
                .flat(true));
    }

    /**
     * Updates the marker for our current position.
     * @requires to be updated once every 1/freq second to function properly.
     * @requires setupPositionMarker() has been called.
     */
    private void updatePositionMarker(){
        if(posMarker != null) { //Make sure we initiated posMaker in onCreateView


            //We actually just need 2 bearings since we use linear interpolations but we require 3 so
            //that the bearings and positions are synced.
            if(positions.size() >= 3  && bearings.size() >= 3){

                Double2 a = positions.get(0);
                Double2 b = positions.get(1);
                Double2 c = positions.get(2);

                Double2 vecA = b.sub(a).div(freq);
                Double2 vecB = c.sub(b).div(freq);

                //The linear interpolations.
                Double2 ab = a.add(vecA.mul(index));
                Double2 bc = b.add(vecB.mul(index));

                //The quadratic-interpolation
                Double2 smooth = ab.add(bc.sub(ab).div(freq).mul(index));

                posMarker.setPosition(new LatLng(smooth.getY(), smooth.getX()));


                //Linear intepolation of the bearing.
                float rot1 = bearings.get(0);
                float rot2 = bearings.get(0);

                float step = (rot2 - rot1) / freq;

                float smoothBearing = rot1 + step * index;


                posMarker.setRotation(smoothBearing);

                index++;
                index = index % freq;

                if(index == 0){
                    positions.removeFirst();
                    bearings.removeFirst();

                }
            }
        }
    }

    @Override
    public void performEvent(Event event) {
        if (event.isType(LocationChangedEvent.class)) {
            MapLocation newLocation = ((LocationChangedEvent) event).getNewPosition();
            adjustCamera(newLocation);
            positions.add(new Double2(newLocation.getLongitude(), newLocation.getLongitude()));
            bearings.add(newLocation.getBearing());
            updatePositionMarker();
        }
        if (event.isType(NewRouteEvent.class)) {
            NewRouteEvent routeEvent = (NewRouteEvent) event;
            paintRoute(routeEvent.getNewRoute());
        }
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
