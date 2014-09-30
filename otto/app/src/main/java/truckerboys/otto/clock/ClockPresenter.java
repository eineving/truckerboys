package truckerboys.otto.clock;

import android.support.v4.app.Fragment;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import truckerboys.otto.IPresenter;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class ClockPresenter implements IPresenter{
    private ClockModel model;
    private ClockView view;

    public ClockPresenter(ClockView view, ClockModel model){
        this.view = view;
        this.model = model;

        ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor)
                Executors.newScheduledThreadPool(1);
        Runnable update = new Runnable(){
            public void run(){
                update();
            }
        };
        sch.scheduleWithFixedDelay(update, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public Fragment getView() {
        return view;
    }

    private void update(){
        model.update();
        view.setTimeLeft(model.getTimeLeft());
        view.updateUI();
    }
}
