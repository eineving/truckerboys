package truckerboys.otto.stats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import truckerboys.otto.R;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import utils.IView;



/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Class for displaying statistics for the user.
 */

public class StatsView extends Fragment implements IView, IEventListener{

    private View rootView;
    private StatsPresenter presenter;

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
        timeToday.setText(statsToday[0] + " min");
        distanceToday.setText(statsToday[1] + " " + distanceUnit);
        fuelToday.setText(statsToday[2] + " " + fuelUnit);
        fuelByDistanceToday.setText(statsToday[3] + " " + fuelUnit + "/" + distanceUnit);

        // Sets all.time stats
        timeTotal.setText(statsTotal[0] + " min");
        distanceTotal.setText(statsTotal[1] + " " + distanceUnit);
        fuelTotal.setText(statsTotal[2] + " " + fuelUnit);
        fuelByDistanceTotal.setText(statsTotal[3] + " " + fuelUnit + "/" + distanceUnit);

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
        //setUnits(view.getActivity().getSharedPreferences(SETTINGS, 0).getString("system", "metric"));
    }
}
