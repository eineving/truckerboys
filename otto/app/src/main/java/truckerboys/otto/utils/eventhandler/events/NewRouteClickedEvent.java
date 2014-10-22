package truckerboys.otto.utils.eventhandler.events;

import truckerboys.otto.utils.eventhandler.EventType;

/**
 * Created by Mikael Malmqvist on 2014-10-16.
 */
public class NewRouteClickedEvent extends Event {
    public NewRouteClickedEvent(){
        super(EventType.BUTTON_CLICKED);
    }
}
