package truckerboys.otto.maps;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;

import truckerboys.otto.R;
import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.GPSUpdateEvent;
import truckerboys.otto.utils.eventhandler.events.ChangedRouteEvent;
import truckerboys.otto.utils.math.Double2;
import truckerboys.otto.utils.positions.MapLocation;
import utils.IView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapView extends SupportMapFragment implements IView, IEventListener, GoogleMap.OnCameraChangeListener{
    private View rootView;
    private GoogleMap googleMap;
    private Marker positionMarker;
    private Polyline currentRoutePolyline;

    private MapPresenter mapPresenter;
    private TripPlanner tripPlanner;

    //Default to detailed route, since zoom default is 16f.
    private RouteDetail currentDetail = RouteDetail.DETAILED;
    private static final float DETAILED_ZOOM_ABOVE = 10;


    private int gps_freq = 500; // The frequency of gps updates.
    private int freq = 25; //The frequency of the interpolation.
    private int index; //The step of the interpolation.

    private LinkedList<Double2> positions = new LinkedList<Double2>();
    private LinkedList<Float> bearings = new LinkedList<Float>();

    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable updatePos = new Runnable() {
        public void run() {
            updatePositionMarker();
        }
    };

    public MapView() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        EventTruck.getInstance().subscribe(this);

        //Get map from Google.
        googleMap = getMap();

        //If we could receive map from Google, modify freely to our needs.
        if(googleMap != null) {
            //Set all gestures disabled, truckdriver shouldn't be able to move the map.
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            //TODO Read from settings if the user wants Hybrid or Normal map type.
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setOnCameraChangeListener(this);

            //Set current position and default zoom, tilt and bearing.
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(0,0), 16f, 50, 0)));

            //Initialize position marker to current position and set the arrow icon.
            positionMarker = googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.position_arrow))
                    .position(new LatLng(0,0))
                    .flat(true));

            currentRoutePolyline = googleMap.addPolyline(new PolylineOptions().color(Color.BLUE));
        }

        updatePos.run();
        mapPresenter = new MapPresenter(this.tripPlanner, googleMap);
        return rootView;
    }

    /**
     * Adjust the camera position and bearing to match the location provided.
     * @param location The location to adjust adjust accordingly to.
     */
    private void updateCamera(LatLng location, float bearing){
        if(googleMap != null) { //Make sure google map was successfully retrieved from MapFragment.
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(
                            location,
                            googleMap.getCameraPosition().zoom,
                            googleMap.getCameraPosition().tilt,
                            bearing
                    )));
        }
    }

    /**
     * Updates the marker for our current position.
     * @requires to be updated once every 1/freq second to function properly.
     * @requires setupPositionMarker() has been called.
     */

    private void updatePositionMarker(){
        if(positionMarker != null) { //Make sure we initiated posMaker in onCreateView


            //We actually just need 2 bearings since we use linear interpolations but we require 3 so
            //that the bearings and positions are synced.
            if(positions.size() >= 3){

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


                positionMarker.setPosition(new LatLng(smooth.getX(), smooth.getY()));


                //Linear intepolation of the bearing.
                float rot1 = bearings.get(0);
                float rot2 = bearings.get(2);

                float step = (rot2 - rot1) / freq;

                float smoothBearing = rot1 + step * index;

                positionMarker.setRotation(smoothBearing);





                index++;
                index = index % freq;

                if(index == 0){
                    //Since we interpolate over 3 values the first 2 has been used when we reach the third.
                    positions.removeFirst();
                    positions.removeFirst();

                    bearings.removeFirst();
                    bearings.removeFirst();
                }

                updateCamera(positionMarker.getPosition(), positionMarker.getRotation());
            }
        }

        updateHandler.postDelayed(updatePos,gps_freq/freq);
    }

    /*
     * Paint the route provided to this views Google Map.
     * @param route The route to paint.
     */
    private void updatePolylineByZoom(Route route, float zoomLevel) {
        //Check what detail level we want based on Zoom amount.
        RouteDetail detail = (zoomLevel > DETAILED_ZOOM_ABOVE ? RouteDetail.DETAILED : RouteDetail.OVERVIEW);

        if (detail == RouteDetail.DETAILED) {
            currentRoutePolyline.setPoints(route.getDetailedPolyline());
            currentDetail = RouteDetail.DETAILED;
        } else {
            currentRoutePolyline.setPoints(route.getOverviewPolyline());
            currentDetail = RouteDetail.OVERVIEW;
        }
    }

    @Override
    public void performEvent(Event event) {
        if (event.isType(GPSUpdateEvent.class)) {
            MapLocation newLocation = ((GPSUpdateEvent) event).getNewPosition();
            positions.add(new Double2(newLocation.getLatitude(), newLocation.getLongitude()));
            bearings.add(newLocation.getBearing());
        }
        if(event.isType(ChangedRouteEvent.class)) {
            updatePolylineByZoom(((ChangedRouteEvent) event).getRoute(), googleMap.getCameraPosition().zoom);
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

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if(mapPresenter.getOriginalRoute() != null) {
            updatePolylineByZoom(mapPresenter.getOriginalRoute(), cameraPosition.zoom);
        }
    }
}

enum RouteDetail {
    OVERVIEW, DETAILED
}
