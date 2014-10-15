package truckerboys.otto;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.clock.ClockPresenter;
import truckerboys.otto.directionsAPI.GoogleDirections;
import truckerboys.otto.driver.TachographHandler;
import truckerboys.otto.driver.User;
import truckerboys.otto.home.HomeView;
import truckerboys.otto.maps.MapPresenter;
import truckerboys.otto.placesAPI.GooglePlaces;
import truckerboys.otto.planner.EURegulationHandler;
import truckerboys.otto.planner.IRegulationHandler;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.settings.SettingsView;
import truckerboys.otto.stats.StatsPresenter;
import truckerboys.otto.stats.StatsView;
import truckerboys.otto.utils.LocationHandler;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.RouteRequestEvent;
import truckerboys.otto.utils.tabs.SlidingTabLayout;
import truckerboys.otto.utils.tabs.TabPagerAdapter;

/**
 * The root class of the program.
 * Created by Martin on 29/09/2014.
 */
public class OTTOActivity extends FragmentActivity implements IEventListener{
    private TripPlanner tripPlanner;
    private IRegulationHandler regulationHandler;

    private User user;
    private TachographHandler tachographHandler;
    private LocationHandler locationHandler;

    private List<IView> views = new ArrayList<IView>();
    private ViewPager viewPager;
    private TabPagerAdapter pagerAdapter;

    private static OTTOActivity ottoActivity;

    public static final String SETTINGS = "Settings_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set XML for this view.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Make singleton of this activity, making it possible to close it from another activity.
        ottoActivity = this;

        initiateOTTO();
        createPresenters();

        //Create standard view with a ViewPager and corresponding tabs.
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), getViews());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(2);

        /*
         * Make sure we never have to reload any of the tabs after the app has been started.
         * This makes the users interaction experience with the app alot smoother.
         */
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount() - 1);

        //Use Googles 'SlidingTabLayout' to display tabs for all views.
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab_slider);
        slidingTabLayout.setViewPager(viewPager);

        // Turns display properties (alive on/off) based on saved settings file
        if (getSharedPreferences(SETTINGS, 0).getBoolean("displayAlive", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        EventTruck.getInstance().subscribe(this);
    }

    @Override
    public void finish() {
        super.finish();
        ottoActivity = null;
    }

    /**
     * Initiates the regulations handler, tripplanner, user, tacograph handler and location handler.
     */
    private void initiateOTTO(){
        regulationHandler = new EURegulationHandler();
        user = User.getInstance();
        tripPlanner = new TripPlanner(regulationHandler, new GoogleDirections(), new GooglePlaces(), user);

        tachographHandler = new TachographHandler(user);
        locationHandler = new LocationHandler(this);
    }

    /**
     * Creates all relevant presenters that we need to have tabs for.
     */
    private void createPresenters() {
        views.add(new MapPresenter(tripPlanner));

        views.add(new ClockPresenter());

        views.add(new HomeView());

        views.add(new StatsPresenter());

        views.add(new SettingsView());
    }

    public List<IView> getViews() {
        return views;
    }

    @Override
    public void performEvent(Event event) {
        // Sets the current page to Map if a new destination is set
        if (event.isType(RouteRequestEvent.class)) {
            viewPager.setCurrentItem(0);
        }
    }
}
