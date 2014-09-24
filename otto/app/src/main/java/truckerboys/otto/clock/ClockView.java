package truckerboys.otto.clock;

import android.widget.TextView;

import truckerboys.otto.FragmentView;
import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 */
public class ClockView extends FragmentView {
    TextView timeLeft, stopTL1, stopTL2, stopTL3, stopN1, stopN2, stopN3;
    public ClockView(){
        super("Clock", R.layout.fragment_clock);
        initiateVariables();
    }

    private void initiateVariables(){
        timeLeft = (TextView) getActivity().findViewById(R.id.clockText);
        timeLeft.setText("04:22");
        stopTL1 = (TextView) getActivity().findViewById(R.id.timeStop1);
        stopTL2 = (TextView) getActivity().findViewById(R.id.timeStop2);
        stopTL3 = (TextView) getActivity().findViewById(R.id.timeStop3);
        stopN1 = (TextView) getActivity().findViewById(R.id.nameStop1);
        stopN2 = (TextView) getActivity().findViewById(R.id.nameStop2);
        stopN3 = (TextView) getActivity().findViewById(R.id.nameStop3);
    }
}
