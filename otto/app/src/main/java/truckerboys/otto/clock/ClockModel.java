package truckerboys.otto.clock;

import org.joda.time.Duration;
import org.joda.time.Instant;

import truckerboys.otto.driver.User;
import truckerboys.otto.planner.IRegulationHandler;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class ClockModel {

    private User user;
    private IRegulationHandler regulations;

    private Instant violationStart, lastTimeUpdate, timeNow;

    private Duration timeLeft, tlStop1, tlStop2, tlStop3;
    private RestStop stop1, stop2, stop3;
    private long timeDifference;

    public ClockModel(User user, IRegulationHandler reg){
        this.user = user;
        this.regulations = reg;
        lastTimeUpdate = new Instant();

        //Placeholders until TripPlanner is fully implemented
        stop1 = new RestStop(new Duration(45*60*1000),"Name of first stop");
        stop2 = new RestStop(new Duration(110*60*1000),"Name of second stop");
        stop2 = new RestStop(new Duration(140*60*1000),"Name of third stop");
    }


    public void update(){
        timeNow = new Instant();
        timeDifference = timeNow.getMillis()-lastTimeUpdate.getMillis();
        timeLeft.minus(timeDifference);
        stop1.getTimeLeft().minus(timeDifference);
        stop2.getTimeLeft().minus(timeDifference);
        stop3.getTimeLeft().minus(timeDifference);
        lastTimeUpdate = timeNow;
    }

    public Duration getTimeLeft(){
        return timeLeft;

    }
}
