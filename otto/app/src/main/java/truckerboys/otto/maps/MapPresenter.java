package truckerboys.otto.maps;

import android.support.v4.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.LocationHandler;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.ChangedRouteEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.IView;

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
        this.mapView.setOnCameraChangeListener(this);

        this.mapModel = new MapModel(tripPlanner);
    }

    public Route getRoute(){
        return mapModel.getRoute();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (getRoute() != null) {
            mapView.updatePolyline(getRoute(), cameraPosition.zoom, LocationHandler.getCurrentLocationAsMapLocation());
        }

        mapView.adjustCamera(LocationHandler.getCurrentLocationAsLatLng(), cameraPosition.bearing);
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
