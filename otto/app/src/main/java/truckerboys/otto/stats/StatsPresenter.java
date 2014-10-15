package truckerboys.otto.stats;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.automotiveapi.AutomotiveSignalId;

import com.swedspot.automotiveapi.AutomotiveManager;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.util.ArrayList;

import truckerboys.otto.IView;
import truckerboys.otto.driver.Session;
import truckerboys.otto.driver.SessionHistory;
import truckerboys.otto.driver.SessionType;
import truckerboys.otto.driver.User;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.RestorePreferencesEvent;
import truckerboys.otto.utils.eventhandler.events.SettingsChangedEvent;
import truckerboys.otto.utils.eventhandler.events.TimeDrivenEvent;
import truckerboys.otto.vehicle.IVehicleListener;
import truckerboys.otto.vehicle.VehicleSignalID;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class StatsPresenter implements IView, IEventListener, IVehicleListener {
    private StatsModel model;

    private StatsView view;
    private String distanceUnit = "km";
    private String fuelUnit = "L";

    public static final String STATS = "Stats_file";
    public static final String SETTINGS = "Settings_file";

    public StatsPresenter(){
        this.view = new StatsView();
        this.model = new StatsModel();

        System.out.println("CERATED***********");
        EventTruck.getInstance().subscribe(this);
    }

    /**
     * Method for restoring the saved preferences from
     * the user statistics
     */
    public void restorePreferences() {
        SharedPreferences stats = view.getActivity().getSharedPreferences(STATS, 0);

        // Gets today stats
        double timeToday = stats.getFloat("timeToday", 0);
        // double distanceToday = stats.getFloat("distanceToday", 5); // Implement later
        // double fuelToday = stats.getFloat("fuelToday", 5);
        double distanceByFuel = stats.getFloat("distanceByFuel", 0);

        // Gets total stats
        double timeTotal = stats.getFloat("timeTotal", 0);
        double distanceTotal = stats.getFloat("distanceTotal", 0);
        double fuelTotal = stats.getFloat("fuelTotal", 0);
        // double fuelByDistanceTotal = stats.getFloat("fuelByDistanceTotal", 5);

        // Gets total stats
        int violation = stats.getInt("violation", 0);

 //       double[] statsToday = {timeToday, distanceToday, fuelToday, fuelByDistanceToday};
 //       double[] statsTotal = {timeTotal, distanceTotal, fuelTotal, fuelByDistanceTotal};

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

       // double[] statsToday = {model.getTimeToday(), model.getDistanceToday(), model.getFuelToday(), model.getfuelByDistanceToday()};
       // double[] statsTotal = {model.getTimeTotal(), model.getDistanceTotal(), model.getFuelTotal(), model.getfuelByDistanceTotal()};


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
        // TODO Set stuff

        SessionHistory userHistory = User.getInstance().getHistory();

        //userHistory.getSessions().get(0).getStartTime();

        // Adds dummy session history
        userHistory.addSession(new Session(SessionType.DRIVING, new Instant(Instant.now())));
        userHistory.addSession(new Session(SessionType.RESTING, new Instant(Instant.now())));
        userHistory.addSession(new Session(SessionType.WORKING, new Instant(Instant.now())));
        userHistory.addSession(new Session(SessionType.RESTING, new Instant(Instant.now())));
        userHistory.addSession(new Session(SessionType.WORKING, new Instant(Instant.now())));

        String sessionString = "";


        // Update statsview with the new session string
        view.updateSessionHistory(sessionString);

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

                System.out.println("SENT TO VIEW: " + sessionString + "**********");
            }


        }


        /*ArrayList<String> history1, history2, history3, history4, history5;
        history1 = new ArrayList<String>(4);
        history2 = new ArrayList<String>(4);
        history3 = new ArrayList<String>(4);
        history4 = new ArrayList<String>(4);
        history5 = new ArrayList<String>(4);

        int n = 0;

        if(userHistory.getSessions().get(0).isActive()) {
            n++;
        }

        // Adds history data of latest sessions if exists
        if(n != userHistory.getSessions().size() && userHistory.getSessions().get(n) != null) {


            history1.add(0, userHistory.getDateOfSession(n).getYear()
                    + "-" + userHistory.getDateOfSession(n).getMonthOfYear()
                    + "-" + userHistory.getDateOfSession(n).getDayOfMonth()); // Date from userHistory
            history1.add(1, userHistory.getSessions().get(n).getDuration().getStandardHours() + "h, "
                    + userHistory.getSessions().get(n).getDuration().getStandardMinutes() + " min"); // Time from userHistory
            history1.add(2, ""); // Violations from userHistory
            history1.add(3, userHistory.getSessions().get(n).getSessionType().toString()); // Type from userHistory

            n++;
        }


        // Adds history data of sessions if exists
        if(n != userHistory.getSessions().size() && userHistory.getSessions().get(n) != null) {
            history2.add(0, userHistory.getDateOfSession(n).getYear()
                    + "-" + userHistory.getDateOfSession(n).getMonthOfYear()
                    + "-" + userHistory.getDateOfSession(n).getDayOfMonth()); // Date from userHistory
            history2.add(1, userHistory.getSessions().get(n).getDuration().getStandardHours() + "h, "
                    + userHistory.getSessions().get(n).getDuration().getStandardMinutes() + " min"); // Time from userHistory
            history2.add(2, ""); // Violations from userHistory
            history2.add(3, userHistory.getSessions().get(n).getSessionType().toString()); // Type from userHistory

            n++;
        }

        if(n != userHistory.getSessions().size() && userHistory.getSessions().get(n) != null) {
            history3.add(0, userHistory.getDateOfSession(n).getYear()
                    + "-" + userHistory.getDateOfSession(n).getMonthOfYear()
                    + "-" + userHistory.getDateOfSession(n).getDayOfMonth()); // Date from userHistory
            history3.add(1, userHistory.getSessions().get(n).getDuration().getStandardHours() + "h, "
                    + userHistory.getSessions().get(n).getDuration().getStandardMinutes() + " min"); // Time from userHistory
            history3.add(2, ""); // Violations from userHistory
            history3.add(3, userHistory.getSessions().get(n).getSessionType().toString()); // Type from userHistory

            n++;
        }

        if(n != userHistory.getSessions().size() && userHistory.getSessions().get(n) != null) {
            history4.add(0, userHistory.getDateOfSession(n).getYear()
                    + "-" + userHistory.getDateOfSession(n).getMonthOfYear()
                    + "-" + userHistory.getDateOfSession(n).getDayOfMonth()); // Date from userHistory
            history4.add(1, userHistory.getSessions().get(n).getDuration().getStandardHours() + "h, "
                    + userHistory.getSessions().get(n).getDuration().getStandardMinutes() + " min"); // Time from userHistory
            history4.add(2, ""); // Violations from userHistory
            history4.add(3, userHistory.getSessions().get(n).getSessionType().toString()); // Type from userHistory

            n++;
        }

        if(n != userHistory.getSessions().size() && userHistory.getSessions().get(n) != null) {
            history5.add(0, userHistory.getDateOfSession(n).getYear()
                    + "-" + userHistory.getDateOfSession(n).getMonthOfYear()
                    + "-" + userHistory.getDateOfSession(n).getDayOfMonth()); // Date from userHistory
            history5.add(1, userHistory.getSessions().get(n).getDuration().getStandardHours() + "h, "
                    + userHistory.getSessions().get(n).getDuration().getStandardMinutes() + " min"); // Time from userHistory
            history5.add(2, ""); // Violations from userHistory
            history5.add(3, userHistory.getSessions().get(n).getSessionType().toString()); // Type from userHistory

            n++;
        }

        model.setUserHistory(history1, history2, history3, history4, history5);
        view.setUserHistory(history1, history2, history3, history4, history5);*/
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

    @Override
    public void receive(AutomotiveSignal signal) {

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
