package truckerboys.otto.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import truckerboys.otto.R;
import truckerboys.otto.utils.eventhandler.EventBus;
import truckerboys.otto.utils.eventhandler.events.DisplayChangedEvent;
import truckerboys.otto.utils.eventhandler.events.RestoreSettingsEvent;
import truckerboys.otto.utils.eventhandler.events.SoundChangedEvent;
import truckerboys.otto.utils.eventhandler.events.TankChangedEvent;
import truckerboys.otto.vehicle.FuelTankInfo;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */

public class SettingsView extends Fragment {
    private View rootView;
    private Switch soundSwitch;
    private Switch displaySwitch;
    private EditText tankSize;
    private FuelTankInfo fuelTank;
    //private User user;

    public static final String SETTINGS = "Settings_file";

    public SettingsView() {

    }

    public SettingsView(FuelTankInfo fuelTank) {
        this.fuelTank = fuelTank;
    }

    public View getTankSize() {
        return tankSize;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Creates switches from the fragment
        soundSwitch = (Switch) rootView.findViewById(R.id.soundSwitch);
        displaySwitch = (Switch) rootView.findViewById(R.id.displaySwitch);
        tankSize = (EditText) rootView.findViewById(R.id.tankEditText);


        // Sets button listeners
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                // Fire event to be handled by the presenter
                EventBus.getInstance().newEvent(new SoundChangedEvent(b));

            }
        });

        displaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                // Fire event to be handled by the presenter
                EventBus.getInstance().newEvent(new DisplayChangedEvent(b));

            }
        });

        tankSize.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(!tankSize.getText().toString().equals("") && tankSize.getText() != null) {
                    // Fire event to be handled by the presenter
                    EventBus.getInstance().newEvent(new TankChangedEvent(Integer.parseInt(tankSize.getText() + "")));

                }

                return false;
            }
        });

        SharedPreferences settings = getActivity().getSharedPreferences(SETTINGS, 0);

        // Reads stored tank size from shared preferences
        // tankSize.setText("" + settings.getInt("tankSize", 330));

        // Restores preferences for settings in presenter
        EventBus.getInstance().newEvent(new RestoreSettingsEvent());

        return rootView;

    }



    /**
     * Pause method for the view.
     * Saving the size of the fuel tank when paused.
     */
    @Override
    public void onPause() {
        super.onPause();

        // Stores the tankSize when on pause
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(SETTINGS, 0).edit();

        if (tankSize.getText().toString().length() > 0) {
            editor.putInt("tankSize", Integer.parseInt(tankSize.getText() + ""));
        }

        editor.apply();
    }


    /**
     * Update the switches with settings loaded from shared preference file
     * @param sound        on/off
     * @param displayAlive on/off
     */
    public void update(boolean sound, boolean displayAlive) {
        soundSwitch.setChecked(sound);
        displaySwitch.setChecked(displayAlive);

    }

    public void settingsChanged() {
        // Loads settings file
        SharedPreferences settings = getActivity().getSharedPreferences(SETTINGS, 0);

        // Keeps display alive or not based on settings
        if (settings.getBoolean("displayAlive", true)) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }
    }

    public void soundChanged(boolean sound) {
        // Checks if sound is on or off and sets systems sound based on this
        if (sound) {
            ((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } else {
            ((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
    }

}