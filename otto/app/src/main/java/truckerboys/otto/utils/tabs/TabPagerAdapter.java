package truckerboys.otto.utils.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.utils.IPresenter;

/**
 * Created by Simon Petersson on 2014-09-18.
 * <p/>
 * A FragmentAdapter that is used by the ViewPager to define what fragments
 * we want to be able to display in the "Slideview". This means that each 'page'
 * that it is possible to slide to is in fact a fragment, not an activity.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    //List with all views that are needed in the "main" view. (The one with tabs.)
    private List<IPresenter> views = new ArrayList<IPresenter>();


    public TabPagerAdapter(FragmentManager fragmentManager, List<IPresenter> views) {
        super(fragmentManager);

        this.views.addAll(views);

    }


    public void removeItem(IPresenter item){
        views.remove(item);
        notifyDataSetChanged();
    }

    public void addItem(IPresenter item){
        views.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object item){

        int pos = -1;

        for(int i = 0; i < views.size(); i++){
            if(item == views.get(i)){
                pos = i;
                break;
            }
        }


        if(pos >= 0){
            return pos;
        }else{
            return POSITION_UNCHANGED;
        }
    }


    @Override
    public Fragment getItem(int index) {
        return views.get(index).getFragment();
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public CharSequence getPageTitle(int index) {
        return views.get(index).getName();
    }

}
