package truckerboys.otto.maps;

import android.support.v4.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.LocationHandler;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.ChangedRouteEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import utils.IView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 *
 * Handles communication between MapView and MapModel.
 */
public class MapPresenter implements GoogleMap.OnCameraChangeListener, IEventListener, IView {
    private MapModel mapModel;
    private MapView mapView;

    public MapPresenter(TripPlanner tripPlanner){
        this.mapView = new MapView();
        this.mapModel = new MapModel(tripPlanner, mapView.getGoogleMap());
        this.mapView.getGoogleMap().setOnCameraChangeListener(this);
    }

    public Route getRoute(){
        return mapModel.getRoute();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (getRoute() != null) {
            mapView.updateCamera(getRoute(), cameraPosition);
        }
    }

    @Override
    public void performEvent(Event event) {
        if (event.isType(ChangedRouteEvent.class)) {
            mapView.updateCamera(getRoute());
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
