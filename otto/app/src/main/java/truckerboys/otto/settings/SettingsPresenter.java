package truckerboys.otto.settings;


import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import truckerboys.otto.utils.eventhandler.EventBus;
import truckerboys.otto.utils.eventhandler.events.SettingsChangedEvent;
import truckerboys.otto.utils.eventhandler.events.SoundChangedEvent;
import truckerboys.otto.vehicle.FuelTankInfo;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Presenter handling logic between settingsView and settingsModel
 */
public class SettingsPresenter {
    private SettingsModel model;

    private SharedPreferences settings;

    public SettingsPresenter(SharedPreferences settings, FuelTankInfo fuelTank){
        this.model = new SettingsModel(fuelTank);
        this.settings = settings;
    }

    public void setListeners(Switch sound, Switch display, final EditText tankSize) {

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

        tankSize.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                //TODO update model
                if(!tankSize.getText().toString().equals("") && tankSize.getText() != null) {
                    model.setTankSize(Integer.parseInt(tankSize.getText() + ""));
                }

                return false;
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


        // Commit the changes
        settingsEditor.apply();

        EventBus.getInstance().newEvent(new SettingsChangedEvent());
    }


    /**
     * Method to run when sound switched is changed
     * @param sound on/off
     */
    public void soundChanged(boolean sound) {

        // Fire an event with new sound mode on/off
        EventBus.getInstance().newEvent(new SoundChangedEvent(sound));

    }

    /**
     * Method for restoring the saved preferences from
     * the user statistics and the user settings
     */
    public void restorePreferences() {

        boolean displayAlive = settings.getBoolean("displayAlive", true); // true is the value to be returned if no "displayAlive"-value exists

        setSettings(displayAlive);
    }

    /**
     * Method for setting the restored settings in the
     * model and updating the view
     * @param displayAlive on/off
     */
    public void setSettings(boolean displayAlive) {
        model.setSettings(displayAlive);
    }

    public boolean isDisplayActive(){
        return model.getDisplayAlive();
    }

    public SettingsModel getModel() {
        return model;
    }
}
