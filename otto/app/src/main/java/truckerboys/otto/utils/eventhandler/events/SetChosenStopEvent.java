package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.positions.MapLocation;

/**
 * Created by Simon on 2014-10-17.
 */
public class SetChosenStopEvent extends Event {

    private MapLocation stop;

    public SetChosenStopEvent(MapLocation stop){
        this.stop = stop;
    }

    public MapLocation getStop(){
        return stop;
    }

}
