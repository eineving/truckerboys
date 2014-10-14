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
import truckerboys.otto.utils.positions.GasStation;
import truckerboys.otto.utils.positions.MapLocation;
import truckerboys.otto.utils.positions.RestLocation;

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

    MapLocation recStop, firstAltStop, secAltStop;

    Boolean variablesSet = false, showStops = false;
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
                String tag = ((RelativeLayout) view).getTag().toString();
                //TODO: Add sending events + method for handling clickevents
                if (tag.equalsIgnoreCase("recStop")) {
                    recStopTitle.setText("Chosen stop");
                }
                if (tag.equalsIgnoreCase("firstAltStop")) {
                    recStopTitle.setText("Chosen stop");
                    MapLocation temp = recStop;
                    recStop = firstAltStop;
                    firstAltStop = temp;
                }
                if (tag.equalsIgnoreCase("secAltStop")) {
                    recStopTitle.setText("Chosen stop");
                    MapLocation temp = recStop;
                    recStop = secAltStop;
                    secAltStop = temp;
                }
            }
        };
        recStopClick.setOnClickListener(stopClickListener);
        firstAltStopClick.setOnClickListener(stopClickListener);
        secAltStopClick.setOnClickListener(stopClickListener);

        setStopUI(recStop, recStopETA, recStopName, recStopImage);
        setStopUI(firstAltStop, firstAltStopETA, firstAltStopName, firstAltStopImage);
        setStopUI(secAltStop, secAltStopETA, secAltStopName, secAltStopImage);

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
            if (timeLeft.getExtendedTimeLeft().getMillis() > 0) {
                timeLE = timeLEPrefix + getTimeAsFormattedString(timeLeft.getExtendedTimeLeft());
            } else {
                timeLE = "0";
            }
        }
    }

    /**
     * Sets the recommended stop
     *
     * @param stop The new recommended stop
     */
    public void setRecommendedStop(MapLocation stop) {
        recStop = stop;
    }

    /**
     * Sets the alternative stops to the given stops
     *
     * @param firstAltStop The first alternative stop
     * @param secAltStop   The second alternative stop
     */
    public void setAltStops(MapLocation firstAltStop, MapLocation secAltStop) {
        this.firstAltStop = firstAltStop;
        this.secAltStop = secAltStop;
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
            if(getActivity()!=null)
            getActivity().runOnUiThread(updateUI);
        }
    }

    /**
     * Sets the labels of the time until violation and the reststops.
     */
    private void setLabels() {
        timeLeft.setText(timeL);
        if (timeLE.equalsIgnoreCase("0")) {
            timeLeftExtended.setVisibility(TextView.GONE);
        } else {
            timeLeftExtended.setText(timeLE);
            timeLeftExtended.setVisibility(TextView.VISIBLE);
        }

        setStopUI(recStop, recStopETA, recStopName, recStopImage);
        setStopUI(firstAltStop, firstAltStopETA, firstAltStopName, firstAltStopImage);
        setStopUI(secAltStop, secAltStopETA, secAltStopName, secAltStopImage);

    }

    /**
     * A help method to set the UI of the stops.
     *
     * @param stop  The stop to read from
     * @param eta   The TextView representing the ETA
     * @param name  The TextView representing the name
     * @param image The ImageView with the image
     */
    private void setStopUI(MapLocation stop, TextView eta, TextView name, ImageView image) {
        if (stop == null) {
            setStopVisible(false, eta, name, image);
            return;
        }
        eta.setText(getTimeAsFormattedString(stop.getEta()));
        if (stop instanceof RestLocation) {
            name.setText(((RestLocation) stop).getName());
            image.setImageResource(R.drawable.reststop);
        } else if (stop instanceof GasStation) {
            name.setText(((GasStation) stop).getName());
            image.setImageResource(R.drawable.gasstation);
        } else {
            name.setText(stop.getAddress());
            image.setImageResource(R.drawable.reststop);
        }
    }

    /**
     * Sets the visibility of the given stop
     *
     * @param visible True if visible, false if invisible
     * @param eta     The ETA TextView
     * @param name    The name TextView
     * @param image   The image ImageView
     */
    private void setStopVisible(boolean visible, TextView eta, TextView name, ImageView image) {
        int visibility;
        if (visible) {
            visibility = TextView.VISIBLE;
        } else {
            visibility = TextView.GONE;
        }
        eta.setVisibility(visibility);
        name.setVisibility(visibility);
        image.setVisibility(visibility);

        recStopETA.setVisibility(visibility);
        recStopName.setVisibility(visibility);
        recStopImage.setVisibility(visibility);
        firstAltStopETA.setVisibility(visibility);
        firstAltStopName.setVisibility(visibility);
        firstAltStopImage.setVisibility(visibility);
        secAltStopETA.setVisibility(visibility);
        secAltStopName.setVisibility(visibility);
        secAltStopImage.setVisibility(visibility);
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
