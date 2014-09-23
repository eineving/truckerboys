package truckerboys.otto.vehicle;

import android.swedspot.automotiveapi.AutomotiveSignal;

import com.swedspot.automotiveapi.AutomotiveListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The central vehicle-listener that listens to all registered signals and distributes them
 * to all subsribed listeners.
 * Created by Martin on 21/09/2014.
 */
class VehicleSignalDispatcher implements AutomotiveListener {

    /// A hashmap that maps a list of subscribers to a Signal ID.
    private final HashMap<Integer, ArrayList<IVehicleListener>> subs = new HashMap<Integer, ArrayList<IVehicleListener>>();


    /**
     * Internal function that adds a subscriber to the hashmap.
     *
     * @param id       the id to subscribe to.
     * @param listener the listener.
     */
    private void addSubscriber(int id, IVehicleListener listener) {
        if (!subs.containsKey(id)) {
            subs.put(id, new ArrayList<IVehicleListener>());
        }
        if (!subs.get(id).contains(listener)) {
            subs.get(id).add(listener);
        }
    }


    /**
     * Subscribes a VehicleListener to the specified signal IDs.
     *
     * @param listener  The listener.
     * @param signalIds The list of signal IDs.
     */
    public void subscribe(IVehicleListener listener, int... signalIds) {
        for (int id : signalIds) {
            addSubscriber(id, listener);
        }
    }

    /**
     * Unsubscribes a VehicleListener from the specified signals.
     *
     * @param listener
     * @param signalIds
     */
    public void unsubscribe(IVehicleListener listener, int... signalIds) {
        for (int id : signalIds) {
            if (subs.containsKey(id)) {
                subs.get(id).remove(listener);
            }
        }
    }


    /**
     * Distributes the incoming signal to all the subscribed listeners.
     *
     * @param signal
     */
    @Override
    public void receive(AutomotiveSignal signal) {
        for (IVehicleListener listener : subs.get(signal.getSignalId())) {
            listener.receive(signal);
        }

    }

    @Override
    public void timeout(int i) {

    }

    @Override
    public void notAllowed(int i) {
        System.err.println("Not allowed called, this shouldn't happen. Code: " + i + " . Contact swedspot" );
    }
}
