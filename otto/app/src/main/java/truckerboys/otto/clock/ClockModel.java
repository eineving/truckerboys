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

    private Instant violationStart;

    public ClockModel(User user, IRegulationHandler reg){
        this.user = user;
        this.regulations = reg;
    }


    public void update(){

    }

    public Duration getTimeLeft(){
        return new Duration(0);

    }
}
