package truckerboys.otto;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.LinkedList;
import java.util.List;

import truckerboys.otto.clock.ClockView;
import truckerboys.otto.home.HomeView;
import truckerboys.otto.maps.MapView;
import truckerboys.otto.settings.SettingsView;
import truckerboys.otto.stats.StatsView;

/**
 * Created by Simon Petersson on 2014-09-18.
 *
 * A FragmentAdapter that is used by the ViewPager to define what fragments
 * we want to be able to display in the "Slideview". This means that each 'page'
 * that it is possible to slide to is infact a fragment, not an activity.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    //List with all views that are needed in the "main" view. (The one with tabs.)
    private List<FragmentView> viewList = new LinkedList<FragmentView>();

    private final int numTabs = 5;

    public TabPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
        initializeAllViews();
    }

    /**
     * Adds all fragments to the viewList so that the adapter
     * can retrieve what views we want to have in the ViewPager.
     */
    private void initializeAllViews(){
        viewList.add(new ClockView());
        viewList.add(new MapView());
        viewList.add(new HomeView());
        viewList.add(new StatsView());
        viewList.add(new SettingsView());
    }

    @Override
    public Fragment getItem(int index){
        return viewList.get(index);
    }

    @Override
    public int getCount(){
        return numTabs;
    }

    @Override
    public CharSequence getPageTitle(int index){
        return viewList.get(index).getPageTitle();
    }
}
