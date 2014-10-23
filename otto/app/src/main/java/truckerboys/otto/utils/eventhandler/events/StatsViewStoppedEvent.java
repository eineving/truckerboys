package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.eventhandler.EventType;

/**
 * Created by Mikael Malmqvist on 2014-10-17.
 */
public class StatsViewStoppedEvent extends Event {
    public StatsViewStoppedEvent() {
        super(EventType.STATISTICS);
    }
}
