package truckerboys.otto.clock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import truckerboys.otto.R;
import truckerboys.otto.planner.TimeLeft;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 * <p/>
 * The viewclass for the clock that handles the UI.
 * It also runs the timer for the clock.
 */
public class ClockView extends Fragment {
    View rootView;
    TextView timeLeft, timeLeftExtended, recStopETA, firstAltStopETA, secAltStopETA, recStopName, firstAltStopName, secAltStopName, recStopTitle;
    ImageView recStopImage, firstAltStopImage, secAltStopImage;
    RelativeLayout recStopClick, firstAltStopClick, secAltStopClick;

    RestStop recStop, firstAltStop, secAltStop;

    Boolean variablesSet = false;
    String timeL, timeLE, timeLEPrefix = "Extended time: ";

    public ClockView() {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_clock, container, false);

        initiateVariables();

        return rootView;
    }

    /**
     * Initiates the UI components of the view.
     */
    private void initiateVariables() {
        timeLeft = (TextView) rootView.findViewById(R.id.clockETA);
        timeLeft.setText("04:22");
        timeLeftExtended = (TextView) rootView.findViewById(R.id.clockETAExtended);

        recStopTitle = (TextView) rootView.findViewById(R.id.recStopTitle);

        recStopETA = (TextView) rootView.findViewById(R.id.recStopETA);
        firstAltStopETA = (TextView) rootView.findViewById(R.id.firstAltStopETA);
        secAltStopETA = (TextView) rootView.findViewById(R.id.secAltStopETA);

        recStopName = (TextView) rootView.findViewById(R.id.recStopName);
        firstAltStopName = (TextView) rootView.findViewById(R.id.firstAltStopName);
        secAltStopName = (TextView) rootView.findViewById(R.id.secAltStopName);

        recStopImage = (ImageView) rootView.findViewById(R.id.recStopImage);
        firstAltStopImage = (ImageView) rootView.findViewById(R.id.firstAltStopImage);
        secAltStopImage = (ImageView) rootView.findViewById(R.id.secAltStopImage);

        recStopClick = (RelativeLayout) rootView.findViewById(R.id.recStop);
        firstAltStopClick = (RelativeLayout) rootView.findViewById(R.id.firstAltStop);
        secAltStopClick = (RelativeLayout) rootView.findViewById(R.id.secAltStop);

        View.OnClickListener stopClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = ((RelativeLayout)view).getTag().toString();
                //TODO: Add sending events
                if(tag.equalsIgnoreCase("recStop")){
                    recStopTitle.setText("Chosen stop");
                }
                if(tag.equalsIgnoreCase("firstAltStop")){
                    recStopTitle.setText("Chosen stop");
                    RestStop temp = recStop;
                    recStop = firstAltStop;
                    firstAltStop = temp;
                }
                if(tag.equalsIgnoreCase("secAltStop")){
                    recStopTitle.setText("Chosen stop");
                    RestStop temp = recStop;
                    recStop = secAltStop;
                    secAltStop = temp;
                }
            }
        };
        recStopClick.setOnClickListener(stopClickListener);
        firstAltStopClick.setOnClickListener(stopClickListener);
        secAltStopClick.setOnClickListener(stopClickListener);

        variablesSet = true;
    }

    /**
     * Set the time remaining until regulations are broken.
     *
     * @param timeLeft The remaining time
     */
    public void setTimeLeft(TimeLeft timeLeft) {
        if (variablesSet) {
            timeL = getTimeAsFormattedString(timeLeft.getTimeLeft());
            timeLE = timeLEPrefix + getTimeAsFormattedString(timeLeft.getTimeLeft().plus(timeLeft.getExtendedTimeLeft()));
        }
    }

    /**
     * Sets the recommended reststop
     *
     * @param reststop The new recommended reststop
     */
    public void setRecommendedRestStop(RestStop reststop) {
        recStop = reststop;
    }

    /**
     * Sets the alternative reststops to the given reststops
     *
     * @param firstAltReststop The first alternative reststop
     * @param secAltReststop   The second alternative reststop
     */
    public void setAltRestStops(RestStop firstAltReststop, RestStop secAltReststop) {
        this.firstAltStop = firstAltReststop;
        this.secAltStop = secAltReststop;
    }

    /**
     * Updates the UI
     */
    public void updateUI() {

        if (variablesSet) {
            Runnable updateUI = new Runnable() {
                public void run() {
                    setLabels();
                }
            };
            getActivity().runOnUiThread(updateUI);
        }
    }

    /**
     * Sets the labels of the reststops.
     */
    private void setLabels() {
        try {
            timeLeft.setText(timeL);
            timeLeftExtended.setText(timeLE);
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
        }

        recStopETA.setText(getTimeAsFormattedString(recStop.getTimeLeft()));
        recStopName.setText(recStop.getName());

        firstAltStopETA.setText(getTimeAsFormattedString(firstAltStop.getTimeLeft()));
        firstAltStopName.setText(firstAltStop.getName());

        secAltStopETA.setText(getTimeAsFormattedString(secAltStop.getTimeLeft()));
        secAltStopName.setText(secAltStop.getName());

    }

    /**
     * Takes a duration and returns a formatted string as such "HH:MM"
     *
     * @param time The duration to format.
     * @return A formatted string in the form of "HH:MM"
     */
    private String getTimeAsFormattedString(Duration time) {

        Period period = time.toPeriod();
        PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendHours()
                .appendSeparator(":")
                .appendMinutes()
                .toFormatter();
        return minutesAndSeconds.print(period);
    }

}
