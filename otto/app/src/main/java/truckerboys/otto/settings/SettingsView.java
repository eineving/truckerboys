package truckerboys.otto.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import truckerboys.otto.R;
import utils.IView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * This view knows about its the presenter, which
 * causes a cyclic dependency. This is typically
 * a bad solution, but as for now it'll have to do,
 * since android is build in a horrific way that mostly
 * resemble a scene from the classic flick Nightmare on
 * Elm street more than proper java coding.
 */

public class SettingsView extends Fragment implements IView {
    private View rootView;
    private Switch soundSwitch;
    private Switch displaySwitch;
    private SettingsPresenter presenter;

    public static final String SETTINGS = "Settings_file";

    public SettingsView(){ }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        presenter = new SettingsPresenter(getActivity().getSharedPreferences(SETTINGS, 0));

        // Creates switches from the fragment
        soundSwitch = (Switch) rootView.findViewById(R.id.soundSwitch);
        displaySwitch = (Switch) rootView.findViewById(R.id.displaySwitch);

        // Sets listeners in presenter
        presenter.setListeners(soundSwitch, displaySwitch);

        // Restores preferences for settings in presenter
        presenter.restorePreferences();

        update(presenter.isSoundOn(), presenter.isDisplayActive());
        return rootView;

    }


    /**
     * Update the switches with settings loaded from shared preference file
     * @param sound on/off
     * @param displayAlive on/off
     */
    public void update(boolean sound, boolean displayAlive) {
        soundSwitch.setChecked(sound);
        displaySwitch.setChecked(displayAlive);
    }

    @Override
    public Fragment getFragment(){
        return this;
    }

    @Override
    public String getName() {
        return "Settings";
    }
}