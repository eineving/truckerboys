package truckerboys.otto.stats;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.scs.data.SCSFloat;
import android.swedspot.scs.data.SCSLong;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;


import truckerboys.otto.IView;
import truckerboys.otto.driver.Session;
import truckerboys.otto.driver.SessionHistory;
import truckerboys.otto.driver.User;
import truckerboys.otto.utils.eventhandler.EventBuss;
import truckerboys.otto.utils.eventhandler.EventType;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.DistanceByFuelEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.RestorePreferencesEvent;
import truckerboys.otto.utils.eventhandler.events.StatsViewStoppedEvent;
import truckerboys.otto.utils.eventhandler.events.TimeDrivenEvent;
import truckerboys.otto.utils.eventhandler.events.TotalDistanceEvent;
import truckerboys.otto.vehicle.IVehicleListener;
import truckerboys.otto.vehicle.VehicleInterface;
import truckerboys.otto.vehicle.VehicleSignalID;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Class for handling most logic for the StatsView showing
 * usefull statistics for the user.
 */
public class StatsPresenter implements IView, IEventListener, IVehicleListener {
    private StatsModel model;

    private StatsView view;

    public static final String STATS = "Stats_file";

    //region Runnables
    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable statsUpdater = new Runnable() {
        public void run() {

            restorePreferences(view.getActivity().getSharedPreferences(STATS, 0));
            loadUserHistory();
            updateHandler.postDelayed(statsUpdater, 600000); // Updates each 10min
        }
    };

    private User user;

    public StatsPresenter(User user){
        this.user = user;
        this.view = new StatsView();
        this.view.setUser(user);
        this.model = new StatsModel();

        // Subscribes to the signals wanted
        Log.w("SIGNAL", "SUBSCRIBED");
        VehicleInterface.subscribe(this, VehicleSignalID.KM_PER_LITER,VehicleSignalID.TOTAL_VEHICLE_DISTANCE);


        EventBuss.getInstance().subscribe(this, EventType.STATISTICS);
        EventBuss.getInstance().subscribe(view, EventType.STATISTICS);
    }

    public StatsView getView() {
        return view;
    }

    public void setView(StatsView view) {
        this.view = view;
    }

    public StatsModel getModel() {
        return model;
    }

    /**
     * Method for restoring the saved preferences from
     * the user statistics
     */
    public void restorePreferences(SharedPreferences stats) {

        // Gets the time driven today and time driven total
        double[] time = loadTime();

        // Gets today stats
        float distanceByFuel = stats.getFloat("distanceByFuel", 0);

        // Gets total stats
        float distanceTotal = stats.getFloat("distanceTotal", 0);
        float fuelTotal = stats.getFloat("fuelTotal", 0);

        // Gets total stats
        int violation = stats.getInt("violation", 0);

        double[] statsToday = {time[0], 0, 0, (double)distanceByFuel};
        double[] statsTotal = {time[1], (double)distanceTotal, (double)fuelTotal, 0};

        setStats(statsToday, statsTotal, violation);

    }

    /**
     * Loads the time driven (today and total) from the user history.
     */
    public double[] loadTime() {
        Duration durationToday;
        Duration durationTotal;

        double[] time = new double[2];

        if(user.getHistory() != null) {

            // Time driven today
            durationToday = user.getHistory().getActiveTimeSinceLastDailyBreak();

            // Total time driven since start
            durationTotal = user.getHistory().getActiveTimeSince(new Instant(0));

            double timeDrivenToday = durationToday.getStandardMinutes()/60;
            double timeDrivenTotal = durationTotal.getStandardMinutes()/60;

            time[0] = timeDrivenToday;
            time[1] = timeDrivenTotal;

        }

        return time;
    }

    /**
     * Method for setting the stored stats in the
     * model and updating the view
     * @param statsToday stats for today
     * @param statsTotal stats for all-time
     */
    public void setStats(double[] statsToday, double[] statsTotal, int violations) {
        // Sets the stats in the model

        model.setStats(statsToday, statsTotal, violations);

        // Updates the view with stats
        updateView(statsToday, statsTotal, violations);
    }


    /**
     * Method for updating the view with corrects stats
     * @param statsToday stats for today
     * @param statsTotal stats for all-time
     * @param violations violations all-time
     */
    public void updateView(double[] statsToday, double[] statsTotal, int violations) {
        view.update(statsToday, statsTotal, violations);
    }


    /**
     * Loads session history from user database.
     */
    public void loadUserHistory() {

        SessionHistory userHistory = user.getHistory();

        String sessionString = "";

        view.clearSessionAdapter();


        // Update statsview with the new session string
        for(Session session : userHistory.getSessions()) {

            // If the session isn't active
            if(!session.isActive()) {
                sessionString = new DateTime(session.getStartTime()).getYear()
                        + "-" + new DateTime(session.getStartTime()).getMonthOfYear()
                        + "-" + new DateTime(session.getStartTime()).getDayOfMonth()
                        + ": " + session.getSessionType().toString()
                        + " for " + session.getDuration().getStandardHours()
                        + "h " + session.getDuration().minus(session.getDuration().getStandardHours() * 3600000).getStandardMinutes() + "min";


                // Update statsview with the new session string
                //TODO Fix this nullpointer
                view.updateSessionHistory(sessionString);

            }


            model.updateSessionHistory(sessionString);
        }

    }

    /**
     * Method for saving the current stats
     * if the StatsView has stopped.
     */
    public void saveCurrentStats(SharedPreferences sharedPreferences) {
        //SharedPreferences.Editor editor = view.getActivity().getSharedPreferences(STATS, 0).edit();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat("distanceTotal", (float)model.getDistanceTotal());
        editor.putFloat("fuelTotal", (float)model.getFuelTotal());
        editor.putFloat("distanceByFuel", (float)model.getDistanceByFuel());

        editor.apply();
    }

    @Override
    public void performEvent(Event event) {

        // If the view has stopped grab stats data from model and store them in a shared pref. file
        if(event.isType(StatsViewStoppedEvent.class)) {
            saveCurrentStats(view.getActivity().getSharedPreferences(STATS,0));
        }

        // Starts the update loop (10min interval)
        if(event.isType(RestorePreferencesEvent.class)) {
            statsUpdater.run();
        }

        // If the new time has been set in the model
        if(event.isType(TimeDrivenEvent.class)) {

            double[] statsToday = {((TimeDrivenEvent)event).getTimeToday(), 0, model.getFuelToday(), model.getDistanceByFuel()};
            double[] statsTotal = {((TimeDrivenEvent)event).getTimeTotal(), model.getDistanceTotal(), model.getFuelTotal(), model.getfuelByDistanceTotal()};

            // Update the time in the view
            model.setStats(statsToday, statsTotal, model.getViolations());
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

        SharedPreferences.Editor statsEditor = view.getActivity().getSharedPreferences(STATS, 0).edit();

        switch (signal.getSignalId()) {

            case VehicleSignalID.KM_PER_LITER:

                // Gets the total distance by fuel and updates the listeners
                Float kmPerLiter = ((SCSFloat) signal.getData()).getFloatValue();

                EventBuss.getInstance().newEvent(new DistanceByFuelEvent(Math.floor(kmPerLiter * 100)/100));
                statsEditor.putFloat("distanceByFuel", kmPerLiter);

                statsEditor.apply();

                Log.w("SIGNAL", "FUEL");
                break;

            case VehicleSignalID.TOTAL_VEHICLE_DISTANCE:

                // Gets the total distance and updates the listeners
                long distance = ((SCSLong) signal.getData()).getLongValue();

                EventBuss.getInstance().newEvent(new TotalDistanceEvent(distance));

                statsEditor.putFloat("distanceTotal", distance);
                statsEditor.apply();

                Log.w("SIGNAL", "DISTANCE");
                break;
        }

    }

    @Override
    public Fragment getFragment() {
        return view;
    }

    @Override
    public String getName() {
        return "Statistics";
    }
}
