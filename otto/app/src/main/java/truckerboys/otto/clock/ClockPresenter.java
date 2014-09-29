package truckerboys.otto.clock;

import android.support.v4.app.Fragment;

import truckerboys.otto.IPresenter;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class ClockPresenter implements IPresenter{
    private ClockModel model;
    private ClockView view;

    public ClockPresenter(ClockView view, ClockModel model){
        this.view = view;
        this.model = model;
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
