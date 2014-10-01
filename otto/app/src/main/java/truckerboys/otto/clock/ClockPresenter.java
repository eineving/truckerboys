package truckerboys.otto.clock;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.planner.TimeLeft;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * The presenter class of the clock that acts as a bridge between the view and model.
 */
public class ClockPresenter{
    private ClockModel model;

    public ClockPresenter(){
        this.model = new ClockModel();
    }
    /**
     * Updates the model and view.
     */
    public void update() {
        model.update();
    }

    public TimeLeft getTimeLeft(){
        return model.getTimeLeft();
    }

    public ArrayList<RestStop> getRestStops(){
        return model.getRestStops();
    }
}
