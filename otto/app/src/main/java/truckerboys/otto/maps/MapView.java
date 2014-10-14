package truckerboys.otto.maps;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import truckerboys.otto.utils.LocationHandler;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.GPSUpdateEvent;
import truckerboys.otto.utils.math.Double2;
import truckerboys.otto.utils.positions.MapLocation;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapView extends SupportMapFragment implements  IEventListener {
    private View rootView;
    private GoogleMap googleMap;
    private Marker positionMarker;
    private Polyline routePolyline;

    private static final float DETAILED_ZOOM_ABOVE = 10;

    private static final int GPS_FREQ = 500; // The frequency of gps updates.
    private static final int INTERPOLATION_FREQ = 25; //The frequency of the interpolation.
    private int index; //The step of the interpolation.

    private LinkedList<Double2> positions = new LinkedList<Double2>();
    private LinkedList<Float> bearings = new LinkedList<Float>();

    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable updatePos = new Runnable() {
        public void run() {
            updatePositionMarker();
        }
    };

    private GoogleMap.OnCameraChangeListener onCameraChangeListener;

    public void setOnCameraChangeListener(GoogleMap.OnCameraChangeListener onCameraChangeListener) {
        this.onCameraChangeListener = onCameraChangeListener;
    }

    public MapView() { }

    //TODO What happens if googleMap isn't initiated correctly. (Check all methods making sure that the app doesn't crash.)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        EventTruck.getInstance().subscribe(this);

        //Get map from Google.
        googleMap = getMap();

        //If we could receive map from Google, modify freely to our needs.
        if (googleMap != null) {
            googleMap.setOnCameraChangeListener(onCameraChangeListener);

            //Set all gestures disabled, truckdriver shouldn't be able to move the map.
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            //TODO Read from settings if the user wants Hybrid or Normal map type.
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //Set current position and default zoom, tilt and bearing.
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(0, 0), 16f, 50, 0)));

            //Initialize position marker to current position and set the arrow icon.
            positionMarker = googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.position_arrow))
                    .position(new LatLng(0, 0))
                    .flat(true));

            routePolyline = googleMap.addPolyline(new PolylineOptions().color(Color.BLUE));
        }

        updatePos.run();
        return rootView;
    }

    /**
     * Adjust the GoogleMap Camera to the provided LatLng position and bearing.
     *
     * @param location The location to move the Camera to.
     * @param bearing The bearing to adjust the Camera to.
     */
    private void adjustCamera(LatLng location, float bearing) {
        if (googleMap != null) { //Make sure google map was successfully retrieved from MapFragment.
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
     * Updates the marker for our current position, will also interpolate the position making it
     * move smoothly across the map between each GPS Update.
     *
     * @requires to be updated once every 1/INTERPOLATION_FREQ second to function properly.
     * @requires setupPositionMarker() has been called.
     */
    private void updatePositionMarker() {
        if (positionMarker != null) { //Make sure we initiated posMaker in onCreateView

            //We actually just need 2 bearings since we use linear interpolations but we require 3 so
            //that the bearings and positions are synced.
            if (positions.size() >= 3) {

                Double2 a = positions.get(0);
                Double2 b = positions.get(1);
                Double2 c = positions.get(2);

                Double2 vecA = b.sub(a).div(INTERPOLATION_FREQ);
                Double2 vecB = c.sub(b).div(INTERPOLATION_FREQ);

                //The linear interpolations.
                Double2 ab = a.add(vecA.mul(index));
                Double2 bc = b.add(vecB.mul(index));

                //The quadratic-interpolation
                Double2 smooth = ab.add(bc.sub(ab).div(INTERPOLATION_FREQ).mul(index));

                positionMarker.setPosition(new LatLng(smooth.getX(), smooth.getY()));

                //Linear intepolation of the bearing.
                float rot1 = bearings.get(0);
                float rot2 = bearings.get(2);

                float step = (rot2 - rot1) / INTERPOLATION_FREQ;

                float smoothBearing = rot1 + step * index;

                positionMarker.setRotation(smoothBearing);

                index++;
                index = index % INTERPOLATION_FREQ;

                if (index == 0) {
                    //Since we interpolate over 3 values the first 2 has been used when we reach the third.
                    positions.removeFirst();
                    positions.removeFirst();

                    bearings.removeFirst();
                    bearings.removeFirst();
                }

                //Move the camera to marker position
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition(
                                positionMarker.getPosition(),
                                googleMap.getCameraPosition().zoom,
                                googleMap.getCameraPosition().tilt,
                                positionMarker.getRotation()
                        )));
            }
        }

        updateHandler.postDelayed(updatePos, GPS_FREQ / INTERPOLATION_FREQ);
    }

    /*
     * Draws the polyline from the specified route. Depending on what zoomLevel you specify it
     * will draw an overview of the route or a detailed version.
     * @param route The route to base the polyline on.
     */
    private void updatePolyline(Route route, float zoomLevel) {
        //Check what detail level we want based on Zoom amount.
        RouteDetail detail = (zoomLevel > DETAILED_ZOOM_ABOVE ? RouteDetail.DETAILED : RouteDetail.OVERVIEW);

        if (detail == RouteDetail.DETAILED) {
            routePolyline.setPoints(route.getDetailedPolyline());
        } else {
            routePolyline.setPoints(route.getOverviewPolyline());
        }
    }

    /**
     * Updates the MapView according to the specified Route. CameraPosition will remain the same
     * as it did before.
     * @param route The new Route to draw on the Map.
     */
    public void updateCamera(Route route){
        updatePolyline(route, googleMap.getCameraPosition().zoom);
    }

    /**
     * Updates the MapView according to the specified Route and CameraPosition.
     * @param route The new Route to draw on the Map.
     * @param cameraPosition The CameraPosition to use when displaying the Map.
     */
    public void updateCamera(Route route, CameraPosition cameraPosition) {
        updatePolyline(route, cameraPosition.zoom);
        adjustCamera(LocationHandler.getCurrentLocationAsLatLng(), cameraPosition.bearing);
    }

    @Override
    public void performEvent(Event event) {
        if (event.isType(GPSUpdateEvent.class)) {
            MapLocation newLocation = ((GPSUpdateEvent) event).getNewPosition();
            positions.add(new Double2(newLocation.getLatitude(), newLocation.getLongitude()));
            bearings.add(newLocation.getBearing());
        }
    }
}

enum RouteDetail {
    OVERVIEW, DETAILED
}
