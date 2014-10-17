package truckerboys.otto.maps;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.LocationHandler;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.ChangedRouteEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.IView;
import truckerboys.otto.utils.eventhandler.events.RouteRequestEvent;
import truckerboys.otto.utils.positions.MapLocation;
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
            if(mapModel.isActiveRoute()) {
                mapView.updatePositionMarker(true);
            } else {
                mapView.updatePositionMarker(false);
            }
            updateHandler.postDelayed(updatePos, LocationHandler.LOCATION_REQUEST_INTERVAL_MS / MapView.INTERPOLATION_FREQ);
        }
    };
    //endregion

    public MapPresenter(TripPlanner tripPlanner){
        this.mapView = new MapView();
        mapView.setPresenter(this);

        this.mapModel = new MapModel(tripPlanner);
        EventTruck.getInstance().subscribe(this);
    }

    public void startFollowRoute(){
        mapView.moveCamera(true, LocationHandler.getCurrentLocationAsLatLng(), 18f, LocationHandler.getCurrentLocationAsMapLocation().getBearing(), 1000);
        mapModel.setActiveRoute(true);

        //Make sure the handler doesnt run updatePos to many times.
        updateHandler.removeCallbacks(updatePos);
        //Wait 1000ms before running the updatePos runnable. Making the above animation finish before starting the next one.
        updateHandler.postDelayed(updatePos, 2000);
    }

    public void stopFollowRoute(){
        mapModel.setActiveRoute(false);
    }

    @Override
    public void performEvent(Event event) {
        if (event.isType(ChangedRouteEvent.class)) {
            mapView.setRoute(mapModel.getRoute());
            mapView.setMarkers(mapModel.getRoute().getCheckpoints());
        }

        // If a new route is set in RouteActivity, zoom out to see all of the route.
        if(event.isType(RouteRequestEvent.class)){
            RouteRequestEvent routeRequestEvent = (RouteRequestEvent)event;

            mapView.setFinalDestinationText(mapModel.getRoute().getFinalDestination().getAddress());
            mapView.setFinalDestinationDistText(((mapModel.getRoute().getDistance() + 50) / 100) / 10.0 + "km | ");
            mapView.setFinalDestinationETAText(mapModel.getRoute().getEta().getStandardHours()  + "h " + mapModel.getRoute().getEta().getStandardMinutes() % 60 + "min");
            mapView.showStartRouteDialog(true);

            if(!mapModel.getRoute().getCheckpoints().isEmpty()) {
                // Get first checkpoint
                RouteLocation firstCheckpoint = mapModel.getRoute().getCheckpoints().get(0);

                // Set first checkpoints in strings.
                mapView.setNextCheckpointText(firstCheckpoint.getAddress());
                //mapView.setNextCheckpointDistText(firstCheckpoint.getDistance());
                mapView.setNextCheckpointETAText(firstCheckpoint.getEta().getStandardMinutes() + "");
            } else {
                mapView.setNextCheckpointText(mapModel.getRoute().getFinalDestination().getAddress());
                mapView.setNextCheckpointETAText(mapModel.getRoute().getEta().getStandardHours()  + "h " + mapModel.getRoute().getEta().getStandardMinutes() % 60 + "min");
                mapView.setNextCheckpointDistText(((mapModel.getRoute().getDistance() + 50) / 100) / 10.0 + "km | ");
            }
            mapView.showActiveRouteDialog(false);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(LocationHandler.getCurrentLocationAsLatLng());
            builder.include(new LatLng(routeRequestEvent.getFinalDestion().getLatitude(), routeRequestEvent.getFinalDestion().getLongitude()));
            LatLngBounds bounds = builder.build();

            mapView.moveCamera(true, bounds);
        }
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
