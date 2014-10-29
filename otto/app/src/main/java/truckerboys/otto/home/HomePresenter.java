package truckerboys.otto.home;

import android.content.Intent;
import android.support.v4.app.Fragment;


import org.joda.time.Duration;

import truckerboys.otto.utils.IPresenter;
import truckerboys.otto.driver.CurrentlyNotOnRestException;
import truckerboys.otto.driver.SessionType;
import truckerboys.otto.driver.User;
import truckerboys.otto.newroute.RouteActivity;
import truckerboys.otto.planner.IRegulationHandler;
import truckerboys.otto.utils.eventhandler.EventBus;
import truckerboys.otto.utils.eventhandler.events.EventType;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.NewRouteClickedEvent;
import truckerboys.otto.utils.eventhandler.events.YesClickedEvent;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * This class handles logic for the HomeView.
 */
public class HomePresenter implements IPresenter, IEventListener {

    private IRegulationHandler handler;
    private HomeView view;
    private User user;

    public HomePresenter(IRegulationHandler handler, User user) {
        this.view = new HomeView();
        this.user = user;
        this.handler = handler;

        EventBus.getInstance().subscribe(this, EventType.BUTTON_CLICKED);
    }


    /**
     * Method to run when user has clicked new route.
     * Checks if there's an active session running
     * and displays a dialog about this.
     */
    public void newRouteClicked() {
        // If there's any sessions stored
        if (user.getHistory().getSessions().size() > 0) {

            // If the latest session is RESTING
            if (user.getHistory().getSessions().get(0).getSessionType() == SessionType.RESTING){

                try {
                    // If the user can't drive because this would violate a time regulation show a dialog
                    if(handler.getTimeLeftOnBreak(user.getHistory()).getTimeLeft().isLongerThan(Duration.ZERO)){

                        // Sends time left on break
                        ActiveSessionDialogFragment dialog = new ActiveSessionDialogFragment().newInstance(
                                handler.getTimeLeftOnBreak(user.getHistory()).getTimeLeft().getMillis());
                        dialog.onAttach(view.getActivity());

                        dialog.show(view.getActivity().getFragmentManager(), "DriverBreak");
                    }
                } catch (CurrentlyNotOnRestException e) { // Else
                }

                launchRouteActivity();

            } else { // If the latest session isn't active
                launchRouteActivity();
            }
        } else { // If there's no sessions stored
            launchRouteActivity();
        }
    }

    /**
     * Helper method that launches the RouteActivity.
     */
    private void launchRouteActivity(){
        Intent newRouteIntent = new Intent(view.getActivity(), RouteActivity.class);
        view.getActivity().startActivity(newRouteIntent);
    }


    @Override
    public void performEvent(Event event) {
        // If new route has been clicked in HomeView
        if (event.isType(NewRouteClickedEvent.class)) {
            newRouteClicked();
        }

        // If user clicks yes, in the "session-is-active"-dialog
        if (event.isType(YesClickedEvent.class)) {
            Intent newRouteIntent = new Intent(view.getActivity(), RouteActivity.class);
            view.getActivity().startActivity(newRouteIntent);
        }
    }

    @Override
    public Fragment getFragment() {
        return view;
    }

    @Override
    public String getName() {
        return "Home";
    }
}
