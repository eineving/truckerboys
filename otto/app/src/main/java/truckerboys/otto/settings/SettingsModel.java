package truckerboys.otto.settings;

import truckerboys.otto.driver.User;
import truckerboys.otto.vehicle.FuelTankInfo;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class SettingsModel {
    private User user;
    private boolean displayAlive;
    private FuelTankInfo fuelTank;

    public SettingsModel(FuelTankInfo fuelTank) {
        this.fuelTank = fuelTank;
    }

    public void setSettings(boolean displayAlive) {
        this.displayAlive = displayAlive;

    }

    public void setTankSize(int tankSize) {
        fuelTank.setFuelTankVolume(tankSize);
    }

    public int getTankSize() {
        return fuelTank.getFuelTankVolume();
    }

    public boolean getDisplayAlive() {
        return displayAlive;
    }

}
