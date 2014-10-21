package truckerboys.otto.clock;

import android.support.v4.app.Fragment;

import truckerboys.otto.IView;
import truckerboys.otto.driver.User;
import truckerboys.otto.planner.IRegulationHandler;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.ChangedRouteEvent;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.SetChosenStopEvent;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * The presenter class of the clock that acts as a bridge between the view and model.
 */
public class ClockPresenter  implements IView, IEventListener {
    private ClockModel model;
    private ClockView view;

    public ClockPresenter(TripPlanner tripPlanner, IRegulationHandler regulationHandler, User user){
        model = new ClockModel(tripPlanner, regulationHandler, user);
        view = new ClockView();

        EventTruck.getInstance().subscribe(this);

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
            view.setRecommendedStop(model.getRecommendedStop());
            view.setAltStops(model.getAltStops());
            view.setNextDestination(model.getNextDestination());
            view.setTimeLeft(model.getTimeLeft());
            view.updateUI();
        }

        if(event.isType(SetChosenStopEvent.class)){
            model.setChosenStop(((SetChosenStopEvent) event).getStop());
        }
    }
}
