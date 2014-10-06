package truckerboys.otto.maps;

import com.google.android.gms.maps.GoogleMap;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import truckerboys.otto.directionsAPI.Route;
import truckerboys.otto.planner.TripPlanner;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapPresenter {
    private MapModel mapModel;

    public MapPresenter(TripPlanner tripPlanner, GoogleMap googleMap){
        this.mapModel = new MapModel(tripPlanner, googleMap);
    }

    public Route getCurrentRoute(){
        return mapModel.getCurrentRoute();
    }
}
