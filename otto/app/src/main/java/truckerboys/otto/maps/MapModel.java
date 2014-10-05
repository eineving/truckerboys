package truckerboys.otto.maps;

import com.google.android.gms.maps.model.LatLng;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.LocationChangedEvent;
import truckerboys.otto.utils.eventhandler.events.NewRouteEvent;
import truckerboys.otto.utils.positions.MapLocation;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapModel implements IEventListener{
    private TripPlanner tripPlanner;
    private Route currentRoute;

    public MapModel(TripPlanner tripPlanner) {
        this.tripPlanner = tripPlanner;

        //TODO Remove this and instead receive new route from RouteActivity.
        if(currentRoute == null){
            currentRoute = this.tripPlanner.calculateRoute(
                    new MapLocation(new LatLng(57.6878618, 11.9777905)), //Start location
                    new MapLocation(new LatLng(59.326142, 17.9875455)) //End location
            );
            EventTruck.getInstance().newEvent(new NewRouteEvent(currentRoute, currentRoute));
        }
    }


    @Override
    public void performEvent(Event event) {
        if(event.isType(LocationChangedEvent.class)){
            //TODO Check if outside current route, calculate new route.
        }
    }
}
