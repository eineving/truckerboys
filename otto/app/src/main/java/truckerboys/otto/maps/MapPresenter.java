package truckerboys.otto.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapPresenter implements GoogleMap.OnCameraChangeListener {
    private MapModel mapModel;
    private MapView mapView;

    public MapPresenter(TripPlanner tripPlanner, GoogleMap googleMap){
        this.mapModel = new MapModel(tripPlanner, googleMap);
    }

    public Route getRoute(){
        return mapModel.getCurrentRoute();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }
}
