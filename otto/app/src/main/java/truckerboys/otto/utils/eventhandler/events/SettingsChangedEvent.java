package truckerboys.otto.utils.eventhandler.events;

/**
 * Created by root on 2014-10-02.
 */
public class SettingsChangedEvent extends Event {
    private static String system = "metric";

    public SettingsChangedEvent() {

    }

    public SettingsChangedEvent(String system) {
        this.system = system;
    }

    public String getSystem() {
        return system;
    }


}
