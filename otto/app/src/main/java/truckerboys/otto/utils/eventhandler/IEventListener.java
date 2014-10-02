package truckerboys.otto.utils.eventhandler;

import truckerboys.otto.utils.eventhandler.events.Event;

/**
 * Created by Simon Petersson on 2014-10-02.
 *
 * Interface that defines the implementer to be someone that can subscribe to the EventBuss.
 */
public interface IEventListener {
    public void performEvent(Event event);
}
