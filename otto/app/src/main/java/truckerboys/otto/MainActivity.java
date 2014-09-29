package truckerboys.otto;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.google.android.gms.maps.GoogleMap;

import java.util.LinkedList;
import java.util.List;

import truckerboys.otto.clock.ClockModel;
import truckerboys.otto.clock.ClockPresenter;
import truckerboys.otto.clock.ClockView;
import truckerboys.otto.home.HomeModel;
import truckerboys.otto.home.HomePresenter;
import truckerboys.otto.home.HomeView;
import truckerboys.otto.maps.MapModel;
import truckerboys.otto.maps.MapPresenter;
import truckerboys.otto.maps.MapView;
import truckerboys.otto.settings.SettingsModel;
import truckerboys.otto.settings.SettingsPresenter;
import truckerboys.otto.settings.SettingsView;
import truckerboys.otto.stats.StatsModel;
import truckerboys.otto.stats.StatsPresenter;
import truckerboys.otto.stats.StatsView;
import truckerboys.otto.utils.tabs.TabPagerAdapter;
import truckerboys.otto.utils.tabs.SlidingTabLayout;

public class MainActivity extends FragmentActivity {
    private ViewPager viewPager;
    private TabPagerAdapter pagerAdapter;
    private IPresenter homePresenter, mapPresenter, clockPresenter, settingsPresenter, statsPresenter;
    private List<FragmentView> viewList = new LinkedList<FragmentView>();
    private List<IPresenter> presenterList = new LinkedList<IPresenter>();
    private boolean mute;
    private boolean dislayAlive;

    public static final String SETTINGS = "Settings_file";
    public static final String STATS = "Stats_file";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makePresenters();

        //Create standard view with a ViewPager and corresponding tabs.
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), viewList);
        viewPager.setAdapter(pagerAdapter);



        //Use Googles 'SlidingTabLayout' to display tabs for all views.
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab_slider);
        slidingTabLayout.setViewPager(viewPager);


        // Restore preferences from last session
        restorePreferences();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Writes preferences to the settings and stats file
        SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putBoolean("mute", mute);
        settingsEditor.putBoolean("displayAlive", dislayAlive);

        // Commit the changes
        settingsEditor.commit();
    }

    private void makePresenters() {

        homePresenter = new HomePresenter(new HomeView(), new HomeModel());
        presenterList.add(homePresenter);

        mapPresenter = new MapPresenter(new MapView(), new MapModel());
        presenterList.add(mapPresenter);

        clockPresenter = new ClockPresenter(new ClockView(), new ClockModel());
        presenterList.add(clockPresenter);

        settingsPresenter = new SettingsPresenter(new SettingsView(), new SettingsModel());
        presenterList.add(settingsPresenter);

        statsPresenter = new StatsPresenter(new StatsView(), new StatsModel());
        presenterList.add(statsPresenter);

        createViewList();
    }

    private void createViewList() {

        // debug view
        viewList.add(new DebugView());

        for(IPresenter presenter : presenterList) {
            viewList.add(presenter.getView());
        }



    }

    /**
     * Method for restoring the saved preferences from
     * the user statistics and the user settings
     */
    private void restorePreferences() {
        SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
        SharedPreferences stats = getSharedPreferences(SETTINGS, 0);
        boolean mute = settings.getBoolean("mute", false); // false is the value to be returned if no "mute"-value exists
        boolean displayAlive = settings.getBoolean("displayAlive", true); // true is the value to be returned if no "displayAlive"-value exists

        setMute(mute);
        setDisplayAlive(displayAlive);
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public void setDisplayAlive(boolean displayAlive) {
       this.dislayAlive = displayAlive;
    }
}
