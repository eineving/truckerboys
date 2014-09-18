package truckerboys.otto.home;

import android.view.View;

import truckerboys.otto.ActivitySwapper;
import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class HomePresenter implements ActivitySwapper{
    private HomeModel model;
    private HomeView view;

    public HomePresenter(HomeView view){
        this.view = view;
    }

    @Override
    public void swapActivity(View clickedView) {

    }
}
