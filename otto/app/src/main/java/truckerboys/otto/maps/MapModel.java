package truckerboys.otto.maps;

import android.location.Address;
import android.location.Location;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.LocationHandler;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.GPSUpdateEvent;
import truckerboys.otto.utils.eventhandler.events.RouteRequestEvent;
import truckerboys.otto.utils.exceptions.InvalidRequestException;
import truckerboys.otto.utils.exceptions.NoActiveRouteException;
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.MapLocation;
import truckerboys.otto.utils.positions.RouteLocation;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapModel implements IEventListener {
    // The distance from a checkpoint (in meters) that we need to go to change route.
    private static final int DISTANCE_FROM_CHECKPOINT = 200;

    private TripPlanner tripPlanner;

    private Handler updateRouteHandler = new Handler();
    private Runnable updateRoute = new Runnable() {
        @Override
        public void run() {
            if(tripPlanner != null && LocationHandler.isConnected()){
                try {
                    tripPlanner.updateRoute(LocationHandler.getCurrentLocationAsMapLocation());
                } catch (InvalidRequestException e) {
                    e.printStackTrace();
                } catch (NoConnectionException e) {
                    e.printStackTrace();
                }
            }

            //Call this runnable every 30 seconds.
            //TODO Uncomment
            //updateRouteHandler.postDelayed(updateRoute, 120*1000);
        }
    };

    // If we've been close to a checkpoint on the route.
    private boolean closeToCheckpoint = false;

    public MapModel(final TripPlanner tripPlanner) {
        this.tripPlanner = tripPlanner;
        EventTruck.getInstance().subscribe(this);
        //TODO Uncomment
        //updateRouteHandler.post(updateRoute);
    }

    @Override
    public void performEvent(Event event) {
        /**
         * When the LocationHandler registers a location change it fires a GPSUpdateEvent.
         * We need to catch this here in order to check if we passed a checkpoint on the route.
         *
         * TODO Implement the fact that you can also be outside the route. If so, calculate a new one.
         */
        //region GPSUpdateEvent
        if (event.isType(GPSUpdateEvent.class)) {
            GPSUpdateEvent gpsUpdateEvent = (GPSUpdateEvent) event;

            /**
             * Check if we've passed the upcoming checkpoint on the route.
             */
            // If we have route and checkpoints in route.
            if(getRoute() != null && getRoute().getCheckpoints().size() > 0) {
                // If we got in range of the checkpoint since last update.
                if (gpsUpdateEvent.getNewPosition().distanceTo(getRoute().getCheckpoints().get(0)) < DISTANCE_FROM_CHECKPOINT && !closeToCheckpoint) {
                    closeToCheckpoint = true;
                } else if (closeToCheckpoint) /* We're currently in range. */ {
                    try {
                        // We left checkpoint range again, calculate new route to final destination.
                        if (gpsUpdateEvent.getNewPosition().distanceTo(getRoute().getCheckpoints().get(0)) > DISTANCE_FROM_CHECKPOINT) {
                            tripPlanner.updateRoute(LocationHandler.getCurrentLocationAsMapLocation());
                            closeToCheckpoint = false;
                        }
                    } catch (InvalidRequestException e) {
                        //TODO Create proper catch
                        e.printStackTrace();
                    } catch (NoConnectionException e) {
                        //TODO Create proper catch
                        e.printStackTrace();
                    }
                }
            }
        }
        //endregion

        /*
         * When a Route is requested by the user (More specifically this is done from RouteActivity when
         * the user presses 'Navigate'). We need to pass all this information to the TripPlanner making it calculate
         * a new route with the information we provide.
         */
        //region NewRouteEvent (This event is fired when the user requests a new route from RouteActivity.)
        if (event.isType(RouteRequestEvent.class)) {
            try {
                // We get the checkpoints as adresses in RouteRequestEvent.
                // Convert them to MapLocations making it possible to send them into the TripPlanner.
                ArrayList<MapLocation> checkpoints = new ArrayList<MapLocation>();
                for (Address address : ((RouteRequestEvent) event).getCheckpoints()) {
                   checkpoints.add(new MapLocation(new LatLng(address.getLatitude(), address.getLongitude())));
                }

                // Tell the TripPlanner to calculate a new route with the information we provide.
                tripPlanner.setNewRoute(
                        new MapLocation(LocationHandler.getCurrentLocationAsMapLocation()),
                        new MapLocation(new LatLng(((RouteRequestEvent) event).getFinalDestion().getLatitude(),
                                ((RouteRequestEvent) event).getFinalDestion().getLongitude())),
                        checkpoints);
            } catch (InvalidRequestException e) {
                //TODO Create proper catch
                e.printStackTrace();
            } catch (NoConnectionException e) {
                //TODO Create proper catch
                e.printStackTrace();
            }
        }
        //endregion
    }

    public Route getRoute() {
        try {
            return tripPlanner.getRoute();
        } catch (NoActiveRouteException e) {
            //TODO catch this properly!!!
            e.printStackTrace();
            return null;
        }
    }
}
