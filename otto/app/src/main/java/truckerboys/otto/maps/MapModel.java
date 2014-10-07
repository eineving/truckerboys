package truckerboys.otto.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.LocationHandler;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.ChangedRouteEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.GPSUpdateEvent;
import truckerboys.otto.utils.eventhandler.events.NewDestination;
import truckerboys.otto.utils.eventhandler.events.UpdatedRouteEvent;
import truckerboys.otto.utils.positions.MapLocation;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapModel implements IEventListener{
    private TripPlanner tripPlanner;
    private Route originalRoute ;
    private EventTruck eventTruck = EventTruck.getInstance();

    public MapModel(final TripPlanner tripPlanner, GoogleMap googleMap) {
        this.tripPlanner = tripPlanner;
        eventTruck.subscribe(this);

        Runnable updateRoute = new Runnable() {
            @Override
            public void run() {
                if(LocationHandler.isConnected()) {
                    eventTruck.newEvent(new UpdatedRouteEvent(
                                    tripPlanner.calculateRoute(LocationHandler.getCurrentLocation(),
                                    originalRoute.getFinalDestination()
                                    )));
                }
            }
        };
        ScheduledThreadPoolExecutor routeExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        routeExecutor.scheduleWithFixedDelay(updateRoute, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void performEvent(Event event) {
        if(event.isType(GPSUpdateEvent.class)) {
            //TODO Check if outside current route, calculate new route.
        }
        if(event.isType(NewDestination.class)) {
            originalRoute
                    = tripPlanner.
                    calculateRoute(
                    new MapLocation(LocationHandler.getCurrentLocation()), //Start location
                    new MapLocation(new LatLng(((NewDestination) event).getLocation().getLatitude(),
                            ((NewDestination) event).getLocation().getLongitude())) //End location
            );
            EventTruck.getInstance().newEvent(new ChangedRouteEvent(originalRoute));
        }
    }

    public Route getOriginalRoute(){
        return originalRoute;
    }
}
