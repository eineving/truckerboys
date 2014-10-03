package truckerboys.otto.settings;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.v4.app.Fragment;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import truckerboys.otto.IPresenter;
import truckerboys.otto.MainActivity;
import truckerboys.otto.R;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.events.SettingsChangedEvent;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Presenter handling logic between settingsView and settingsModel
 */
public class SettingsPresenter{
    private SettingsModel model;

    private SharedPreferences settings;

    public SettingsPresenter( SharedPreferences settings){
        this.model = new SettingsModel();
        this.settings = settings;
    }

    public void setListeners(Switch sound, Switch display, Switch unit) {

        unit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                unitsChanged(b);
            }
        });

        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                soundChanged(b);
            }
        });

        display.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        SharedPreferences.Editor settingsEditor = settings.edit();

        settingsEditor.putBoolean("displayAlive", b);


        // if b == true call view.getActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // if b == false call view.getActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Commit the changes
        settingsEditor.commit();

        EventTruck.getInstance().newEvent(new SettingsChangedEvent());

    }

    /**
     * Method to run when units switched is changed
     * @param b on/off
     */
    public void unitsChanged(boolean b) {

        // Writes preferences to the settings and stats file
        SharedPreferences.Editor settingsEditor = settings.edit();

        String system = (b ? "metric" : "imperial");

        System.out.println("***********" + system + "*****************");

        if(b) {
            settingsEditor.putString("system", system);
        } else {
            settingsEditor.putString("system", system);
        }

        // Commit the changes
        settingsEditor.commit();

        // Read new value metric/imperial from parameters
        EventTruck.getInstance().newEvent(new SettingsChangedEvent(system));
    }

    /**
     * Method to run when sound switched is changed
     * @param b on/off
     */
    public void soundChanged(boolean b) {

        // Writes preferences to the settings and stats file
        SharedPreferences.Editor settingsEditor = settings.edit();

        settingsEditor.putBoolean("sound", b);

        // if b == true call ((AudioManager)view.getActivity().this.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        // if b == false call ((AudioManager)view.getActivity().this.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_SILENT);


        // Commit the changes
        settingsEditor.commit();

        // Read new value metric/imperial from parameters
        EventTruck.getInstance().newEvent(new SettingsChangedEvent());
    }

    /**
     * Method for restoring the saved preferences from
     * the user statistics and the user settings
     */
    public void restorePreferences() {
        boolean sound = settings.getBoolean("sound", true); // true is the value to be returned if no "sound"-value exists
        boolean displayAlive = settings.getBoolean("displayAlive", true); // true is the value to be returned if no "displayAlive"-value exists

        setSettings(sound, displayAlive);
    }

    /**
     * Method for setting the restored settings in the
     * model and updating the view
     * @param sound on/off
     * @param displayAlive on/off
     */
    public void setSettings(boolean sound, boolean displayAlive) {
        model.setSettings(sound, displayAlive);
    }

    public boolean isSoundOn(){
        return model.getSound();
    }

    public boolean isDisplayActive(){
        return model.getDisplayAlive();
    }

    public SettingsModel getModel() {
        return model;
    }

}
