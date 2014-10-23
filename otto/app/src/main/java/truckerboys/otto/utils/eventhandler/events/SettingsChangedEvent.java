package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.eventhandler.EventType;

/**
 * Created by root on 2014-10-02.
 */
public class SettingsChangedEvent extends Event {

    public SettingsChangedEvent() {
        super(EventType.SETTINGS);
    }
}
