package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by Simon Petersson on 2014-10-29.
 *
 * This event is only used when testing the EventBus.
 */
public class TestEventEvent extends Event {
    public TestEventEvent(){
        super(EventType.TEST);
    }
}
