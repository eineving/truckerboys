package truckerboys.otto.settings;

import truckerboys.otto.driver.User;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class SettingsModel {
    private User user;
    private boolean sound;
    private boolean displayAlive;

    public void setSettings(boolean sound, boolean displayAlive) {
        this.sound = sound;
        this.displayAlive = displayAlive;

    }

    public boolean getSound() {
        return sound;
    }

    public boolean getDisplayAlive() {
        return displayAlive;
    }

}
