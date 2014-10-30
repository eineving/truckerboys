package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by Simon Petersson on 2014-10-02.
 *
 * An event that is possible to send to all subscribers of the EventBuss.
 */
public abstract class Event {
    private EventType eventType;

    public Event(EventType eventType){
        this.eventType = eventType;
    }

    /**
     * Used to compare diffferent subtypes of event.
     * @param event
     * @return
     */
    public boolean isType(Class<? extends Event> event){
        if(event.isInstance(this)){
            return true;
        }
        return false;
    }

    public EventType getEventType() {
        return eventType;
    }
}
