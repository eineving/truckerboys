package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.eventhandler.EventType;

/**
 * Created by Mikael Malmqvist on 2014-10-14.
 */
public class RestorePreferencesEvent extends Event {
    public RestorePreferencesEvent() {
        super(EventType.STATISTICS);
    }
}
