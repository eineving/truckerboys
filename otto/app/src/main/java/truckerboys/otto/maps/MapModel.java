package truckerboys.otto.maps;

import android.location.Address;
import android.location.Location;

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
import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.exceptions.NoConnectionException;
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
                    try {
                        eventTruck.newEvent(new UpdatedRouteEvent(
                                        tripPlanner.calculateRoute(LocationHandler.getCurrentLocation(),
                                        originalRoute.getFinalDestination()
                                        )));
                    } catch (InvalidRequestException e) {
                        //TODO Create proper catch
                        e.printStackTrace();
                    } catch (NoConnectionException e) {
                        //TODO Create proper catch
                        e.printStackTrace();
                    }
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


                MapLocation[] checkpoints = new MapLocation[((NewDestination)event).getCheckpoints().size()];

                for(Address address : ((NewDestination)event).getCheckpoints()) {
                    checkpoints[((NewDestination)event).getCheckpoints().indexOf(address)]
                            = new MapLocation(new LatLng(address.getLatitude(), address.getLongitude()));
                }


            try {

                if(!((NewDestination)event).getCheckpoints().isEmpty()) {
                    originalRoute
                            = tripPlanner.
                            getNewRoute(
                                    new MapLocation(LocationHandler.getCurrentLocation()), //Start location
                                    new MapLocation(new LatLng(((NewDestination) event).getLocation().getLatitude(),
                                            ((NewDestination) event).getLocation().getLongitude())),//End location
                                             checkpoints  // checkpoints

                            );
                } else {
                    originalRoute
                            = tripPlanner.
                            getNewRoute(
                                    new MapLocation(LocationHandler.getCurrentLocation()), //Start location
                                    new MapLocation(new LatLng(((NewDestination) event).getLocation().getLatitude(),
                                            ((NewDestination) event).getLocation().getLongitude())) //End location

                            );
                }


            } catch (InvalidRequestException e) {
                //TODO Create proper catch
                e.printStackTrace();
            } catch (NoConnectionException e) {
                //TODO Create proper catch
                e.printStackTrace();
            }
            EventTruck.getInstance().newEvent(new ChangedRouteEvent(originalRoute));
        }
    }

    public Route getOriginalRoute(){
        return originalRoute;
    }
}
