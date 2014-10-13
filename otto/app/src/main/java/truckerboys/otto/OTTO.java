package truckerboys.otto;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.clock.ClockPresenter;
import truckerboys.otto.clock.ClockView;
import truckerboys.otto.directionsAPI.GoogleDirections;
import truckerboys.otto.driver.TachographHandler;
import truckerboys.otto.driver.User;
import truckerboys.otto.home.HomeView;
import truckerboys.otto.maps.MapView;
import truckerboys.otto.placesAPI.GooglePlaces;
import truckerboys.otto.planner.EURegulationHandler;
import truckerboys.otto.planner.IRegulationHandler;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.settings.SettingsView;
import truckerboys.otto.stats.StatsView;
import utils.IView;

/**
 * The root class of the program.
 * Created by Martin on 29/09/2014.
 */
public class OTTO {

    private List<IView> views = new ArrayList<IView>();

    private TripPlanner tripPlanner;
    private IRegulationHandler regulationHandler;

    private User user;
    private TachographHandler tachographHandler;

    public OTTO() {
        regulationHandler = new EURegulationHandler();
        user = User.getInstance();
        tripPlanner = new TripPlanner(regulationHandler, new GoogleDirections(), new GooglePlaces(), user);

        tachographHandler = new TachographHandler(user);

        createPresenters();
    }

    private void createPresenters() {
        MapView mapView = new MapView();
        mapView.setTripPlanner(tripPlanner);
        views.add(mapView);

        views.add(new ClockPresenter());

        views.add(new HomeView());

        views.add(new StatsView());

        views.add(new SettingsView());
    }

    public TripPlanner getTripPlanner() {
        return tripPlanner;
    }

    public List<IView> getViews() {
        return views;
    }


}
