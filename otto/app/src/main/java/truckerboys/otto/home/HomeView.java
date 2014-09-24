package truckerboys.otto.home;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import truckerboys.otto.FragmentView;
import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class HomeView extends FragmentView{
    private View rootView;
    private ImageButton newRouteButton;
    private ImageButton continueRouteButton;
    private ImageButton mapsButton;
    private ImageButton clockButton;
    private ImageButton statsButton;
    private ImageButton settingsButton;

    public HomeView(){
        super("Home", R.layout.fragment_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Defines the buttons to be clicked on at the home screen
        newRouteButton = (ImageButton) rootView.findViewById(R.id.newRouteButton);
        continueRouteButton = (ImageButton) rootView.findViewById(R.id.continueRouteButton);
        mapsButton = (ImageButton) rootView.findViewById(R.id.mapsButton);
        clockButton = (ImageButton) rootView.findViewById(R.id.clockButton);
        statsButton = (ImageButton) rootView.findViewById(R.id.statsButton);
        settingsButton = (ImageButton) rootView.findViewById(R.id.settingsButton);


        // Add all the buttons to an array to loops through later
        ImageButton[] buttonArray = {newRouteButton, continueRouteButton, mapsButton, clockButton, statsButton, settingsButton};


        asignListeners((AbstractImageButton[]) buttonArray);

        return rootView;
    }


    /**
     * Asigns listeners to the buttons in our array
     * and pass on the activity.
     * @param buttonArray
     */
    public void asignListeners(AbstractImageButton[] buttonArray) {
        for(ImageButton button : buttonArray) {
            ((AbstractImageButton) button).setOnClickAction(getActivity());
        }
    }


}
