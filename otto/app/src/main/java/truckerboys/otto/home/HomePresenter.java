package truckerboys.otto.home;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v4.app.Fragment;

import org.joda.time.Instant;

import truckerboys.otto.IView;
import truckerboys.otto.driver.Session;
import truckerboys.otto.driver.SessionType;
import truckerboys.otto.driver.User;
import truckerboys.otto.newroute.RouteActivity;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.NewRouteClickedEvent;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * This class handles logic for the HomeView.
 */
public class HomePresenter implements IView, IEventListener,
        ActiveSessionDialogFragment.ActiveSessionDialogFragmentListener{

    private HomeModel model;
    private HomeView view;
    private ActiveSessionDialogFragment dialog = new ActiveSessionDialogFragment();

    public HomePresenter(){
        this.model = new HomeModel();
        this.view = new HomeView();

        EventTruck.getInstance().subscribe(this);
    }


    /**
     * Method to run if user clicks "yes" in the ActiveSessionDialog.
     * Enters new route screen.
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Enter new route
        Intent newRouteIntent = new Intent(view.getActivity(), RouteActivity.class);
        view.getActivity().startActivity(newRouteIntent);
    }
    /**
     * Method to run if user clicks "cancel" in the ActiveSessionDialog
     * Does Nothing
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        ;
    }

    /**
     * Method to run when user has clicked new route.
     * Checks if there's an active session running
     * and displays a dialog about this.
     */
    public void newRouteClicked() {

        // Adds a dummy session that's active
        User.getInstance().getHistory().addSession(new Session(SessionType.WORKING,
                new Instant(Instant.now())));


        // If there's any sessions stored
        if(User.getInstance().getHistory().getSessions().size() > 0) {

            // If the latest session is active show a dialog
            if(User.getInstance().getHistory().getSessions().get
                    (User.getInstance().getHistory().getSessions().size() - 1).isActive()) {

                dialog.show(view.getActivity().getFragmentManager(), "Active Session");

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
        if(event.isType(NewRouteClickedEvent.class)) {
            newRouteClicked();
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
