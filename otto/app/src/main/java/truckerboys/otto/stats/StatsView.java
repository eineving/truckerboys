package truckerboys.otto.stats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import truckerboys.otto.R;
import utils.IView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class StatsView extends Fragment implements IView {

    private View rootView;



    public StatsView(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fragment_stats, container, false);

        return rootView;
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public String getName() {
        return "Statistics";
    }
}
