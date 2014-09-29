package truckerboys.otto.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * This view knows about its the presenter, which
 * causes a cyclic dependency. This is typically
 * a bad solution, but as for now it'll have to do,
 * since android is build in a horrific way that mostly
 * resemble a scene from the classic flick Nightmare on
 * Elm street more than proper java coding.
 */

public class SettingsView extends Fragment {
    private View rootView;
    private Switch soundSwitch;
    private Switch displaySwitch;
    private SettingsPresenter presenter;

    public SettingsView(){

    }

    public void setPresenter(SettingsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Creates switches from the fragment
        soundSwitch = (Switch) rootView.findViewById(R.id.soundSwitch);
        displaySwitch = (Switch) rootView.findViewById(R.id.displaySwitch);

        // Sets listeners in presenter
        presenter.setListeners();

        // Restores preferences for settings in presenter
        presenter.restorePreferences();

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

    public Switch getSoundSwitch() {
        return soundSwitch;
    }
    public Switch getDisplaySwitch() {
        return displaySwitch;
    }
}