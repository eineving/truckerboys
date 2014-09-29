package truckerboys.otto.maps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class MapView extends SupportMapFragment {
    private View rootView;
    private GoogleMap googleMap;

    public MapView(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);

        googleMap = getMap();

        if(googleMap != null){ //Map is not null, modify freely.
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setMyLocationEnabled(true);
        }

        return rootView;
    }
}
