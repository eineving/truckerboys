package truckerboys.otto.utils.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.swedspot.vil.distraction.DriverDistractionLevel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import truckerboys.otto.vehicle.IDistractionListener;
import truckerboys.otto.vehicle.VehicleInterface;
import utils.IView;

/**
 * Created by Simon Petersson on 2014-09-18.
 *
 * A FragmentAdapter that is used by the ViewPager to define what fragments
 * we want to be able to display in the "Slideview". This means that each 'page'
 * that it is possible to slide to is in fact a fragment, not an activity.
 */
public class TabPagerAdapter extends FragmentPagerAdapter implements IDistractionListener{

    //List with all views that are needed in the "main" view. (The one with tabs.)
    private Map<Fragment, String> viewMap = new LinkedHashMap<Fragment, String>();

    private final int numTabs, numTabsDistraction;
    private boolean distractionLevelHigher = false;

    public TabPagerAdapter(FragmentManager fragmentManager, List<IView> views){
        super(fragmentManager);

        for(IView v : views){
            this.viewMap.put(v.getFragment(), v.getName());
        }


        numTabs = viewMap.size();

        numTabsDistraction = 2; //Only the first two tabs are shown in driving mode, maps and clock

        VehicleInterface.subscribeToDistractionChange(this);
    }

    @Override
    public Fragment getItem(int index){
        return new ArrayList<Fragment>(viewMap.keySet()).get(index);
    }

    @Override
    public int getCount(){
        if(distractionLevelHigher){
            return numTabsDistraction;
        }

        return numTabs;
    }

    @Override
    public CharSequence getPageTitle(int index){
        return new ArrayList<String>(viewMap.values()).get(index);
    }

    /**
     * Gets called when the distraction level of the truck driver changes.
     * Only sets a boolean value that states if the app is in standstill or driving mode.
     * @param driverDistractionLevel
     */
    public void distractionLevelChanged(DriverDistractionLevel driverDistractionLevel){
        if(driverDistractionLevel.getLevel()>1){
            distractionLevelHigher = true;
        }else{
            distractionLevelHigher = false;
        }
    }
}
