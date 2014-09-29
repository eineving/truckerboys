package truckerboys.otto.settings;


import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.widget.CompoundButton;

import truckerboys.otto.IPresenter;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Presenter handling logic between settingsView and settingsModel
 */
public class SettingsPresenter implements IPresenter {
    private SettingsModel model;
    private SettingsView view;

    public static final String SETTINGS = "Settings_file";



    public SettingsPresenter(SettingsView view, SettingsModel model){
        this.view = view;
        this.model = model;

    }

    public void setListeners() {

        view.getSoundSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                soundChanged(b);
            }
        });

        view.getDisplaySwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                displayChanged(b);
            }
        });
    }

    /**
     * Method to run when sound switched is changed
     * @param b on/off
     */
    public void displayChanged(boolean b) {
        // Writes preferences to the settings and stats file
        SharedPreferences settings = getView().getActivity().getSharedPreferences(SETTINGS, 0);
        SharedPreferences.Editor settingsEditor = settings.edit();

        settingsEditor.putBoolean("displayAlive", b);

        // Commit the changes
        settingsEditor.commit();

    }

    /**
     * Method to run when sound switched is changed
     * @param b on/off
     */
    public void soundChanged(boolean b) {
        // Writes preferences to the settings and stats file
        SharedPreferences settings = getView().getActivity().getSharedPreferences(SETTINGS, 0);
        SharedPreferences.Editor settingsEditor = settings.edit();

        settingsEditor.putBoolean("sound", b);

        // Commit the changes
        settingsEditor.commit();
    }

    /**
     * Method for restoring the saved preferences from
     * the user statistics and the user settings
     */
    public void restorePreferences() {
        SharedPreferences settings = getView().getActivity().getSharedPreferences(SETTINGS, 0);
        boolean sound = settings.getBoolean("sound", true); // true is the value to be returned if no "sound"-value exists
        boolean displayAlive = settings.getBoolean("displayAlive", true); // true is the value to be returned if no "displayAlive"-value exists

        setSettings(sound, displayAlive);
    }

    public void setSettings(boolean sound, boolean displayAlive) {
        model.setSettings(sound, displayAlive);
        updateView();
    }

    /**
     * Updates view with settings data from model
     */
    public void updateView() {
        boolean sound = model.getSound();
        boolean displayAlive = model.getDisplayAlive();

        view.update(sound, displayAlive);
    }

    public SettingsModel getModel() {
        return model;
    }

    @Override
    public Fragment getView() {
        return view;
    }

    @Override
    public String getName() {
        return "Settings";
    }
}
