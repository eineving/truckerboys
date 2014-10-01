package truckerboys.otto.maps;

import android.support.v4.app.Fragment;

import truckerboys.otto.IPresenter;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapPresenter implements IPresenter{
    private MapModel model;
    private MapView view;

    public MapPresenter(){
        this.view = new MapView();
        this.model = new MapModel();
    }

    @Override
    public Fragment getView() {
        return view;
    }

    @Override
    public String getName() {
        return "Map";
    }
}
