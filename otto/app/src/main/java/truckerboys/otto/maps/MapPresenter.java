package truckerboys.otto.maps;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import truckerboys.otto.planner.TripPlanner;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapPresenter implements PropertyChangeListener {
    private MapModel mapModel;

    public MapPresenter(TripPlanner tripPlanner){
        this.mapModel = new MapModel(tripPlanner);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

    }
}
