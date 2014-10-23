package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.eventhandler.EventType;
import truckerboys.otto.utils.positions.RouteLocation;

/**
 * Created by Simon on 2014-10-17.
 */
public class SetChosenStopEvent extends Event {

    private RouteLocation stop;

    public SetChosenStopEvent(RouteLocation stop){
        super(EventType.CLOCK);
        this.stop = stop;
    }

    public RouteLocation getStop(){
        return stop;
    }

}
