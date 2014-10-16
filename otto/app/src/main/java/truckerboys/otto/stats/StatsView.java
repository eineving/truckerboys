package truckerboys.otto.stats;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.scs.data.SCSFloat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.R;
import truckerboys.otto.driver.User;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.DistanceByFuelEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.RestorePreferencesEvent;
import truckerboys.otto.utils.eventhandler.events.SettingsChangedEvent;
import truckerboys.otto.utils.eventhandler.events.TimeDrivenEvent;
import truckerboys.otto.utils.eventhandler.events.TotalDistanceEvent;
import truckerboys.otto.vehicle.IVehicleListener;
import truckerboys.otto.vehicle.VehicleInterface;
import truckerboys.otto.vehicle.VehicleSignalID;
import truckerboys.otto.IView;



/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Class for displaying statistics for the user.
 */

public class StatsView extends Fragment implements IView, IEventListener{

    private View rootView;
    private static final String SETTINGS = "Settings_file";
    private static final String STATS = "Stats_file";

    // Todays stats
    private TextView timeToday;
    private TextView distanceByFuel;

    // Total stats
    private TextView timeTotal;
    private TextView distanceTotal;
    private TextView fuelTotal;

    // Violation stats
    private TextView violations;

    // History stats
    ArrayAdapter<String> sessionAdapter;
    private ListView historyList;

    private String distanceUnit = "";
    private String fuelUnit = "";

    //region Runnables
    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable statsUpdater = new Runnable() {
        public void run() {
            updateStats();
            updateHandler.postDelayed(statsUpdater, 600000); // Updates each 5sec
        }
    };

    public StatsView(){
    }


    /**
     * Method for updating the stats.
     * This method is run each 5min
     */
    public void updateStats() {
        Duration durationToday;
        Duration durationTotal;


        if(User.getInstance().getHistory() != null) {

            // Time driven today
            durationToday = User.getInstance().getHistory().getActiveTimeSinceLastDailyBreak();

            // Total time driven since start
            durationTotal = User.getInstance().getHistory().getActiveTimeSince(new Instant(0));

            double timeDrivenToday = durationToday.getStandardMinutes()/60;
            double timeDrivenTotal = durationTotal.getStandardMinutes()/60;

            timeToday.setText(timeDrivenToday + " h");
            timeTotal.setText(timeDrivenTotal + " h");


            EventTruck.getInstance().newEvent(new TimeDrivenEvent(timeDrivenToday, timeDrivenTotal));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fragment_stats, container, false);

        // Creates TextViews from the fragment for daily stats
        timeToday = (TextView) rootView.findViewById(R.id.timeTodayTime);
        distanceByFuel = (TextView) rootView.findViewById(R.id.KmByFuel);

        // Session history
        historyList = (ListView) rootView.findViewById(R.id.sessionListView);

        sessionAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_plain_text);

        historyList.setAdapter(sessionAdapter);


        // Creates TextViews from the fragment for daily stats
        timeTotal = (TextView) rootView.findViewById(R.id.timeTotalTime);
        distanceTotal = (TextView) rootView.findViewById(R.id.distanceTotaldistance);
        fuelTotal = (TextView) rootView.findViewById(R.id.fuelTotalFuel);

        // Creates TextViews from the fragment for violation stats
        this.violations = (TextView) rootView.findViewById(R.id.numberOfViolations);

        // Restores preferences for settings in presenter
        EventTruck.getInstance().newEvent(new RestorePreferencesEvent());

        statsUpdater.run();

        return rootView;
    }


    /**
     * On stop, save the settings displayed in the view.
     */
    @Override
    public void onStop() {
        super.onStop();

        //TODO Put this code in the presenter and get values from model

        SharedPreferences.Editor editor = getActivity().getSharedPreferences(STATS, 0).edit();

        editor.putFloat("timeToday", Float.parseFloat(timeToday.getText().toString().substring(0, timeToday.getText().toString().length() - 2)));
        editor.putFloat("timeTotal", Float.parseFloat(timeTotal.getText().toString().substring(0, timeTotal.getText().toString().length() - 2)));
        editor.putFloat("distanceTotal", Float.parseFloat(distanceTotal.getText().toString().substring(0, distanceTotal.getText().toString().length() - 3)));
        editor.putFloat("fuelTotal", Float.parseFloat(fuelTotal.getText().toString().substring(0, fuelTotal.getText().toString().length() - 2)));
        editor.putFloat("distanceByFuel", Float.parseFloat(distanceByFuel.getText().toString().substring(0, distanceByFuel.getText().toString().length() - 5)));


        editor.commit();

    }


    /**
     * Updates the list of previous sessions.
     * @param sessionString data from previous sessions.
     */
    public void updateSessionHistory(String sessionString) {
        sessionAdapter.add(sessionString);

        sessionAdapter.notifyDataSetChanged();

        historyList.setLayoutParams(new LinearLayout.LayoutParams(
                1000, historyList.getAdapter().getCount()*125));

    }

    /**
     * Method for setting a new unit system.
     * This method is to be called from the presenter.
     * @param system metric/imperial
     */
    public void updateUnits(String system) {
        if(system.equals("imperial")) {
            fuelUnit = ""; // MILES
            distanceUnit = ""; // GALLONS

        } else {
            fuelUnit = "";
            distanceUnit = "";
        }

    }


    /**
     * Update the switches with settings loaded from shared preference file
     * @param statsToday stats for today
     * @param statsTotal stats for all-time
     * @param violations violations all-time
     */
    public void update(double[] statsToday, double[] statsTotal, int violations) {

        // Checks so TextViews isn't null when setting texts
        if(timeToday != null) {
            timeToday.setText(Math.floor(statsToday[0]*100)/100 + "");
        }

        if(distanceByFuel != null) {
            distanceByFuel.setText(Math.floor(statsToday[3]*100)/100 + " km/L");
        }

        if(timeTotal != null) {
            timeTotal.setText(Math.floor(statsTotal[0]*100)/100 +"");
        }

        if(distanceTotal != null ){
            distanceTotal.setText(Math.floor(statsTotal[1]*100)/100 + " km");
        }

        if(fuelTotal != null) {
            fuelTotal.setText(Math.floor(statsTotal[2]*100)/100 + " L");
        }


        // Sets violations
        if(this.violations != null) {
            this.violations.setText("" + violations);
        }
    }

    public Fragment getFragment() {
        return this;
    }

    @Override
    public String getName() {
        return "Statistics";
    }

    private void updateTotalDistance(TotalDistanceEvent e){
        //The signals unit should be meters..
        distanceTotal.setText(e.getTotalDistance()/1000 + " km");
    }

    private void updateFuelConsumption(DistanceByFuelEvent e){
        distanceByFuel.setText(e.getDistanceByFuel() + " km/L");
    }


    @Override
    public void performEvent(final Event event) {
        if(event.isType(SettingsChangedEvent.class)) {

            // read from file and set String called system based on that
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(SETTINGS, 0).edit();

            editor.putString("system", ((SettingsChangedEvent) event).getSystem());

            editor.commit();

        }


        if(event.isType(TotalDistanceEvent.class)) {
            // Update view if new total distance signal is sent

            Runnable updateDistance = new Runnable() {
                public void run() {
                    updateTotalDistance((TotalDistanceEvent) event);
                }
            };
            updateHandler.post(updateDistance);
        }

        if(event.isType(DistanceByFuelEvent.class)) {
            // Update view if new total distance/fuel signal is sent
            Runnable updateFuelConsumption = new Runnable() {
                public void run() {
                    updateFuelConsumption((DistanceByFuelEvent)event);
                }
            };
            updateHandler.post(updateFuelConsumption);
        }
    }

}
