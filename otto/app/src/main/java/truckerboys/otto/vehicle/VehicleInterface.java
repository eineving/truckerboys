package truckerboys.otto.vehicle;

import com.swedspot.automotiveapi.AutomotiveFactory;
import com.swedspot.automotiveapi.AutomotiveManager;
import android.swedspot.automotiveapi.AutomotiveSignalId;


/**
 * Created by Martin on 21/09/2014.
 */
public class VehicleInterface {

    private final static MainVehicleListener vehicleListener = new MainVehicleListener();

    //Automotive certificate? how?
    private final static AutomotiveManager manager = AutomotiveFactory.createAutomotiveManagerInstance(null, vehicleListener, null);




    public static void subscribe(IVehicleListener listener, int... signalIds){
        vehicleListener.subscribe(listener, signalIds);
        manager.register(signalIds);
    }

    public static void unsubscribe(IVehicleListener listener, int... signalIds){
        vehicleListener.unsubscribe(listener, signalIds);
    }

}
