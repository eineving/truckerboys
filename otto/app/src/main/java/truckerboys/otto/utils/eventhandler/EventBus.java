package truckerboys.otto.utils.eventhandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.EventType;

/**
 * Created by Simon Petersson on 2014-10-02.
 *
 * A class that represents an EventBuss, that takes events and sends it out to all subscribers.
 * Making it possible for anyone to react to events that anyone fires.
 */
public class EventBus {
    private static EventBus eventBus;

    // We use a map to map certain listeners to specific events.
    private Map<EventType, LinkedList<IEventListener>> eventListeners = new HashMap<EventType, LinkedList<IEventListener>>();

    // We need to make sure that the eventListeners map has an entry with all specific types of event.

    private EventBus(){ }

    /**
     * Return the instance of the EventBuss.
     * @return The instance of EventBuss.
     */
    public static EventBus getInstance(){
        if(eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    /**
     * Add a subscriber to the EventListener, this subscriber will then be notified when
     * the EventBuss has an Event of the specific type that it's subscribed to is fired.
     * @param eventListener The new subscriber
     * @param types The types of events this subscriber should listen to.
     */
    public void subscribe(IEventListener eventListener, EventType ... types) {
        if(eventListener != null) {
            for (EventType type : types) {
                if(!eventListeners.containsKey(type)){
                    eventListeners.put(type, new LinkedList<IEventListener>());
                }
                eventListeners.get(type).add(eventListener);
            }
        }
    }

    /**
     * Add a new event to the Eventbuss that is to be pushed out to all subscribers.
     * @param event The event that is pushed out to all subscribers
     */
    public void newEvent(Event event){
        if(event != null && this.eventListeners.get(event.getEventType()) != null){ //Make sure someone doesn't try to execute an invalid event.
            for(IEventListener eventListener : this.eventListeners.get(event.getEventType())){
                //Send out event to all subscribers
                eventListener.performEvent(event);
            }
        }
    }
}