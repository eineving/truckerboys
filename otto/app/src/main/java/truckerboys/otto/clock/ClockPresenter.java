package truckerboys.otto.clock;

import truckerboys.otto.FragmentView;
import truckerboys.otto.IPresenter;
import truckerboys.otto.home.HomeModel;
import truckerboys.otto.home.HomeView;

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
    public FragmentView getView() {
        return view;
    }
}
