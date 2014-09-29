package truckerboys.otto.vehicle;

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


    /**
     * Subscribes the listener to the specified signals.
     * @param listener The listener.
     * @param signalIds The signals to subscribe to.
     */
    public static void subscribe(IVehicleListener listener, int... signalIds) {
        vehicleDispatcher.subscribe(listener, signalIds);
        manager.register(signalIds);
    }

    /**
     * Unsubscribes a listener from the specified vehicle signals.
     * @param listener The listener.
     * @param signalIds The collection of signals.
     */
    public static void unsubscribe(IVehicleListener listener, int... signalIds) {
        vehicleDispatcher.unsubscribe(listener, signalIds);
    }

    /**
     * Subscribes a collection of listeners to the distraction level.
     * @param listeners
     */
    public static void subscribeToDistractionChange(IDistractionListener... listeners) {
        distractionDispatcher.addSubscribers(listeners);
    }

    /**
     * Unsubscribes the provided listeners from listening to the distraction level.
     * @param listeners The listeners to unsubscribe.
     */
    public static void unsubscribeFromDistractionChange(IDistractionListener... listeners) {
        distractionDispatcher.removeSubscribers(listeners);
    }

    /**
     * Checks if a Signal is available on the bus.
     * @param signalID The signal to check for availability.
     * @return true if the signal is available.
     */
    public static boolean isSignalAvailable(int signalID){
       return manager.isSignalAvailable(signalID);
    }

}
