package truckerboys.otto.stats;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.swedspot.scs.data.SCSFloat;
import android.swedspot.scs.data.SCSLong;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Instant;


import truckerboys.otto.IView;
import truckerboys.otto.driver.Session;
import truckerboys.otto.driver.SessionHistory;
import truckerboys.otto.driver.SessionType;
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

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Class for handling most logic for the StatsView showing
 * usefull statistics for the user.
 */
public class StatsPresenter implements IView, IEventListener, IVehicleListener {
    private StatsModel model;

    private StatsView view;
    private String distanceUnit = "km";
    private String fuelUnit = "L";

    public static final String STATS = "Stats_file";
    public static final String SETTINGS = "Settings_file";

    private User user;

    public StatsPresenter(User user){
        this.user = user;
        this.view = new StatsView();
        this.view.setUser(user);
        this.model = new StatsModel();

        // Subscribes to the signals wanted
        Log.w("SIGNAL", "SUBSCRIBED");
        VehicleInterface.subscribe(this, VehicleSignalID.KM_PER_LITER,VehicleSignalID.TOTAL_VEHICLE_DISTANCE);

        EventTruck.getInstance().subscribe(this);
        EventTruck.getInstance().subscribe(view);
    }

    /**
     * Method for restoring the saved preferences from
     * the user statistics
     */
    public void restorePreferences() {
        SharedPreferences stats = view.getActivity().getSharedPreferences(STATS, 0);

        // Gets today stats
        double timeToday = stats.getFloat("timeToday", 0);
        double distanceByFuel = stats.getFloat("distanceByFuel", 0);

        // Gets total stats
        double timeTotal = stats.getFloat("timeTotal", 0);
        double distanceTotal = stats.getFloat("distanceTotal", 0);
        double fuelTotal = stats.getFloat("fuelTotal", 0);

        // Gets total stats
        int violation = stats.getInt("violation", 0);

        double[] statsToday = {timeToday, 0, 0, distanceByFuel};
        double[] statsTotal = {timeTotal, distanceTotal, fuelTotal, 0};

        setStats(statsToday, statsTotal, violation);

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
     * Method for setting the units in the model.
     * Calls this method when changing between units by:
     * presenter.setUnits("imperial").
     * @param system metric/imperial - defaults to metric.
     */
    public void setUnits(String system) {
        // TODO implement when better switching between units has been set
        // model.setUnits(system);
        // view.updateUnits(system);


        double[] statsToday = {model.getTimeToday(), 0, 0, 0};
        double[] statsTotal = {model.getTimeTotal(), model.getDistanceTotal(), model.getFuelTotal(), 0};

        updateView(statsToday, statsTotal, model.getViolations());
        // TODO: Sets view fuel, distance etc based on new values in model
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

        // Adds dummy session history
        userHistory.addSession(new Session(SessionType.DRIVING, new Instant(Instant.now().minus(2000000000)), new Instant(Instant.now().minus(1500000000)) ));
        userHistory.addSession(new Session(SessionType.RESTING, new Instant(Instant.now().minus(1000000000)), new Instant(Instant.now().minus(500000000))));
        userHistory.addSession(new Session(SessionType.WORKING, new Instant(Instant.now().minus(5000000)), new Instant(Instant.now())));

        // Ends them
        for(Session session : userHistory.getSessions()){
            session.end();
        }

        String sessionString = "";

        // Update statsview with the new session string
        for(Session session : userHistory.getSessions()) {

            // If the session isn't active
            if(!session.isActive()) {
                sessionString = new DateTime(session.getStartTime()).getYear()
                        + "-" + new DateTime(session.getStartTime()).getMonthOfYear()
                        + "-" + new DateTime(session.getStartTime()).getDayOfMonth()
                        + ": " + session.getSessionType().toString()
                        + " for " + session.getDuration().getStandardHours()
                        + "h " + session.getDuration().getStandardMinutes() + "min";


                // Update statsview with the new session string
                view.updateSessionHistory(sessionString);

            }

        }

        model.updateSessionHistory(sessionString);
    }

    @Override
    public void performEvent(Event event) {

        if(event.isType(RestorePreferencesEvent.class)) {
            restorePreferences();

            loadUserHistory();
        }

        // If the new time has been set in the model
        if(event.isType(TimeDrivenEvent.class)) {

            double[] statsToday = {((TimeDrivenEvent)event).getTimeToday(), 0, model.getFuelToday(), model.getDistanceByFuel()};
            double[] statsTotal = {((TimeDrivenEvent)event).getTimeTotal(), model.getDistanceTotal(), model.getFuelTotal(), model.getfuelByDistanceTotal()};

            // Update the time in the view
            model.setStats(statsToday, statsTotal, model.getViolations());
        }

        if(event.isType(SettingsChangedEvent.class)) {

            // read from file and set String called system based on that

            setUnits(((SettingsChangedEvent)event).getSystem());

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
        switch (signal.getSignalId()) {

            case VehicleSignalID.KM_PER_LITER:


                // Gets the total distance by fuel and updates the listeners
                Float kmPerLiter = ((SCSFloat) signal.getData()).getFloatValue();

                EventTruck.getInstance().newEvent(new DistanceByFuelEvent(Math.floor(kmPerLiter * 100)/100));

                Log.w("SIGNAL", "FUEL");
                break;

            case VehicleSignalID.TOTAL_VEHICLE_DISTANCE:

                // Gets the total distance and updates the listeners
                long distance = ((SCSLong) signal.getData()).getLongValue();

                EventTruck.getInstance().newEvent(new TotalDistanceEvent(distance));

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
