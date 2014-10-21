package truckerboys.otto.maps;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.swedspot.vil.distraction.DriverDistractionLevel;

import java.util.LinkedList;
import java.util.List;

import truckerboys.otto.R;
import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.GPSUpdateEvent;
import truckerboys.otto.utils.math.Double2;
import truckerboys.otto.utils.positions.MapLocation;
import truckerboys.otto.utils.positions.RouteLocation;
import truckerboys.otto.vehicle.IDistractionListener;
import truckerboys.otto.vehicle.VehicleInterface;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapView extends Fragment implements IEventListener, GoogleMap.OnCameraChangeListener, IDistractionListener {

    private View rootView;
    private MapPresenter presenter;

    // Objects that identify everything that is visible on the map.
    private GoogleMap googleMap;
    private Marker positionMarker;
    private List<Marker> checkpointMarkers = new LinkedList<Marker>();
    private Polyline routePolyline;

    // Button that is clicked when the user wants to start following a route.
    private LinearLayout startRoute;
    private ImageView stopRoute;
    private LinearLayout startRouteDialog;
    private LinearLayout activeRouteDialog;

    // Texts
    private TextView finalDestinationText;
    private TextView finalDestinationETAText;
    private TextView finalDestinationDistText;
    private TextView nextCheckpointText;
    private TextView nextCheckpointETAText;
    private TextView nextCheckpointDistText;

    // Last known distraction level. Used for optimizing (Not having to set new icon every time distractionlevel is changed.)
    private int lastDistractionLevel;

    //region Camera Settings
    public float CAMERA_TILT = 45f;
    public float CAMERA_ZOOM = 16f; //Default to 16f zoom.
    //endregion

    //region Interpolation variables.
    // The frequency of the interpolation.
    public static final int INTERPOLATION_FREQ = 50;
    // The step of the interpolation.
    private int index;
    private LinkedList<Double2> positions = new LinkedList<Double2>();
    private LinkedList<Float> bearings = new LinkedList<Float>();
    //endregion

    @Override
    public void onDestroyView() {

        FragmentManager fm = getFragmentManager();

        Fragment xmlFragment = fm.findFragmentById(R.id.map);
        if (xmlFragment != null) {
            fm.beginTransaction().remove(xmlFragment).commit();
        }

        super.onDestroyView();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        // Subscribe to EventTruck, since we want to listen for GPS Updates.
        EventTruck.getInstance().subscribe(this);
        // Subscribe to DistractionLevelChanged, since we want to change marker when having a high distraction level.
        VehicleInterface.subscribeToDistractionChange(this);

        //region Initiate GoogleMap
        // Get GoogleMap from Google.
        googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (googleMap != null) /* If we were successful in receiving a GoogleMap */ {
            // Set all gestures disabled, truckdriver shouldn't be able to move the map.
            setAllGestures(true);
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            googleMap.setOnCameraChangeListener(this);

            //TODO Read from settings if the user wants Hybrid or Normal map type.
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Add the positionmarker to the GoogleMap, making it possible to move it later on.
            positionMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.position_arrow_blue)).position(new LatLng(0, 0)).flat(true));

            // Add the empty polyline to the GoogleMap, making it possible to change the line later on.
            routePolyline = googleMap.addPolyline(new PolylineOptions().color(Color.rgb(1, 87, 155)).width(20));
        }
        //endregion

        startRouteDialog = (LinearLayout) rootView.findViewById(R.id.startRoute_dialog);
        activeRouteDialog = (LinearLayout) rootView.findViewById(R.id.activeRoute_dialog);
        startRoute = (LinearLayout) rootView.findViewById(R.id.startRoute_button);
        stopRoute = (ImageView) rootView.findViewById(R.id.stopRoute_button);

        //Initialize texts.
        finalDestinationText = (TextView) rootView.findViewById(R.id.finalDestination_text);
        finalDestinationETAText = (TextView) rootView.findViewById(R.id.finalDestinationETA_text);
        finalDestinationDistText = (TextView) rootView.findViewById(R.id.finalDestinationDist_text);
        nextCheckpointText = (TextView) rootView.findViewById(R.id.nextStop_text);
        nextCheckpointETAText = (TextView) rootView.findViewById(R.id.nextStopETA_text);
        nextCheckpointDistText = (TextView) rootView.findViewById(R.id.nextStopDistance_text);


        startRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.startFollowRoute();
                showStartRouteDialog(false);
                showActiveRouteDialog(true);
                setAllGestures(false);
            }
        });

        // Make the follow route button responsive.
        //region StartRoute - OnTouchListener
        startRoute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    startRoute.setBackgroundColor(getResources().getColor(R.color.textLightest));
                } else {
                    startRoute.setBackgroundColor(Color.TRANSPARENT);
                }

                return false;
            }
        });
        //endregion

        stopRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActiveRouteDialog(false);
                presenter.stopFollowRoute();
                setAllGestures(true);

                //If there currently is a route drawn on the map.
                if(routePolyline.getPoints().size() > 0){
                    // Show Start route dialog again.
                    showStartRouteDialog(true);
                }
            }
        });

        return rootView;
    }

    /**
     * Helper method to move the camera across the map.
     *
     * @param animate  True if you want the camera to be animated across the map. False if it should just move instantly.
     * @param location The location to set the camera to.
     * @param bearing  The bearing to set the camera to.
     */
    public void moveCamera(final boolean animate, final LatLng location, final float bearing) {
        if (googleMap != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (animate) {
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(location, CAMERA_ZOOM, CAMERA_TILT, bearing)));
                    } else {
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(location, CAMERA_ZOOM, CAMERA_TILT, bearing)));
                    }
                }
            });

        }
    }

    /**
     * Helper method to move the camera across the map.
     *
     * @param animate  True if you want the camera to be animated across the map. False if it should just move instantly.
     * @param location The location to set the camera to.
     * @param bearing  The bearing to set the camera to.
     */
    public void moveCamera(final boolean animate, final LatLng location, final float zoom, final float bearing, final int durationMs) {
        if (googleMap != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (animate) {
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(location, zoom, CAMERA_TILT, bearing)), durationMs, null);
                    } else {
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(location, zoom, CAMERA_TILT, bearing)));
                    }
                }
            });

        }
    }

    /**
     * Helper method to move the camera across the map.
     *
     * @param animate  True if you want the camera to be animated across the map. False if it should just move instantly.
     * @param location The location to set the camera to.
     * @param bearing  The bearing to set the camera to.
     */
    public void moveCamera(CameraUpdate cameraUpdate, GoogleMap.CancelableCallback cancelableCallback) {
        if (googleMap != null) {
            googleMap.animateCamera(cameraUpdate, cancelableCallback);
        }
    }

    /**
     * Helper method to move the camera across the map.
     *
     * @param animate True if you want the camera to be animated across the map. False if it should just move instantly.
     * @param bounds The bounds to zoom according to.
     */
    public void moveCamera(final boolean animate, final LatLngBounds bounds) {
        if (googleMap != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (animate) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
                    } else {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
                    }
                }
            });

        }
    }

    /**
     * Function that will set the current markers on the map to a List of specified markers.
     *
     * @param routeLocations The specified list of markers to add to the map.
     */
    public void setMarkers(final List<RouteLocation> routeLocations) {
        if (googleMap != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Clear the old markers off the map.
                    for (Marker marker : checkpointMarkers) {
                        marker.remove();
                    }
                    checkpointMarkers.clear();

                    // Add the new markers to the map.
                    for (RouteLocation location : routeLocations) {
                        checkpointMarkers.add(googleMap.addMarker(new MarkerOptions()
                                        .position(location.getLatLng())
                                        .title("Adress: " + location.getAddress() + ", ETA: " + location.getEta().getStandardMinutes())
                        ));
                    }
                }
            });

        }
    }

    /**
     * Updates the marker for our current position, will also interpolate the position making it
     * move smoothly across the map between each GPS Update.
     *
     * @param followMarker True if camera should follow marker.
     * @requires to be updated once every 1/INTERPOLATION_FREQ second to function properly.
     * @requires setupPositionMarker() has been called.
     */
    public void updatePositionMarker(boolean followMarker) {
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

                float deltaRot = rot2 - rot1;
                float step = (float)Math.min(deltaRot, 2*Math.PI - deltaRot) / INTERPOLATION_FREQ;

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
            }

            if(followMarker) {
                moveCamera(false, positionMarker.getPosition(), positionMarker.getRotation());
            }
        }
    }

    @Override
    public void performEvent(Event event) {
        //region GPSUpdateEvent
        if (event.isType(GPSUpdateEvent.class)) {
            final MapLocation newLocation = ((GPSUpdateEvent) event).getNewPosition();

            // If we just initiated the map, we should move to first received GPS position.
            // For better user experience. (THIS ONLY HAPPENS ONCE)
            if (positions.size() == 0) {
                moveCamera(true, new LatLng(newLocation.getLatitude(), newLocation.getLongitude()), newLocation.getBearing());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        positionMarker.setPosition(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
                        positionMarker.setRotation(newLocation.getBearing());
                    }
                });

            }

            // Everytime we get a GPS Position Update we add that position and the bearing to a list
            // Making it possible to interpolate theese values.
            positions.add(new Double2(newLocation.getLatitude(), newLocation.getLongitude()));
            bearings.add(newLocation.getBearing());
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
    public void setRoute(final Route route) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Add all new steps.
                routePolyline.setPoints(route.getDetailedPolyline());
            }
        });

    }

    public void setPresenter(MapPresenter presenter) {
        this.presenter = presenter;
    }

    private void setAllGestures(final boolean value){
        if (googleMap != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    googleMap.getUiSettings().setAllGesturesEnabled(value);
                }
            });

        }
    }

    public void showStartRouteDialog(final boolean visibility){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startRouteDialog.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
            }
        });

    }

    public void showActiveRouteDialog(final boolean visibility){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activeRouteDialog.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);

                //If we have an active route, we don't want the user to be able to move the camera.
                setAllGestures(!visibility);
            }
        });

    }

    @Override
    public void distractionLevelChanged(final DriverDistractionLevel driverDistractionLevel) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(driverDistractionLevel.getLevel() >= 1 && lastDistractionLevel < 1) /* High distraction level */{
                    positionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.position_arrow_blue));
                    System.out.println("changed Distraction Level: " + driverDistractionLevel.getLevel());
                } else if(driverDistractionLevel.getLevel() < 1 && lastDistractionLevel >= 1) /* Low distraction level */ {
                    positionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.position_arrow_red));
                    System.out.println("changed Distraction Level: " + driverDistractionLevel.getLevel() + " woop");
                }
            }
        });
    }

    public void setFinalDestinationText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finalDestinationText.setText(text);
            }
        });

    }

    public void setFinalDestinationETAText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finalDestinationETAText.setText(text);
            }
        });

    }

    public void setFinalDestinationDistText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finalDestinationDistText.setText(text);
            }
        });

    }

    public void setNextCheckpointText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nextCheckpointText.setText(text);
            }
        });

    }

    public void setNextCheckpointETAText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nextCheckpointETAText.setText(text);
            }
        });
    }

    public void setNextCheckpointDistText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nextCheckpointDistText.setText(text);
            }
        });
    }
}