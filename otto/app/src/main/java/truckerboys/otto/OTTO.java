package truckerboys.otto;

import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.clock.ClockModel;
import truckerboys.otto.clock.ClockPresenter;
import truckerboys.otto.clock.ClockView;
import truckerboys.otto.driver.User;
import truckerboys.otto.home.HomeModel;
import truckerboys.otto.home.HomePresenter;
import truckerboys.otto.home.HomeView;
import truckerboys.otto.maps.MapModel;
import truckerboys.otto.maps.MapPresenter;
import truckerboys.otto.maps.MapView;
import truckerboys.otto.planner.EURegulationHandler;
import truckerboys.otto.planner.IRegulationHandler;
import truckerboys.otto.settings.SettingsModel;
import truckerboys.otto.settings.SettingsPresenter;
import truckerboys.otto.settings.SettingsView;
import truckerboys.otto.stats.StatsModel;
import truckerboys.otto.stats.StatsPresenter;
import truckerboys.otto.stats.StatsView;

/**
 * The root class of the program.
 * Created by Martin on 29/09/2014.
 */
public class OTTO {

    private List<IPresenter> presenters = new ArrayList<IPresenter>();

    private IRegulationHandler regulationHandler;

    private User user;


    public OTTO(){

        regulationHandler = new EURegulationHandler();
        user = new User();


        createPresenters();
    }

    private void createPresenters(){
        presenters.add(new HomePresenter(new HomeView(), new HomeModel()));

        presenters.add(new MapPresenter(new MapView(), new MapModel()));

        presenters.add(new ClockPresenter(new ClockView(), new ClockModel(user, regulationHandler)));

        //TODO SIMON P Fix this since it's so retardedly FUBAR that it's not even funny.
        SettingsPresenter sp = new SettingsPresenter(new SettingsView(), new SettingsModel());
        ((SettingsView)sp.getView()).setPresenter((SettingsPresenter)sp);
        presenters.add(sp);

        presenters.add(new StatsPresenter(new StatsView(), new StatsModel()));
    }

    public List<IPresenter> getPresenters(){
        return presenters;
    }





}
