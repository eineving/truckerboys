package truckerboys.otto.maps;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapPresenter {
    private MapModel mapModel;
    private MapView mapView;

    public MapPresenter(){
        this.mapView = new MapView();
        this.mapModel = new MapModel();
    }
}
