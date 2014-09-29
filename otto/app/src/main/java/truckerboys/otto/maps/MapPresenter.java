package truckerboys.otto.maps;

import android.support.v4.app.Fragment;

import truckerboys.otto.IPresenter;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapPresenter implements IPresenter{
    private MapModel mapModel;
    private MapView mapView;

    public MapPresenter(MapView view, MapModel model){
        this.mapView = view;
        this.mapModel = model;
    }

    @Override
    public Fragment getMapView() {
        return mapView;
    }
}
