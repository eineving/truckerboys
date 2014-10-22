package truckerboys.otto.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import truckerboys.otto.R;
import truckerboys.otto.utils.eventhandler.EventBuss;
import truckerboys.otto.utils.eventhandler.events.NewRouteClickedEvent;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * Central home view from where the user navigates
 * to the rest of the app
 */
public class HomeView extends Fragment {
    private View rootView;
    private View newRouteButton;
    private View mapsButton;
    private View clockButton;
    private View statsButton;
    private View settingsButton;

    public HomeView(){
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

                EventBuss.getInstance().newEvent(new NewRouteClickedEvent());

            }
        });

        newRouteButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    newRouteButton.setBackgroundColor(Color.LTGRAY);

                }else {
                    newRouteButton.setBackgroundColor(Color.TRANSPARENT);

                }

                return false;
            }
        });

        mapsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Enter maps
                ((ViewPager)getActivity().findViewById(R.id.pager)).setCurrentItem(0);

            }
        });

        mapsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    mapsButton.setBackgroundColor(Color.LTGRAY);

                } else {
                    mapsButton.setBackgroundColor(Color.TRANSPARENT);

                }

                return false;
            }
        });

        clockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //presenter.contiueRouteButtonClicked(v);
                ((ViewPager)getActivity().findViewById(R.id.pager)).setCurrentItem(1);
            }
        });

        clockButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    clockButton.setBackgroundColor(Color.LTGRAY);

                }else {
                    clockButton.setBackgroundColor(Color.TRANSPARENT);

                }

                return false;
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //presenter.contiueRouteButtonClicked(v);
                ((ViewPager)getActivity().findViewById(R.id.pager)).setCurrentItem(3);
            }
        });

        statsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    statsButton.setBackgroundColor(Color.LTGRAY);

                }else {
                    statsButton.setBackgroundColor(Color.TRANSPARENT);

                }

                return false;
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //presenter.contiueRouteButtonClicked(v);
                ((ViewPager)getActivity().findViewById(R.id.pager)).setCurrentItem(4);
            }
        });

        settingsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    settingsButton.setBackgroundColor(Color.LTGRAY);

                }else {
                    settingsButton.setBackgroundColor(Color.TRANSPARENT);

                }

                return false;
            }
        });

    }


}
