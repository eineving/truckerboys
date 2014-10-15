package truckerboys.otto.clock;

import android.support.v4.app.Fragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import truckerboys.otto.IView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * The presenter class of the clock that acts as a bridge between the view and model.
 */
public class ClockPresenter  implements IView {
    private ClockModel model;
    private ClockView view;

    public ClockPresenter(){
        model = new ClockModel();
        view = new ClockView();

        view.setRecommendedRestStop(model.getRecommendedRestStop());
        view.setAltRestStops(model.getFirstAltReststop(), model.getSecondAltReststop());

        ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor)
                Executors.newScheduledThreadPool(1);

        Runnable update = new Runnable(){
            public void run(){
                update();
            }
        };
        sch.scheduleWithFixedDelay(update, 0, 5, TimeUnit.SECONDS); // The timer
    }
    /**
     * Updates the model and view.
     */
    public void update() {
        model.update();
        view.setTimeLeft(model.getTimeLeft());
        view.updateUI();
    }

    @Override
    public Fragment getFragment() {
        return view;
    }

    @Override
    public String getName() {
        return "Clock";
    }
}
