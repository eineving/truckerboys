package truckerboys.otto.clock;

import android.support.v4.app.Fragment;

import truckerboys.otto.IPresenter;
import truckerboys.otto.planner.TripPlanner;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class ClockPresenter implements IPresenter{
    private ClockModel model;
    private ClockView view;

    public ClockPresenter(TripPlanner tp){
        this.view = new ClockView();
        this.model = new ClockModel(tp);
    }

    @Override
    public Fragment getView() {
        return view;
    }

    @Override
    public String getName() {
        return "Clock";
    }
}
