package truckerboys.otto.vehicle;

import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.scs.data.SCSFloat;
import android.util.Log;

/**
 * Created by Martin on 15/10/2014.
 */
public class FuelTankInfo implements IVehicleListener {

    private int fuelTankVolume;
    private float fuelLevel;


    // good enough default value??
    private float fuelConsumption = 5f;

    public FuelTankInfo(int fuelTankVolume) {
        this.fuelTankVolume = fuelTankVolume;
        this.fuelLevel = fuelTankVolume;
        VehicleInterface.subscribe(this, VehicleSignalID.KM_PER_LITER, VehicleSignalID.FUEL_LEVEL_1);
    }


    /**
     * Returns the current mileage, eg. number of km to empty tank.
     * @return KM to empty tank.
     */
    public int getMileage(){
        return (int)(fuelLevel * ( fuelConsumption > 0.0f ?  fuelConsumption : 5.0f));
    }

    @Override
    public void receive(AutomotiveSignal signal) {
        switch (signal.getSignalId()){
            case VehicleSignalID.KM_PER_LITER:
                fuelConsumption = ((SCSFloat) signal.getData()).getFloatValue();
                break;

            case VehicleSignalID.FUEL_LEVEL_1:
                float tanklvl = ((SCSFloat) signal.getData()).getFloatValue();
                fuelLevel = fuelTankVolume * (tanklvl / 100.0f);
                break;
        }
        Log.w("FUEL", "Mileage :"  + getMileage());
    }
}
