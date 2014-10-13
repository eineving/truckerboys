package truckerboys.otto.settings;


import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.SettingsChangedEvent;
import truckerboys.otto.utils.eventhandler.events.SoundChangedEvent;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Presenter handling logic between settingsView and settingsModel
 */
public class SettingsPresenter implements IEventListener{
    private SettingsModel model;

    private SharedPreferences settings;

    public SettingsPresenter(SharedPreferences settings){
        this.model = new SettingsModel();
        this.settings = settings;

        EventTruck.getInstance().subscribe(this);
    }

    public void setListeners(Switch sound, Switch display, EditText tankSize) {

        /*unit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                unitsChanged(b);
            }
        });*/

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
                System.out.println("***************PENIS*************");
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
     * @param sound on/off
     */
    public void soundChanged(boolean sound) {

        // Fire an event with new sound mode on/off
        EventTruck.getInstance().newEvent(new SoundChangedEvent(sound));

    }

    /**
     * Method for restoring the saved preferences from
     * the user statistics and the user settings
     */
    public void restorePreferences() {
        //boolean sound = settings.getBoolean("sound", true); // true is the value to be returned if no "sound"-value exists

        // TODO: Load sound from system sound
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

    @Override
    public void performEvent(Event event) {
        if(event.isType(SoundChangedEvent.class)) {

        }

    }
}
