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

    private Duration timeLeftDuration, timeLeftExtendedDuration;
    private TimeLeft timeLeft;
    private RestStop recStop, firstAltStop, secAltStop;
    private long timeDifference;

    public ClockModel() {

        //TODO: Change to real values when Tripplanner is implemented
        lastTimeUpdate = new Instant();
        timeLeftDuration = new Duration(120 * 60 * 1000);
        timeLeftExtendedDuration = new Duration(60 * 60 * 1000);
        timeLeft = new TimeLeft(timeLeftDuration, timeLeftExtendedDuration);

        //Placeholders until TripPlanner is fully implemented
        recStop = new RestStop(new Duration(45 * 60 * 1000), "Name of first stop");
        firstAltStop = new RestStop(new Duration(110 * 60 * 1000), "Name of second stop");
        secAltStop = new RestStop(new Duration(140 * 60 * 1000), "Name of third stop");

    }


    public void update() {
        timeNow = new Instant();
        timeDifference = timeNow.getMillis() - lastTimeUpdate.getMillis();
        timeLeftDuration = timeLeftDuration.minus(timeDifference);
        timeLeftExtendedDuration = timeLeftDuration.minus(timeDifference);
        recStop.setTimeLeft(recStop.getTimeLeft().minus(timeDifference));
        firstAltStop.setTimeLeft(firstAltStop.getTimeLeft().minus(timeDifference));
        secAltStop.setTimeLeft(secAltStop.getTimeLeft().minus(timeDifference));
        lastTimeUpdate = timeNow;
    }

    private void processRestStops() {
        //TODO: Add checking for reststops with TripPlanner
    }

    public RestStop getRecommendedRestStop(){
        return recStop;
    }

    public RestStop getFirstAltReststop() {
        return firstAltStop;
    }

    public RestStop getSecondAltReststop() {
        return secAltStop;
    }

    public TimeLeft getTimeLeft() {
        return new TimeLeft(timeLeftDuration, timeLeftExtendedDuration);
    }
}
