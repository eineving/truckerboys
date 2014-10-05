package truckerboys.otto.utils.eventhandler;

import truckerboys.otto.utils.eventhandler.events.Event;

/**
 * Created by Simon Petersson on 2014-10-02.
 *
 * Interface that defines the implementer to be someone that can subscribe to the EventBuss.
 */
public interface IEventListener {

    /**
     * Listen to what kind of event is provided and perform an action based on that.
     * @param event The event that was fired by the EventTruck.
     */
    public void performEvent(Event event);
}
