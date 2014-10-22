package truckerboys.otto.maps;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.LocationHandler;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.ChangedRouteEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.IView;
import truckerboys.otto.utils.eventhandler.events.FollowMarkerEvent;
import truckerboys.otto.utils.eventhandler.events.RouteRequestEvent;
import truckerboys.otto.utils.exceptions.NoActiveRouteException;
import truckerboys.otto.utils.positions.RouteLocation;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 *
 * Handles communication between MapView and MapModel.
 */
public class MapPresenter implements IEventListener, IView {
    private MapModel mapModel;
    private MapView mapView;

    //region Runnables
    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable updatePos = new Runnable() {
        public void run() {
            if(mapView.isFollowingMarker()) {
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

    @Override
    public void performEvent(Event event) {
        /*
         * If the route has been changed (more specifically this should be called when the TripPlanner
         * changes the route somehow) we need to listen to this and modify the MapView accordingly.
         */
        //region ChangedRouteEvent
        if (event.isType(ChangedRouteEvent.class)) {
            try {
                mapView.setRoute(mapModel.getRoute());
                mapView.setMarkers(mapModel.getRoute().getCheckpoints());
            } catch (NoActiveRouteException e) {
                //TODO Make proper catch.
            }
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

            try {
                Route route = mapModel.getRoute();

                mapView.setFinalDestinationText(route.getFinalDestination().getAddress());
                mapView.setFinalDestinationDistText(((route.getDistance() + 50) / 100) / 10.0 + "km | ");
                mapView.setFinalDestinationETAText(route.getEta().getStandardHours()  + "h " + mapModel.getRoute().getEta().getStandardMinutes() % 60 + "min");
                mapView.showStartRouteDialog(true);

                // Get first checkpoint
                RouteLocation firstCheckpoint = route.getCheckpoints().get(0);

                // Display the adress of the first checkpoint to the driver.
                mapView.setNextCheckpointText(firstCheckpoint.getAddress());
                mapView.setNextCheckpointDistText(((firstCheckpoint.getDistance() + 50) / 100) / 10.0 + "km | ");
                mapView.setNextCheckpointETAText(firstCheckpoint.getEta().getStandardHours()  + "h " + firstCheckpoint.getEta().getStandardMinutes() % 60 + "min");

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(LocationHandler.getCurrentLocationAsLatLng());
                builder.include(new LatLng(routeRequestEvent.getFinalDestion().getLatitude(), routeRequestEvent.getFinalDestion().getLongitude()));
                LatLngBounds bounds = builder.build();

                mapView.moveCamera(true, bounds);

            } catch (NoActiveRouteException e) {
                // It's stupid to fire a RouteRequestEvent without an active route. But if it happens,
                // Make sure all dialogs are hidden, making sure the user doesn't see any incorrect information.
                mapView.showStartRouteDialog(false);
                mapView.showActiveRouteDialog(false);
                mapView.showStopRoute(false);
            }
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
