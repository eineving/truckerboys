package truckerboys.otto.clock;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;

import truckerboys.otto.utils.IPresenter;
import truckerboys.otto.driver.User;
import truckerboys.otto.planner.IRegulationHandler;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.eventhandler.EventBuss;
import truckerboys.otto.utils.eventhandler.EventType;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.ChangedRouteEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.SetChosenStopEvent;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * The presenter class of the clock that acts as a bridge between the view and model.
 */
public class ClockPresenter  implements IPresenter, IEventListener {
    private ClockModel model;
    private ClockView view;

    private Handler updateHandler;
    private Runnable update;

    public ClockPresenter(TripPlanner tripPlanner, IRegulationHandler regulationHandler, User user){
        model = new ClockModel(tripPlanner, regulationHandler, user);
        view = new ClockView();
        view.setOnBreak(model.isOnBreak());

        EventBuss.getInstance().subscribe(this, EventType.ROUTE, EventType.CLOCK);

        updateHandler = new Handler(Looper.getMainLooper());
        update = new Runnable(){
            public void run(){
                updateTL();
            }
        };
        updateHandler.postDelayed(update, 1000);

    }
    /**
     * Updates the model and view with time left.
     */
    public void updateTL() {
        if(model.getRoute()==null) {
            model.updateTL();
            view.setTimeLeft(model.getTimeLeft());
            view.updateUI();
            view.setOnBreak(model.isOnBreak());
        }
        updateHandler.postDelayed(update, 5000);
    }

    @Override
    public Fragment getFragment() {
        return view;
    }

    @Override
    public String getName() {
        return "Clock";
    }

    @Override
    public void performEvent(Event event) {
        if(event.isType(ChangedRouteEvent.class)){
            model.processChangedRoute();
            if(model.getRoute()!=null){
                updateHandler.removeCallbacks(update);
            }
            view.setRecommendedStop(model.getRecommendedStop());
            view.setAltStops(model.getAltStops());
            view.setNextDestination(model.getNextDestination(), model.isNextDestinationFinal());
            view.setTimeLeft(model.getTimeLeft());
            view.updateUI();
        }

        if(event.isType(SetChosenStopEvent.class)){
            model.setChosenStop(((SetChosenStopEvent) event).getStop());
        }
    }
}
