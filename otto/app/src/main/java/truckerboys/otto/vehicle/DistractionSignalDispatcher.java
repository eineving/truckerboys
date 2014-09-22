package truckerboys.otto.vehicle;

import com.swedspot.vil.distraction.DriverDistractionLevel;
import com.swedspot.vil.distraction.DriverDistractionListener;

import java.util.ArrayList;

/**
 * A dispatcher that sends notifies all subscribed IDistractionListeners.
 *
 * Created by Martin on 22/09/2014.
 */
class DistractionSignalDispatcher implements DriverDistractionListener {

    private final ArrayList<IDistractionListener> subs = new ArrayList<IDistractionListener>();

    public void addSubscribers(IDistractionListener... listeners){
        for(IDistractionListener e : listeners){
            if(!subs.contains(e)){
                subs.add(e);
            }
        }
    }

    public void removeSubscribers(IDistractionListener... listeners){
        for(IDistractionListener e : listeners){
            subs.remove(e);
        }
    }


    @Override
    public void levelChanged(DriverDistractionLevel driverDistractionLevel) {
        for(IDistractionListener e : subs){
            e.distractionLevelChanged(driverDistractionLevel);
        }
    }
}
