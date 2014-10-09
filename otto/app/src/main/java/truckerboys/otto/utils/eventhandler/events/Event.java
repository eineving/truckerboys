package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by Simon Petersson on 2014-10-02.
 *
 * An event that is possible to send to all subscribers of the EventBuss.
 */
public abstract class Event {
    public boolean isType(Class<? extends Event> event){
        if(event.isInstance(this)){
            return true;
        }
        return false;
    }
}
