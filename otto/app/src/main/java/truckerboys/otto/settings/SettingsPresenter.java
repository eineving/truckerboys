package truckerboys.otto.settings;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.TextView;

import truckerboys.otto.driver.User;
import truckerboys.otto.utils.IPresenter;
import truckerboys.otto.utils.eventhandler.EventBus;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.DisplayChangedEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.EventType;
import truckerboys.otto.utils.eventhandler.events.RestoreSettingsEvent;
import truckerboys.otto.utils.eventhandler.events.SoundChangedEvent;
import truckerboys.otto.utils.eventhandler.events.TankChangedEvent;
import truckerboys.otto.vehicle.FuelTankInfo;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Presenter handling logic between settingsView and settingsModel
 */
public class SettingsPresenter implements IPresenter, IEventListener {
    private SettingsModel model;
    private SettingsView view;
    private User user;

    public static final String SETTINGS = "Settings_file";

    public SettingsPresenter(FuelTankInfo fuelTank, User user){
        this.user = user;

        EventBus.getInstance().subscribe(this, EventType.SETTINGS);

        this.view = new SettingsView();
        this.model = new SettingsModel(fuelTank);

    }

    /**
     * Method to run when sound switched is changed
     * @param b on/off
     */
    public void displayChanged(boolean b) {
        // Writes preferences to the settings and stats file
        SharedPreferences.Editor settingsEditor = view.getActivity().getSharedPreferences(SETTINGS, 0).edit();

        settingsEditor.putBoolean("displayAlive", b);


        // Commit the changes
        settingsEditor.apply();

        view.settingsChanged();
    }


    /**
     * Method to run when sound switched is changed
     * @param sound on/off
     */
    public void soundChanged(boolean sound) {

        view.soundChanged(sound);

    }

    /**
     * Method for restoring the saved preferences from
     * the user statistics and the user settings
     */
    public void restorePreferences() {

        boolean displayAlive = view.getActivity().getSharedPreferences(SETTINGS, 0).getBoolean("displayAlive", true); // true is the value to be returned if no "displayAlive"-value exists
        int ringerMode = ((AudioManager) view.getActivity().getSystemService(Context.AUDIO_SERVICE)).getRingerMode();

        // If ringerMode is set on Normal (1) set sound as true
        boolean sound = (ringerMode == AudioManager.RINGER_MODE_NORMAL);

        // Sets tank size in view, defaults to 330 L
        ((EditText)view.getTankSize()).setText("" + view.getActivity().getSharedPreferences(SETTINGS, 0).getInt("tankSize", 330));

        // Sets tank size in model, defaults to 330 L
        model.setTankSize(view.getActivity().getSharedPreferences(SETTINGS, 0).getInt("tankSize", 330));

        setSettings(displayAlive);

        view.update(sound, isDisplayActive());

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
    public Fragment getFragment() {
        return view;
    }

    @Override
    public String getName() {
        return "Settings";
    }

    @Override
    public void performEvent(Event event) {

        if(event.isType(RestoreSettingsEvent.class)) {
            restorePreferences();
        }

        if(event.isType(TankChangedEvent.class)) {
            model.setTankSize(Integer.parseInt(((TextView)view.getTankSize()).getText() + ""));
        }

        if(event.isType(DisplayChangedEvent.class)) {
            displayChanged(((DisplayChangedEvent) event).getDisplayAlive());
        }

        if(event.isType(SoundChangedEvent.class)) {
            soundChanged(((SoundChangedEvent) event).getSound());
        }

    }
}
