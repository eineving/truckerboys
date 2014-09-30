package truckerboys.otto.stats;

import android.support.v4.app.Fragment;

import truckerboys.otto.IPresenter;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class StatsPresenter implements IPresenter {
    private StatsModel model;
    private StatsView view;

    public StatsPresenter(){
        this.view = new StatsView();
        this.model = new StatsModel();
    }


    @Override
    public Fragment getView() {
        return view;
    }

    @Override
    public String getName() {
        return "Statistics";
    }

}
