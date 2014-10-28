package truckerboys.otto.stats;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import org.joda.time.Duration;
import org.joda.time.Instant;

import truckerboys.otto.utils.IPresenter;
import truckerboys.otto.R;
import truckerboys.otto.driver.User;
import truckerboys.otto.utils.eventhandler.EventBuss;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.DistanceByFuelEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.RestorePreferencesEvent;
import truckerboys.otto.utils.eventhandler.events.StatsViewStoppedEvent;
import truckerboys.otto.utils.eventhandler.events.TimeDrivenEvent;
import truckerboys.otto.utils.eventhandler.events.TotalDistanceEvent;


/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Class for displaying statistics for the user.
 */

public class StatsView extends Fragment implements IPresenter, IEventListener{

    private View rootView;

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

    // Updatehandler
    private Handler updateHandler = new Handler(Looper.getMainLooper());

    private User user;

    public StatsView(){
    }


    public void setUser(User user){
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fragment_stats, container, false);

        initzialiseUI();

        sessionAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_plain_text);

        historyList.setAdapter(sessionAdapter);


        // Restores preferences for settings in presenter
        EventBuss.getInstance().newEvent(new RestorePreferencesEvent());

        return rootView;
    }

    public void initzialiseUI() {
        // Creates TextViews from the fragment for daily stats
        timeToday = (TextView) rootView.findViewById(R.id.timeTodayTime);
        distanceByFuel = (TextView) rootView.findViewById(R.id.KmByFuel);

        // Session history
        historyList = (ListView) rootView.findViewById(R.id.sessionListView);

        // Creates TextViews from the fragment for daily stats
        timeTotal = (TextView) rootView.findViewById(R.id.timeTotalTime);
        distanceTotal = (TextView) rootView.findViewById(R.id.distanceTotaldistance);
        fuelTotal = (TextView) rootView.findViewById(R.id.fuelTotalFuel);

        // Creates TextViews from the fragment for violation stats
        this.violations = (TextView) rootView.findViewById(R.id.numberOfViolations);
    }

    @Override
    public void onResume() {
        super.onResume();

        Duration durationToday;
        Duration durationTotal;


        // TODO UNCOMMENT THIS
        durationToday = user.getHistory().getActiveTimeSinceLastDailyBreak();

        // TODO Change this to SessionHistory.getActiveTimeSince(Instant start)
        durationTotal = user.getHistory().getActiveTimeSince(new Instant(0));
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
        EventBuss.getInstance().newEvent(new TimeDrivenEvent(timeDrivenToday, timeDrivenTotal));
    }

    /**
     * On stop, notify the presenter through an event.
     * The presenter will furthermore save the data from the model.
     */
    @Override
    public void onStop() {
        super.onStop();

        // Notifies the presenter that the view has stopped
        EventBuss.getInstance().newEvent(new StatsViewStoppedEvent());

    }


    /**
     * Updates the list of previous sessions.
     * @param sessionString data from previous sessions.
     */
    public void updateSessionHistory(String sessionString) {

        if(sessionAdapter != null) {
            sessionAdapter.add(sessionString);
            sessionAdapter.notifyDataSetChanged();
        }



        if(historyList != null) {
            historyList.setLayoutParams(new LinearLayout.LayoutParams(
                    1000, historyList.getAdapter().getCount()*125));
        }

    }

    /**
     * Clears the session adapter for the
     * history list.
     */
    public void clearSessionAdapter() {
        if(sessionAdapter != null){
            sessionAdapter.clear();
            sessionAdapter.notifyDataSetChanged();
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
