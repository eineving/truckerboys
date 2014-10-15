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
import java.util.List;

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
public class MapView extends SupportMapFragment implements IEventListener, GoogleMap.OnCameraChangeListener {

    private View rootView;

    // Objects that identify everything that is visible on the map.
    private GoogleMap googleMap;
    private Marker positionMarker;
    private List<Marker> checkpointMarkers = new LinkedList<Marker>();
    private Polyline routePolyline;

    // Currently visible (drawn) legs of the polyline.
    private List<LatLng> visibleRouteSteps = new LinkedList<LatLng>();

    //region Camera Settings
    private float CAMERA_TILT = 45f;
    private float CAMERA_ZOOM = 16f; //Default to 16f zoom.
    //endregion

    //region Interpolation variables.
    // The frequency of the interpolation.
    private static final int INTERPOLATION_FREQ = 25;
    // The step of the interpolation.
    private int index;
    private LinkedList<Double2> positions = new LinkedList<Double2>();
    private LinkedList<Float> bearings = new LinkedList<Float>();
    //endregion

    //region Runnables
    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable updatePos = new Runnable() {
        public void run() {
            interpolateMarker();
        }
    };

    private Handler drawPolylineHandler = new Handler();
    private Runnable drawPolyline = new Runnable() {
        @Override
        public void run() {
            routePolyline.setPoints(visibleRouteSteps);
        }
    };
    //endregion

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);

        // Subscribe to EventTruck, since we want to listen for GPS Updates.
        EventTruck.getInstance().subscribe(this);

        // Get GoogleMap from Google.
        googleMap = getMap();
        if (googleMap != null) /* If we were successfull in receiving a GoogleMap */ {
            // Set all gestures disabled, truckdriver shouldn't be able to move the map.
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            googleMap.setOnCameraChangeListener(this);

            //TODO Read from settings if the user wants Hybrid or Normal map type.
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Add the positionmarker to the GoogleMap, making it possible to move it later on.
            positionMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.position_arrow)).position(new LatLng(0, 0)).flat(true));

            // Add the empty polyline to the GoogleMap, making it possible to change the line later on.
            routePolyline = googleMap.addPolyline(new PolylineOptions().color(Color.rgb(1, 87, 155)).width(20));
        }

        updatePos.run();

        return rootView;
    }

    /**
     * Helper method to move the camera across the map.
     *
     * @param animate  True if you want the camera to be animated across the map. False if it should just move instantly.
     * @param location The location to set the camera to.
     * @param bearing  The bearing to set the camera to.
     */
    public void moveCamera(boolean animate, LatLng location, float bearing) {
        if (googleMap != null) {
            if (animate) {
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(location, CAMERA_ZOOM, CAMERA_TILT, bearing)));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(location, CAMERA_ZOOM, CAMERA_TILT, bearing)));
            }
        }
    }

    /**
     * Call this method when the polyline needs to be updated. Should not be updated to often, since
     * it will decrease performance significantly.
     */
    public void drawPolyline() {
        //Make the drawing of the polyline happen on a different thread.
        drawPolylineHandler.post(drawPolyline);
    }

    /**
     * Function that will set the current markers on the map to a List of specified markers.
     *
     * @param mapLocations The specified list of markers to add to the map.
     */
    public void setMarkers(List<MapLocation> mapLocations) {
        if (googleMap != null) {
            // Clear the old markers off the map.
            for (Marker marker : checkpointMarkers) {
                marker.remove();
            }
            checkpointMarkers.clear();

            // Add the new markers to the map.
            for (MapLocation location : mapLocations) {
                checkpointMarkers.add(googleMap.addMarker(new MarkerOptions().position(location.getLatLng())));
            }
        }
    }

    /**
     * Updates the marker for our current position, will also interpolate the position making it
     * move smoothly across the map between each GPS Update.
     *
     * @requires to be updated once every 1/INTERPOLATION_FREQ second to function properly.
     * @requires setupPositionMarker() has been called.
     */
    public void interpolateMarker() {
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
                moveCamera(false, positionMarker.getPosition(), positionMarker.getRotation());
            }
        }

        updateHandler.postDelayed(updatePos, LocationHandler.LOCATION_REQUEST_INTERVAL_MS / INTERPOLATION_FREQ);
    }

    @Override
    public void performEvent(Event event) {
        //region GPSUpdateEvent
        if (event.isType(GPSUpdateEvent.class)) {
            MapLocation newLocation = ((GPSUpdateEvent) event).getNewPosition();

            // If we just initiated the map, we should move to first received GPS position.
            // For better user experience. (THIS ONLY HAPPENS ONCE)
            if (positions.size() == 0) {
                moveCamera(true, new LatLng(newLocation.getLatitude(), newLocation.getLongitude()), newLocation.getBearing());
                positionMarker.setPosition(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
                positionMarker.setRotation(newLocation.getBearing());
            }


            // Everytime we get a GPS Position Update we add that position and the bearing to a list
            // Making it possible to interpolate theese values.
            positions.add(new Double2(newLocation.getLatitude(), newLocation.getLongitude()));
            bearings.add(newLocation.getBearing());

            // TODO Implement logics if you go outside route.

            // TODO Implement logics if you go beyond a checkpoint.
        }
        //endregion
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        CAMERA_ZOOM = cameraPosition.zoom;
    }

    /**
     * Set the new route to draw.
     *
     * @param route The new route do draw on the map.
     */
    public void setRoute(Route route) {
        //Clear old route
        visibleRouteSteps.clear();

        //Add all new steps.
        visibleRouteSteps.addAll(route.getDetailedPolyline());
    }
}
