package truckerboys.otto.home;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import truckerboys.otto.IPresenter;
import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class HomePresenter {
    private HomeModel model;

    public HomePresenter(){
        this.model = new HomeModel();
    }

}
