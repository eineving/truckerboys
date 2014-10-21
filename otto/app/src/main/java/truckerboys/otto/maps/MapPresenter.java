package truckerboys.otto.maps;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.LocationHandler;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.ChangedRouteEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.IView;
import truckerboys.otto.utils.eventhandler.events.FollowMarkerEvent;
import truckerboys.otto.utils.eventhandler.events.RouteRequestEvent;
import truckerboys.otto.utils.positions.RouteLocation;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 *
 * Handles communication between MapView and MapModel.
 */
public class MapPresenter implements IEventListener, IView {
    private MapModel mapModel;
    private MapView mapView;

    private boolean followMarker;

    //region Runnables
    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable updatePos = new Runnable() {
        public void run() {
            if(followMarker) {
                mapView.updatePositionMarker(true);
            } else {
                mapView.updatePositionMarker(false);
            }
            updateHandler.postDelayed(updatePos, (LocationHandler.LOCATION_REQUEST_INTERVAL_MS  * 2)/ MapView.INTERPOLATION_FREQ);
        }
    };
    //endregion

    public MapPresenter(TripPlanner tripPlanner){
        this.mapView = new MapView();
        this.mapModel = new MapModel(tripPlanner);
        EventTruck.getInstance().subscribe(this);
        updatePos.run();
    }

    /**
     * Specifies that the camera should follow the marker, if it should follow the marker it zooms in
     * on the marker and then starts following it.
     *
     * @param follow True if camera should follow the marker.
     */
    private void setFollowMarker(final boolean follow){
        if(follow) {
            mapView.moveCamera(
                    CameraUpdateFactory.newCameraPosition(new CameraPosition(
                            LocationHandler.getCurrentLocationAsLatLng(),
                            18f,
                            mapView.CAMERA_TILT,
                            LocationHandler.getCurrentLocationAsMapLocation().getBearing())),
                    new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            //Make the camera follow the marker when we finished zooming in to marker.
                            followMarker = true;
                        }

                        @Override
                        public void onCancel() {
                        }
                    }
            );
        } else {
            followMarker = false;
        }
    }

    @Override
    public void performEvent(Event event) {
        /*
         * If the route has been changed (more specifically this should be called when the TripPlanner
         * changes the route somehow) we need to listen to this and modify the MapView accordingly.
         */
        //region ChangedRouteEvent
        if (event.isType(ChangedRouteEvent.class)) {
            mapView.setRoute(mapModel.getRoute());
            mapView.setMarkers(mapModel.getRoute().getCheckpoints());
        }
        //endregion

        /*
         * When a Route is requested by the user (More specifically this is done from RouteActivity when
         * the user presses 'Navigate'). We need to adjust the MapView accordingly by setting
          * all UI Text-elements to the information that we need to provide.
         */
        //region RouteRequestEvent
        if(event.isType(RouteRequestEvent.class)){
            RouteRequestEvent routeRequestEvent = (RouteRequestEvent)event;

            mapView.setFinalDestinationText(mapModel.getRoute().getFinalDestination().getAddress());
            mapView.setFinalDestinationDistText(((mapModel.getRoute().getDistance() + 50) / 100) / 10.0 + "km | ");
            mapView.setFinalDestinationETAText(mapModel.getRoute().getEta().getStandardHours()  + "h " + mapModel.getRoute().getEta().getStandardMinutes() % 60 + "min");
            mapView.showStartRouteDialog(true);

            // If we have any checkpoints in the new route, display the first one as the truck starts to move.
            if(!mapModel.getRoute().getCheckpoints().isEmpty()) {
                // Get first checkpoint
                RouteLocation firstCheckpoint = mapModel.getRoute().getCheckpoints().get(0);

                // Display the adress of the first checkpoint to the driver.
                mapView.setNextCheckpointText(firstCheckpoint.getName());
                mapView.setNextCheckpointDistText(firstCheckpoint.getDistance() + "");
                mapView.setNextCheckpointETAText(firstCheckpoint.getEta().getStandardMinutes() + "");
            } else {
                // If we didn't have any checkpoints in the new route, we display the next checkpoint as the final destination.
                mapView.setNextCheckpointText(mapModel.getRoute().getFinalDestination().getName());
                mapView.setNextCheckpointETAText(mapModel.getRoute().getEta().getStandardHours()  + "h " + mapModel.getRoute().getEta().getStandardMinutes() % 60 + "min");
                mapView.setNextCheckpointDistText(((mapModel.getRoute().getDistance() + 50) / 100) / 10.0 + "km | ");
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(LocationHandler.getCurrentLocationAsLatLng());
            builder.include(new LatLng(routeRequestEvent.getFinalDestion().getLatitude(), routeRequestEvent.getFinalDestion().getLongitude()));
            LatLngBounds bounds = builder.build();

            mapView.moveCamera(true, bounds);
        }
        //endregion

        /**
         * When the distractionlevel changes the app specifies that we need to follow the marker or not.
         * This is done via the FollowMarkerEvent.
         */
        //region FollowMarkerEvent
        if(event.isType(FollowMarkerEvent.class)){
            setFollowMarker(((FollowMarkerEvent)event).getFollowMarker());
        }
        //endregion
    }

    @Override
    public Fragment getFragment() {
        return mapView;
    }

    @Override
    public String getName() {
        return "Map";
    }
}
