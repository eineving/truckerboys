package truckerboys.otto.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


import org.joda.time.Duration;
import org.joda.time.Instant;

import truckerboys.otto.IView;
import truckerboys.otto.driver.CurrentlyNotOnRestException;
import truckerboys.otto.driver.Session;
import truckerboys.otto.driver.SessionType;
import truckerboys.otto.driver.User;
import truckerboys.otto.newroute.RouteActivity;
import truckerboys.otto.planner.EURegulationHandler;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.NewRouteClickedEvent;
import truckerboys.otto.utils.eventhandler.events.YesClickedEvent;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * This class handles logic for the HomeView.
 */
public class HomePresenter implements IView, IEventListener {

    private EURegulationHandler handler;
    private HomeModel model;
    private HomeView view;
    private ActiveSessionDialogFragment dialog;

    public HomePresenter(EURegulationHandler handler) {
        this.model = new HomeModel();
        this.view = new HomeView();

        this.handler = handler;



        EventTruck.getInstance().subscribe(this);
    }


    /**
     * Method to run when user has clicked new route.
     * Checks if there's an active session running
     * and displays a dialog about this.
     */
    public void newRouteClicked() {


        User.getInstance().getHistory().addSession(new Session(SessionType.RESTING, new Instant(Instant.now())));

        // If there's any sessions stored
        if (User.getInstance().getHistory().getSessions().size() > 0) {

            // If the latest session is DRIVING show a dialog
            if (User.getInstance().getHistory().getSessions().get
                    (User.getInstance().getHistory().getSessions().size() - 1).getSessionType()
                    == SessionType.RESTING){

                try {
                    // If the user can't drive because this would violate a time regulation show a dialog
                    if(handler.getTimeLeftOnBreak(User.getInstance().getHistory()).getTimeLeft().isLongerThan(Duration.ZERO)){

                        // Sends time left on break
                        dialog = new ActiveSessionDialogFragment().newInstance(
                                handler.getTimeLeftOnBreak(User.getInstance().getHistory()).getTimeLeft().getMillis());
                        dialog.onAttach(view.getActivity());


                        dialog.show(view.getActivity().getFragmentManager(), "DriverBreak");
                    }
                } catch (CurrentlyNotOnRestException e) { // Else 

                    // Enter new route
                    Intent newRouteIntent = new Intent(view.getActivity(), RouteActivity.class);
                    view.getActivity().startActivity(newRouteIntent);
                }



            } else { // If the latest session isn't active

                // Enter new route
                Intent newRouteIntent = new Intent(view.getActivity(), RouteActivity.class);
                view.getActivity().startActivity(newRouteIntent);

            }
        } else { // If there's no sessions stored

            // Enter new route
            Intent newRouteIntent = new Intent(view.getActivity(), RouteActivity.class);
            view.getActivity().startActivity(newRouteIntent);
        }
    }


    @Override
    public void performEvent(Event event) {
        // If new route has been clicked in HomeView
        if (event.isType(NewRouteClickedEvent.class)) {
            newRouteClicked();
        }

        // If user clicks yes, in the "session-is-active"-dialog
        if (event.isType(YesClickedEvent.class)) {

            // Should we ends current session ?
            // User.getInstance().getHistory().getSessions().get(User.getInstance().getHistory().getSessions().size()-1).end();

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
