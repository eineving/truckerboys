package truckerboys.otto.clock;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.ArrayList;

import truckerboys.otto.planner.TimeLeft;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * The model class for the clock that handles the logic.
 */
public class ClockModel {

    private Instant violationStart, lastTimeUpdate, timeNow;

    private Duration timeLeftDuration;
    private TimeLeft timeLeft;
    private RestStop stop1, stop2, stop3;
    private long timeDifference;
    private ArrayList<RestStop> restStops = new ArrayList<RestStop>();

    public ClockModel() {
        lastTimeUpdate = new Instant();
        timeLeftDuration = new Duration(120 * 60 * 1000);

        //Placeholders until TripPlanner is fully implemented
        stop1 = new RestStop(new Duration(45 * 60 * 1000), "Name of first stop");
        stop2 = new RestStop(new Duration(110 * 60 * 1000), "Name of second stop");
        stop3 = new RestStop(new Duration(140 * 60 * 1000), "Name of third stop");
        restStops.add(stop1);
        restStops.add(stop2);
        restStops.add(stop3);

        //TODO: Add when regulations and user is implemented
        //timeLeft = tripPlanner.getTimeleft;
    }


    public void update() {
        timeNow = new Instant();
        timeDifference = timeNow.getMillis() - lastTimeUpdate.getMillis();
        //TODO: Add when TripPlanner is implemented
        //timeLeft = timeLeft.getTimeLeft().minus(timeDifference);
        timeLeftDuration = timeLeftDuration.minus(timeDifference);
        stop1.setTimeLeft(stop1.getTimeLeft().minus(timeDifference));
        stop2.setTimeLeft(stop2.getTimeLeft().minus(timeDifference));
        stop3.setTimeLeft(stop3.getTimeLeft().minus(timeDifference));
        lastTimeUpdate = timeNow;
    }

    private void processRestStops() {
        //TODO: Add checking for reststops with TripPlanner
    }

    public ArrayList<RestStop> getRestStops() {
        return restStops;
    }

    public Duration getTimeLeft() {
        //TODO: Add when TripPlanner is implemented
        //return timeLeft.getTimeLeft();
        return timeLeftDuration;
    }
}
