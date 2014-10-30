package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by Mikael Malmqvist on 2014-10-05.
 */
public class DisplayChangedEvent extends Event {
    private static boolean displayAlive;

    public DisplayChangedEvent(boolean displayAlive) {
        super(EventType.SETTINGS);
        this.displayAlive = displayAlive;
    }

    public boolean getDisplayAlive() {
        return displayAlive;
    }
}
