package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by root on 2014-10-02.
 */
public class TankChangedEvent extends Event {
    private int size;

    public TankChangedEvent(int size) {
        super(EventType.SETTINGS);
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
