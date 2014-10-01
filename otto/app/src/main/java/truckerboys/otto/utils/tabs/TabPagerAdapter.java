package truckerboys.otto.utils.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import truckerboys.otto.IPresenter;
import utils.IView;

/**
 * Created by Simon Petersson on 2014-09-18.
 *
 * A FragmentAdapter that is used by the ViewPager to define what fragments
 * we want to be able to display in the "Slideview". This means that each 'page'
 * that it is possible to slide to is infact a fragment, not an activity.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    //List with all views that are needed in the "main" view. (The one with tabs.)
    private Map<Fragment, String> viewMap = new LinkedHashMap<Fragment, String>();

    private final int numTabs;

    public TabPagerAdapter(FragmentManager fragmentManager, List<IView> views){
        super(fragmentManager);
        //this.viewList = viewList;

        for(IView v : views){
            this.viewMap.put(v.getFragment(), v.getName());
        }


        numTabs = viewMap.size();
    }

    @Override
    public Fragment getItem(int index){
        return new ArrayList<Fragment>(viewMap.keySet()).get(index);
    }

    @Override
    public int getCount(){
        return numTabs;
    }

    @Override
    public CharSequence getPageTitle(int index){
        return new ArrayList<String>(viewMap.values()).get(index);
    }
}
