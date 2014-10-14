package truckerboys.otto.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import truckerboys.otto.R;
import truckerboys.otto.newroute.RouteActivity;
import utils.IView;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class HomeView extends Fragment implements IView {
    private View rootView;
    private HomePresenter presenter;
    private View newRouteButton;
    private View continueRouteButton;
    private View mapsButton;
    private View clockButton;
    private View statsButton;
    private View settingsButton;

    public HomeView(){
        presenter = new HomePresenter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Defines the buttons to be clicked on at the home screen
        defineButtons();

        return rootView;
    }

    public void defineButtons() {
        newRouteButton = rootView.findViewById(R.id.newRouteButton);
        continueRouteButton = rootView.findViewById(R.id.continueRouteButton);
        mapsButton = rootView.findViewById(R.id.mapsButton);
        clockButton = rootView.findViewById(R.id.clockButton);
        statsButton = rootView.findViewById(R.id.statsButton);
        settingsButton = rootView.findViewById(R.id.settingsButton);

        asignListeners();
    }

    /**
     * Asigns listeners to the buttons and passes the actions to the presenter.
     */
    public void asignListeners() {

        newRouteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //((ViewPager)getActivity().findViewById(R.id.pager)).setCurrentItem(1);
                Intent newRouteIntent = new Intent(getActivity(), RouteActivity.class);
                getActivity().startActivity(newRouteIntent);
            }
        });

        continueRouteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //presenter.contiueRouteButtonClicked(v);
                ((ViewPager)getActivity().findViewById(R.id.pager)).setCurrentItem(0);
            }
        });

        mapsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //presenter.contiueRouteButtonClicked(v);
                ((ViewPager)getActivity().findViewById(R.id.pager)).setCurrentItem(0);
            }
        });

        clockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //presenter.contiueRouteButtonClicked(v);
                ((ViewPager)getActivity().findViewById(R.id.pager)).setCurrentItem(1);
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //presenter.contiueRouteButtonClicked(v);
                ((ViewPager)getActivity().findViewById(R.id.pager)).setCurrentItem(3);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //presenter.contiueRouteButtonClicked(v);
                ((ViewPager)getActivity().findViewById(R.id.pager)).setCurrentItem(4);
            }
        });

    }


    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public String getName() {
        return "Home";
    }
}
