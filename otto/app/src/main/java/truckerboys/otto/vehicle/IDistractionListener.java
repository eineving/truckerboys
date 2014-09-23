package truckerboys.otto.vehicle;

import com.swedspot.vil.distraction.DriverDistractionLevel;

/**
 * Created by Martin on 22/09/2014.
 */
public interface IDistractionListener {

    public void distractionLevelChanged(DriverDistractionLevel driverDistractionLevel);

}