package truckerboys.otto.stats;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.scs.data.SCSFloat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.ArrayList;

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
import utils.IView;



/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Class for displaying statistics for the user.
 */

public class StatsView extends Fragment implements IView, IEventListener, IVehicleListener {

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

    private String distanceUnit = "";
    private String fuelUnit = "";



    public StatsView(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fragment_stats, container, false);

        // Subscribes to the signals wanted
        VehicleInterface.subscribe(this, VehicleSignalID.KM_PER_LITER);
        VehicleInterface.subscribe(this, VehicleSignalID.FMS_HIGH_RESOLUTION_TOTAL_VEHICLE_DISTANCE);

        // Creates TextViews from the fragment for daily stats
        timeToday = (TextView) rootView.findViewById(R.id.timeTodayTime);
        distanceByFuel = (TextView) rootView.findViewById(R.id.KmByFuel);

        // Creates TextViews from the fragment for daily stats
        timeTotal = (TextView) rootView.findViewById(R.id.timeTotalTime);
        distanceTotal = (TextView) rootView.findViewById(R.id.distanceTotaldistance);
        fuelTotal = (TextView) rootView.findViewById(R.id.fuelTotalFuel);

        // Creates TextViews from the fragment for violation stats
        this.violations = (TextView) rootView.findViewById(R.id.numberOfViolations);

        // Restores preferences for settings in presenter
        EventTruck.getInstance().newEvent(new RestorePreferencesEvent());


        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        Duration durationToday;
        Duration durationTotal;


        // TODO UNCOMMENT THIS
        durationToday = User.getInstance().getHistory().getActiveTimeSinceLastDailyBreak();

        // TODO Change this to SessionHistory.getActiveTimeSince(Instant start)
        durationTotal = User.getInstance().getHistory().getActiveTimeSince(new Instant(0));
        // TODO Ask pegelow about this


        double timeDrivenToday = durationToday.getStandardMinutes()/60;
        double timeDrivenTotal = durationTotal.getStandardMinutes()/60;

        // TEST VALUES
        //double timeDrivenToday = 55.0/60.0;
        //double timeDrivenTotal = 120.0/60.0;

        //TODO UNCOMMENT IN NEXT MERGE
        // timeToday.setText(timeDrivenToday + "h");
        // timeTotal.setText(timeDrivenTotal + "h");

        timeToday.setText(timeDrivenToday + " h");
        timeTotal.setText(timeDrivenTotal + " h");


        //EventTruck.getInstance().newEvent(new TimeDrivenEvent(durationToday.getStandardMinutes()/60, durationTotal.getStandardMinutes()/60));
        EventTruck.getInstance().newEvent(new TimeDrivenEvent(timeDrivenToday, timeDrivenTotal));

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

        // editor.putFloat("distanceToday", Float.parseFloat(distanceToday.getText().toString().substring(0, distanceToday.getText().toString().length() - 3)));
        editor.putFloat("distanceTotal", Float.parseFloat(distanceTotal.getText().toString().substring(0, distanceTotal.getText().toString().length() - 3)));

        // editor.putFloat("fuelToday", Float.parseFloat(fuelToday.getText().toString()));
        editor.putFloat("fuelTotal", Float.parseFloat(fuelTotal.getText().toString().substring(0, fuelTotal.getText().toString().length() - 2)));
        editor.putFloat("distanceByFuel", Float.parseFloat(distanceByFuel.getText().toString().substring(0, distanceByFuel.getText().toString().length() - 5)));


        editor.commit();


    }

    /**
     * Sets user history.
     * Run this method when history is updated.
     * @param history1
     * @param history2
     * @param history3
     * @param history4
     * @param history5
     */
    public void setUserHistory(ArrayList<String> history1, ArrayList<String> history2,
                               ArrayList<String> history3, ArrayList<String> history4,
                               ArrayList<String> history5){

    }

    /**
     * Method for setting a new unit system.
     * This method is to be called from the presenter.
     * @param system metric/imperial
     */
    public void
    updateUnits(String system) {
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

        /*if(distanceTotal != null) {
            distanceTotal.setText(Math.floor(34.5223*100)/100 + " km");
        }*/

        if(fuelTotal != null) {
            fuelTotal.setText(Math.floor(statsTotal[2]*100)/100 + " L");
        }

        /*if(fuelTotal != null) {
            fuelTotal.setText(Math.floor(654.3424*100)/100 + " L");
        }*/


        // Sets violations
        if(this.violations != null) {
            this.violations.setText("" + violations);
        }
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public String getName() {
        return "Statistics";
    }


    @Override
    public void performEvent(Event event) {
        if(event.isType(SettingsChangedEvent.class)) {

            // read from file and set String called system based on that
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(SETTINGS, 0).edit();

            editor.putString("system", ((SettingsChangedEvent) event).getSystem());

            editor.commit();

        }


        if(event.isType(TotalDistanceEvent.class)) {
            // Update view if new total distance signal is sent

            distanceTotal.setText(((TotalDistanceEvent)event).getTotalDistance() + " km");

        }

        if(event.isType(DistanceByFuelEvent.class)) {
            // Update view if new total distance/fuel signal is sent

            distanceByFuel.setText(((DistanceByFuelEvent)event).getDistanceByFuel() + " km/L");

        }
    }

    /**
     * Listens to signals from the truck and sends
     * a new Event trough the EventTruck.
     * This method can't update the view by it self
     * due to thread unsafety.
     * @param signal the signal sent from the truck.
     */
    @Override
    public void receive(AutomotiveSignal signal) {
       // TODO Get fuel consumption

        switch (signal.getSignalId()) {

            case VehicleSignalID.KM_PER_LITER:

                // Gets the total distance by fuel and updates the listeners
                Float kmPerLiter = ((SCSFloat) signal.getData()).getFloatValue();

                EventTruck.getInstance().newEvent(new DistanceByFuelEvent(Math.floor(kmPerLiter * 100)/100));

            case VehicleSignalID.FMS_HIGH_RESOLUTION_TOTAL_VEHICLE_DISTANCE:

                // Gets the total distance and updates the listeners
                Float distance = ((SCSFloat) signal.getData()).getFloatValue();

                EventTruck.getInstance().newEvent(new TotalDistanceEvent(Math.floor(distance * 100)/100));

        }

    }
}
