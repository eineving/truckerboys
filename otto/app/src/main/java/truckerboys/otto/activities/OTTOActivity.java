package truckerboys.otto.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.R;
import truckerboys.otto.clock.ClockPresenter;
import truckerboys.otto.directionsAPI.GoogleDirections;
import truckerboys.otto.driver.TachographHandler;
import truckerboys.otto.driver.User;
import truckerboys.otto.home.ActiveSessionDialogFragment;
import truckerboys.otto.home.HomePresenter;
import truckerboys.otto.maps.MapPresenter;
import truckerboys.otto.placesAPI.GooglePlaces;
import truckerboys.otto.planner.EURegulationHandler;
import truckerboys.otto.planner.IRegulationHandler;
import truckerboys.otto.planner.TripPlanner;
import truckerboys.otto.settings.SettingsView;
import truckerboys.otto.stats.StatsPresenter;
import truckerboys.otto.utils.IPresenter;
import truckerboys.otto.utils.LocationHandler;
import truckerboys.otto.utils.eventhandler.EventBuss;
import truckerboys.otto.utils.eventhandler.EventType;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.RouteRequestEvent;
import truckerboys.otto.utils.eventhandler.events.SetChosenStopEvent;
import truckerboys.otto.utils.eventhandler.events.YesClickedEvent;
import truckerboys.otto.utils.tabs.SlidingTabLayout;
import truckerboys.otto.utils.tabs.TabPagerAdapter;

/**
 * The root class of the program.
 * Created by Martin on 29/09/2014.
 */
public class OTTOActivity extends FragmentActivity implements IEventListener, ActiveSessionDialogFragment.ActiveSessionDialogFragmentListener{
    private TripPlanner tripPlanner;
    private IRegulationHandler regulationHandler;

    private User user;

    //These may seem unused but they need to be created somewhere
    private TachographHandler tachographHandler;
    private LocationHandler locationHandler;

    private List<IPresenter> presenters = new ArrayList<IPresenter>();
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

        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), getPresenters());

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(2);

        /*
         * Make sure we never have to reload any of the tabs after the app has been started.
         * This makes the users interaction experience with the app alot smoother.
         */
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount() - 1);

        //Use Googles 'SlidingTabLayout' to display tabs for all presenters.
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab_slider);
        slidingTabLayout.setAdapter(pagerAdapter, getPresenters());
        slidingTabLayout.setViewPager(viewPager);

        // Turns display properties (alive on/off) based on saved settings file
        if (getSharedPreferences(SETTINGS, 0).getBoolean("displayAlive", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        EventBuss.getInstance().subscribe(this, EventType.ROUTE, EventType.CLOCK);
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
        user = new User(this);
        tripPlanner = new TripPlanner(regulationHandler, new GoogleDirections(), new GooglePlaces(), user);

        tachographHandler = new TachographHandler(user);
        locationHandler = new LocationHandler(this);
    }

    /**
     * Creates all relevant presenters that we need to have tabs for.
     */
    private void createPresenters() {
        presenters.add(new MapPresenter(tripPlanner));

        presenters.add(new ClockPresenter(tripPlanner, regulationHandler, user));

        presenters.add(new HomePresenter(regulationHandler, user));

        presenters.add(new StatsPresenter(user));

        presenters.add(new SettingsView());
    }

    public List<IPresenter> getPresenters() {
        return presenters;
    }

    @Override
    public void performEvent(Event event) {
        // Sets the current page to Map if a new destination is set
        if (event.isType(RouteRequestEvent.class) || event.isType(SetChosenStopEvent.class)) {
            viewPager.setCurrentItem(0);
        }
    }

    /**
     * Handles clicks from thr ActiveSessionDialog
     * and redirects them to the HomePresenter
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EventBuss.getInstance().newEvent(new YesClickedEvent());
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        ;
    }

    @Override
    public void onBackPressed() {
        // Go to home on back pressed.
        viewPager.setCurrentItem(2);
    }
}
