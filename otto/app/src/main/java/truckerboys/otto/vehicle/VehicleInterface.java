package truckerboys.otto.vehicle;

import android.swedspot.automotiveapi.AutomotiveSignalId;

import com.swedspot.automotiveapi.AutomotiveFactory;
import com.swedspot.automotiveapi.AutomotiveManager;
import com.swedspot.vil.policy.AutomotiveCertificate;

/**
 * The applications connection to the vehicle.
 * Created by Martin on 21/09/2014.
 */
public class VehicleInterface {

    private final static VehicleSignalDispatcher vehicleDispatcher = new VehicleSignalDispatcher();
    private final static DistractionSignalDispatcher distractionDispatcher = new DistractionSignalDispatcher();
    //Automotive certificate? how?
    private final static AutomotiveManager manager = AutomotiveFactory.createAutomotiveManagerInstance(new AutomotiveCertificate(new byte[0]), vehicleDispatcher, distractionDispatcher);

    public static void subscribe(IVehicleListener listener, int... signalIds) {
        vehicleDispatcher.subscribe(listener, signalIds);
        manager.register(signalIds);
    }

    public static void unsubscribe(IVehicleListener listener, int... signalIds) {
        vehicleDispatcher.unsubscribe(listener, signalIds);
    }

    public static void subscribeToDistractionChange(IDistractionListener... listeners) {
        distractionDispatcher.addSubscribers(listeners);
    }

    public static void unsubscribeFromDistractionChange(IDistractionListener... listeners) {
        distractionDispatcher.removeSubscribers(listeners);
    }

}
