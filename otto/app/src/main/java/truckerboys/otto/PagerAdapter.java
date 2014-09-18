package truckerboys.otto;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import truckerboys.otto.home.HomeView;
import truckerboys.otto.maps.MapView;

/**
 * Created by simon on 2014-09-18.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private final int numTabs = 2;

    public PagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int index){
        switch(index) {
            case 0:
                return new HomeView();
            case 1:
                return new MapView();
        }
        return null;
    }

    @Override
    public int getCount(){
        return numTabs;
    }
}
