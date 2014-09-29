package truckerboys.otto.stats;

import truckerboys.otto.FragmentView;
import truckerboys.otto.IPresenter;
import truckerboys.otto.settings.SettingsModel;
import truckerboys.otto.settings.SettingsView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class StatsPresenter implements IPresenter {
    private StatsModel model;
    private StatsView view;

    public StatsPresenter(StatsView view, StatsModel model){
        this.view = view;
        this.model = model;
    }


    @Override
    public FragmentView getView() {
        return view;
    }

}
