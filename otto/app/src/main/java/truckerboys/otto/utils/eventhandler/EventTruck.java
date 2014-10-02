package truckerboys.otto.utils.eventhandler;

import java.util.LinkedList;
import java.util.List;

import truckerboys.otto.utils.eventhandler.events.Event;

/**
 * Created by Simon Petersson on 2014-10-02.
 *
 * A class that represents an EventBuss, that takes events and sends it out to all subscribers.
 * Making it possible for anyone to react to events that anyone fires.
 */
public class EventTruck {
    private static EventTruck eventTruck;
    private List<IEventListener> eventListeners = new LinkedList<IEventListener>();

    private EventTruck(){ }

    public static EventTruck getInstance(){
        if(eventTruck == null) {
            eventTruck = new EventTruck();
        }
        return eventTruck;
    }

    /**
     * Add a subscriber to the EventListener, this subscriber will then be notified when
     * the EventBuss has an Event that has not been taken care of.
     * @param eventListener The new subscriber
     * @return True if subscriber was added to current subscribers.
     */
    public boolean subscribe(IEventListener eventListener) {
        if(eventListener != null)
            return eventListeners.add(eventListener);
        else
            return false;
    }

    /**
     * Add a new event to the Eventbuss that is to be pushed out to all subscribers.
     * @param event The event that is pushed out to all subscribers
     * @return
     */
    public boolean newEvent(Event event){
        if(event == null){ //Make sure someone doesn't try to execute an invalid event.
            return false;
        }
        for(IEventListener eventListener : this.eventListeners){
            //Send out event to all subscribers
            eventListener.performEvent(event);
        }
        return this.eventListeners.remove(event);
    }
}
