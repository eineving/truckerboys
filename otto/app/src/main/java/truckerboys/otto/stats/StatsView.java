package truckerboys.otto.stats;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import truckerboys.otto.R;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.SettingsChangedEvent;
import utils.IView;



/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Class for displaying statistics for the user.
 */

public class StatsView extends Fragment implements IView, IEventListener{

    private View rootView;
    private StatsPresenter presenter;
    private static final String SETTINGS = "Settings_file";

    // Todays stats
    private TextView timeToday;
    private TextView distanceToday;
    private TextView fuelToday;
    private TextView fuelByDistanceToday;

    // Total stats
    private TextView timeTotal;
    private TextView distanceTotal;
    private TextView fuelTotal;
    private TextView fuelByDistanceTotal;

    // Violation stats
    private TextView violations;

    private String distanceUnit = "Km";
    private String fuelUnit = "L";




    public StatsView(){
        presenter = new StatsPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fragment_stats, container, false);

        // Creates TextViews from the fragment for daily stats
        timeToday = (TextView) rootView.findViewById(R.id.timeTodayTime);
        distanceToday = (TextView) rootView.findViewById(R.id.distanceTodaydistance);
        fuelToday = (TextView) rootView.findViewById(R.id.fuelTodayFuel);
        fuelByDistanceToday = (TextView) rootView.findViewById(R.id.fuelByKmTodayFuel);

        // Creates TextViews from the fragment for daily stats
        timeTotal = (TextView) rootView.findViewById(R.id.timeTotalTime);
        distanceTotal = (TextView) rootView.findViewById(R.id.distanceTotaldistance);
        fuelTotal = (TextView) rootView.findViewById(R.id.fuelTotalFuel);
        fuelByDistanceTotal = (TextView) rootView.findViewById(R.id.fuelByKmTotalFuel);

        // Creates TextViews from the fragment for violation stats
        this.violations = (TextView) rootView.findViewById(R.id.numberOfViolations);

        // Restores preferences for settings in presenter
        presenter.restorePreferences();


        return rootView;
    }

    public void updateUnits(String system) {
        if(system.equals("imperial")) {
            fuelUnit = "Gallon";
            distanceUnit = "Miles";

        } else {
            fuelUnit = "L";
            distanceUnit = "Km";
        }

    }


    /**
     * Update the switches with settings loaded from shared preference file
     * @param statsToday stats for today
     * @param statsTotal stats for all-time
     * @param violations violations all-time
     */
    public void update(double[] statsToday, double[] statsTotal, int violations) {
        // Sets todays stats
        timeToday.setText(Math.floor(statsToday[0]*100)/100 + " min");
        distanceToday.setText(Math.floor(statsToday[1]*100)/100 + " " + distanceUnit);
        fuelToday.setText(Math.floor(statsToday[2]*100)/100 + " " + fuelUnit);
        fuelByDistanceToday.setText(Math.floor(statsToday[3]*100)/100 + " " + fuelUnit + "/" + distanceUnit);

        // Sets all.time stats
        timeTotal.setText(Math.floor(statsTotal[0]*100)/100 + " min");
        distanceTotal.setText(Math.floor(statsTotal[1]*100)/100 + " " + distanceUnit);
        fuelTotal.setText(Math.floor(statsTotal[2]*100)/100 + " " + fuelUnit);
        fuelByDistanceTotal.setText(Math.floor(statsTotal[3]*100)/100 + " " + fuelUnit + "/" + distanceUnit);

       /* fuelToday = Math.floor(fuelToday * 100)/100;
        fuelTotal = Math.floor(fuelTotal * 100)/100;
        distanceToday = Math.floor(distanceToday * 100)/100;
        distanceTotal = Math.floor(distanceTotal * 100)/100;
        fuelByDistanceToday = Math.floor(fuelByDistanceToday * 100)/100;
        fuelByDistanceTotal = Math.floor(fuelByDistanceTotal * 100)/100;*/


        // Sets violations
        this.violations.setText("" + violations);
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

        }
    }
}
