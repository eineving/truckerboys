package truckerboys.otto.clock;

import truckerboys.otto.planner.TripPlanner;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class ClockModel {

    private TripPlanner tripPlanner;

    public ClockModel(TripPlanner tp){
        this.tripPlanner = tp;
    }
}
