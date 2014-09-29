package truckerboys.otto.settings;

import truckerboys.otto.FragmentView;
import truckerboys.otto.IPresenter;
import truckerboys.otto.clock.ClockModel;
import truckerboys.otto.clock.ClockView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class SettingsPresenter implements IPresenter {
    private SettingsModel model;
    private SettingsView view;

    public SettingsPresenter(SettingsView view, SettingsModel model){
        this.view = view;
        this.model = model;
    }


    @Override
    public FragmentView getView() {
        return view;
    }
}
