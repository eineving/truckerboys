package truckerboys.otto.clock;

import android.support.v4.app.Fragment;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import truckerboys.otto.IPresenter;
import truckerboys.otto.driver.User;
import truckerboys.otto.planner.IRegulationHandler;
import truckerboys.otto.planner.TripPlanner;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * The presenter class of the clock that acts as a bridge between the view and model.
 * It also runs the timer for the clock.
 */
public class ClockPresenter{
    private ClockModel model;
    private ClockView view;

    public ClockPresenter(ClockView view){
        this.view = view;
        this.model = new ClockModel();

        view.setRestStops(model.getRestStops());
        ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor)
                Executors.newScheduledThreadPool(1);

        Runnable update = new Runnable(){
            public void run(){
                update();
            }
        };
        sch.scheduleWithFixedDelay(update, 5, 5, TimeUnit.SECONDS); // The timer
    }
    /**
     * Updates the model and view.
     */
    private void update() {
        model.update();
        view.setTimeLeft(model.getTimeLeft());
        view.updateUI();
    }
}
