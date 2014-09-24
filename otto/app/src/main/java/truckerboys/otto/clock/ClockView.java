package truckerboys.otto.clock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.Duration;

import truckerboys.otto.FragmentView;
import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class ClockView extends FragmentView {
    View rootView;
    TextView timeLeft, stopTL1, stopTL2, stopTL3, stopN1, stopN2, stopN3;
    public ClockView(){
        super("Clock", R.layout.fragment_clock);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fragment_clock, container, false);

        initiateVariables();

        return rootView;
    }

    private void initiateVariables(){
        timeLeft = (TextView) rootView.findViewById(R.id.clockText);
        timeLeft.setText("04:22");
        stopTL1 = (TextView) rootView.findViewById(R.id.timeStop1);
        stopTL2 = (TextView) rootView.findViewById(R.id.timeStop2);
        stopTL3 = (TextView) rootView.findViewById(R.id.timeStop3);
        stopN1 = (TextView) rootView.findViewById(R.id.nameStop1);
        stopN2 = (TextView) rootView.findViewById(R.id.nameStop2);
        stopN3 = (TextView) rootView.findViewById(R.id.nameStop3);
    }

    public void setTimeLeft(Duration timeLeft){
        this.timeLeft.setText(timeLeft.toString());
    }
}
