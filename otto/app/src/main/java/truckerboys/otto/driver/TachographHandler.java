package truckerboys.otto.driver;

import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.scs.data.SCSInteger;

import truckerboys.otto.vehicle.IVehicleListener;
import truckerboys.otto.vehicle.VehicleInterface;
import truckerboys.otto.vehicle.VehicleSignalID;

/**
 * A class that controls the creation of sessions based on events from the tachograph.
 * Created by Martin on 09/10/2014.
 */
public class TachographHandler implements IVehicleListener {

    private User user;

    public TachographHandler(User user){

        this.user = user;

        VehicleInterface.subscribe(this, VehicleSignalID.DRIVER_1_CARD);
        VehicleInterface.subscribe(this, VehicleSignalID.DRIVER_1_WORKING_STATE);
    }

    private void handleWorkingStateChange(int state){
        
    }



    @Override
    public void receive(AutomotiveSignal signal) {
        switch(signal.getSignalId()){
            case VehicleSignalID.DRIVER_1_CARD:


                break;

            case VehicleSignalID.DRIVER_1_WORKING_STATE:

                int x = (new SCSInteger(signal.getData().getData())).getIntValue();

                break;
        }
    }
}
