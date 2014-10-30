package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by Mikael Malmqvist on 2014-10-05.
 */
public class SoundChangedEvent extends Event {
    private static boolean sound;

    public SoundChangedEvent(boolean sound) {
        super(EventType.SETTINGS);
        this.sound = sound;
    }

    public boolean getSound() {
        return sound;
    }
}
