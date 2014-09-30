package truckerboys.otto;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import truckerboys.otto.utils.tabs.SlidingTabLayout;
import truckerboys.otto.utils.tabs.TabPagerAdapter;

public class MainActivity extends FragmentActivity {
    private ViewPager viewPager;
    private TabPagerAdapter pagerAdapter;
    private IPresenter homePresenter, mapPresenter, clockPresenter, settingsPresenter, statsPresenter;
    private Map<Fragment, String> viewMap = new LinkedHashMap<Fragment, String>();
    private List<IPresenter> presenterList = new LinkedList<IPresenter>();


    public static final String SETTINGS = "Settings_file";
    public static final String STATS = "Stats_file";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makePresenters();
        createViewList();

        //Create standard view with a ViewPager and corresponding tabs.
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), viewMap);
        viewPager.setAdapter(pagerAdapter);


        //Use Googles 'SlidingTabLayout' to display tabs for all views.
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab_slider);
        slidingTabLayout.setViewPager(viewPager);


        // Restore preferences from last session
        // restorePreferences();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    private void makePresenters() {

        homePresenter = new HomePresenter(new HomeView(), new HomeModel());
        presenterList.add(homePresenter);

        mapPresenter = new MapPresenter(new MapView(), new MapModel());
        presenterList.add(mapPresenter);

        clockPresenter = new ClockPresenter(new ClockView(), new ClockModel(null, null));
        presenterList.add(clockPresenter);

        // This coding is extremely hard coupled and creates cyclic dependencies
        settingsPresenter = new SettingsPresenter(new SettingsView(), new SettingsModel());
        ((SettingsView)settingsPresenter.getView()).setPresenter((SettingsPresenter)settingsPresenter);
        presenterList.add(settingsPresenter);

        statsPresenter = new StatsPresenter(new StatsView(), new StatsModel());
        ((StatsView)statsPresenter.getView()).setPresenter((StatsPresenter)statsPresenter);
        presenterList.add(statsPresenter);
    }

    private void createViewList() {
        // debug view
        viewMap.put(new DebugView(), "Debug");

        viewMap.put(homePresenter.getView(), "Home");
        viewMap.put(mapPresenter.getView(), "Map");
        viewMap.put(clockPresenter.getView(), "Clock");
        viewMap.put(settingsPresenter.getView(), "Settings");
        viewMap.put(statsPresenter.getView(), "Stats");
    }

}
