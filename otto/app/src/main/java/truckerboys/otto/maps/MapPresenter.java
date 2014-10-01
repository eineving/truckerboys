package truckerboys.otto.maps;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import truckerboys.otto.planner.TripPlanner;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapPresenter implements PropertyChangeListener {
    private MapModel mapModel;

    public MapPresenter(Context context, GoogleMap googleMap, TripPlanner tripPlanner){
        this.mapModel = new MapModel(context, googleMap, tripPlanner);
        this.mapModel.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

    }

    public void addListenerToModel(PropertyChangeListener propertyChangeListener){
        this.mapModel.addPropertyChangeListener(propertyChangeListener);
    }
}
