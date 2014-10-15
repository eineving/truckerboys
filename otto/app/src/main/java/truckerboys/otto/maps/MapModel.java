package truckerboys.otto.maps;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

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
import truckerboys.otto.utils.exceptions.NoConnectionException;
import truckerboys.otto.utils.positions.MapLocation;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapModel implements IEventListener {
    private TripPlanner tripPlanner;
    private boolean activeRoute = false;

    public MapModel(final TripPlanner tripPlanner) {
        this.tripPlanner = tripPlanner;
        EventTruck.getInstance().subscribe(this);
    }

    @Override
    public void performEvent(Event event) {
        if (event.isType(GPSUpdateEvent.class)) {
            //TODO Check if outside current route, calculate new route.
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
        return tripPlanner.getRoute();
    }

    public boolean isActiveRoute() {
        return activeRoute;
    }

    public void setActiveRoute(boolean activeRoute) {
        this.activeRoute = activeRoute;
    }
}
