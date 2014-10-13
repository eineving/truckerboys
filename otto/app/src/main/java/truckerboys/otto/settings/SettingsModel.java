package truckerboys.otto.settings;

import truckerboys.otto.driver.User;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class SettingsModel {
    private User user;
    private int tankSize = 200;
    private boolean displayAlive;

    public void setSettings(boolean displayAlive) {
        this.displayAlive = displayAlive;

    }

    public void setTankSize(int tankSize) {
        this.tankSize = tankSize;
    }

    public int getTankSize() {
        return tankSize;
    }

    public boolean getDisplayAlive() {
        return displayAlive;
    }

}
