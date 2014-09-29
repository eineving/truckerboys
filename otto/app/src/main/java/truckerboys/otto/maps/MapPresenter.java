package truckerboys.otto.maps;

import truckerboys.otto.FragmentView;
import truckerboys.otto.IPresenter;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapPresenter implements IPresenter {
    private MapModel model;
    private MapView view;

    public MapPresenter(MapView view, MapModel model){
        this.view = view;
        this.model = model;
    }


    @Override
    public FragmentView getView() {
        return view;
    }
}
