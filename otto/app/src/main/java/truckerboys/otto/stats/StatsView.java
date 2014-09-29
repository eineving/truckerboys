package truckerboys.otto.stats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class StatsView extends Fragment {

    private View rootView;

    public StatsView(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fragment_stats, container, false);

        return rootView;
    }
}
