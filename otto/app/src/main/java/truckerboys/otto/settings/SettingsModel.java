package truckerboys.otto.settings;

import truckerboys.otto.driver.User;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class SettingsModel {
    private User user;
    private boolean displayAlive;

    public void setSettings(boolean displayAlive) {
        this.displayAlive = displayAlive;

    }

    public boolean getDisplayAlive() {
        return displayAlive;
    }

}
