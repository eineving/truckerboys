package truckerboys.otto.maps;

import android.location.Address;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

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

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapModel implements IEventListener {

    // The amount of meters that the GPS position is allowed to be outside the route.
    private static final int OUTSIDE_ROUTE_DIFF = 10;

    // The distance from a checkpoint (in meters) that we need to go to change route.
    private static final int DISTANCE_FROM_CHECKPOINT = 1000;

    private TripPlanner tripPlanner;
    private boolean activeRoute = false;

    // The number of GPS coordinates in a row that have been distanceToRoute >= OUTSIDE_ROUTE_DIFF
    private List<MapLocation> outsideRoute = new LinkedList<MapLocation>();

    // If we've been close to a checkpoint on the route.
    private boolean closeToCheckpoint = false;

    public MapModel(final TripPlanner tripPlanner) {
        this.tripPlanner = tripPlanner;
        EventTruck.getInstance().subscribe(this);
    }

    @Override
    public void performEvent(Event event) {
        if (event.isType(GPSUpdateEvent.class)) {
            GPSUpdateEvent gpsUpdateEvent = (GPSUpdateEvent) event;
            // TODO Check if outside current route, calculate new route.

            // If we have route and checkpoints in route.
            if(getRoute() != null && getRoute().getCheckpoints().size() > 0) {
                // If we got in range of the checkpoint since last update.
                if (gpsUpdateEvent.getNewPosition().distanceTo(getRoute().getCheckpoints().get(0)) < DISTANCE_FROM_CHECKPOINT && !closeToCheckpoint) {
                    closeToCheckpoint = true;
                } else if (closeToCheckpoint) /* We're currently in range. */ {
                    try {
                        // We left checkpoint range again, calculate new route to final destination.
                        if (gpsUpdateEvent.getNewPosition().distanceTo(getRoute().getCheckpoints().get(0)) > DISTANCE_FROM_CHECKPOINT) {
                            tripPlanner.setNewRoute(LocationHandler.getCurrentLocationAsMapLocation(), getRoute().getFinalDestination(), null);
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
        //region NewRouteEvent (This event is fired when the user requests a new route from RouteActivity.)
        if (event.isType(RouteRequestEvent.class)) {
            try {
                List<Address> adressList = ((RouteRequestEvent) event).getCheckpoints();

                //Check if NewRoute involves any checkpoints.
                if (!adressList.isEmpty()) {

                    // We get the checkpoints as adresses in NewRouteEvent. Remake them to MapLocations
                    // making it possible to send them into tripplanner.
                    MapLocation[] checkpoints = new MapLocation[((RouteRequestEvent) event).getCheckpoints().size()];
                    for (Address address : adressList) {
                        checkpoints[adressList.indexOf(address)] = new MapLocation(new LatLng(address.getLatitude(), address.getLongitude()));
                    }

                    //Calculate new route with provided checkpoints
                    tripPlanner.setNewRoute(
                            new MapLocation(LocationHandler.getCurrentLocationAsMapLocation()),
                            new MapLocation(new LatLng(((RouteRequestEvent) event).getFinalDestion().getLatitude(),
                                    ((RouteRequestEvent) event).getFinalDestion().getLongitude())),
                            checkpoints);

                } else /* No checkpoints in new route. */ {
                    //Calculate new route without any checkpoints
                    tripPlanner.setNewRoute(
                            new MapLocation(LocationHandler.getCurrentLocationAsMapLocation()),
                            new MapLocation(new LatLng(((RouteRequestEvent) event).getFinalDestion().getLatitude(),
                                    ((RouteRequestEvent) event).getFinalDestion().getLongitude())));
                }
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

    public boolean isActiveRoute() {
        return activeRoute;
    }

    public void setActiveRoute(boolean activeRoute) {
        this.activeRoute = activeRoute;
    }
}
