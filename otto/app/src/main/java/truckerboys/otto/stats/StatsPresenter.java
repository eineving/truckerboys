package truckerboys.otto.stats;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import truckerboys.otto.IPresenter;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class StatsPresenter implements IPresenter {
    private StatsModel model;
    private StatsView view;
    private String distanceUnit = "km";
    private String fuelUnit = "L";

    public static final String STATS = "Stats_file";

    public StatsPresenter(StatsView view, StatsModel model){
        this.view = view;
        this.model = model;
    }

    /**
     * Method for restoring the saved preferences from
     * the user statistics
     */
    public void restorePreferences() {
        SharedPreferences stats = getView().getActivity().getSharedPreferences(STATS, 0);

        // Gets today stats
        double timeToday = stats.getFloat("timeToday", 0);
        double distanceToday = stats.getFloat("distanceToday", 0);
        double fuelToday = stats.getFloat("fuelToday", 0);
        double fuelByDistanceToday = stats.getFloat("fuelByDistanceToday", 0);

        // Gets total stats
        double timeTotal = stats.getFloat("timeTotal", 0);
        double distanceTotal = stats.getFloat("distanceTotal", 0);
        double fuelTotal = stats.getFloat("fuelTotal", 0);
        double fuelByDistanceTotal = stats.getFloat("fuelByDistanceTotal", 0);

        // Gets total stats
        int violation = stats.getInt("violation", 0);

        double[] statsToday = {timeToday, distanceToday, fuelToday, fuelByDistanceToday};
        double[] statsTotal = {timeTotal, distanceTotal, fuelTotal, fuelByDistanceTotal};

        setStats(statsToday, statsTotal,violation);
    }

    /**
     * Method for setting the stored stats in the
     * model and updating the view
     * @param statsToday stats for today
     * @param statsTotal stats for all-time
     */
    public void setStats(double[] statsToday, double[] statsTotal, int violations) {
        model.setStats(statsToday, statsTotal, violations);
        updateView(statsToday, statsTotal, violations);
    }

    /**
     * Method for setting the units in the model.
     * @param system metric/imperial - defaults to metric.
     */
    public void setUnits(String system) {
        model.setUnits(system);
        view.updateUnits(system);

        // TODO: Sets view fuel, distance etc based on new values in model
    }


    /**
     * Method for updating the view with corrects stats
     * @param statsToday stats for today
     * @param statsTotal stats for all-time
     * @param violations violations all-time
     */
    public void updateView(double[] statsToday, double[] statsTotal, int violations) {
        view.update(statsToday, statsToday, violations);
    }


    @Override
    public Fragment getView() {
        return view;
    }

}
