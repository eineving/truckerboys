package truckerboys.otto.home;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import truckerboys.otto.IPresenter;
import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class HomePresenter implements IPresenter {
    private HomeModel model;
    private HomeView view;

    public HomePresenter(HomeView view, HomeModel model){
        this.view = view;
        this.model = model;
    }

    /**
     * Method for when New Route is clicked
     * @param v
     */
    public void newRouteButtonClicked(View v) {
        // TODO: redirect to new route screen

    }

    /**
     * Method for when Continue Route is clicked
     * @param v
     */
    public void contiueRouteButtonClicked(View v) {
        ((ViewPager) view.getActivity().findViewById(R.id.pager)).setCurrentItem(2);

    }

    /**
     * Method for when Map is clicked
     * @param v
     */
    public void mapButtonClicked(View v) {
        ((ViewPager) view.getActivity().findViewById(R.id.pager)).setCurrentItem(2);

    }

    /**
     * Method for when Stats is clicked
     * @param v
     */
    public void statsButtonClicked(View v) {
        ((ViewPager) view.getActivity().findViewById(R.id.pager)).setCurrentItem(5);

    }

    /**
     * Method for when Settings is clicked
     * @param v
     */
    public void settingsButtonClicked(View v) {
        ((ViewPager) view.getActivity().findViewById(R.id.pager)).setCurrentItem(4);

    }

    /**
     * Method for when Clock is clicked
     * @param v
     */
    public void clockButtonClicked(View v) {
        ((ViewPager) view.getActivity().findViewById(R.id.pager)).setCurrentItem(3);

    }

    @Override
    public Fragment getView() {
        return view;
    }

}
