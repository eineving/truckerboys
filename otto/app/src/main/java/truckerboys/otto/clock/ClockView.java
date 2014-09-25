package truckerboys.otto.clock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayDeque;
import java.util.Iterator;

import truckerboys.otto.FragmentView;
import truckerboys.otto.R;

/**
 * Created by Mikael Malmqvist on 2014-09-18.
 *
 * The viewclass for the clock that handles the UI.
 */
public class ClockView extends FragmentView {
    View rootView;
    TextView timeLeft, stopTL1, stopTL2, stopTL3, stopN1, stopN2, stopN3;
    ArrayDeque<RestStop> stops = new ArrayDeque<RestStop>();

    public ClockView(){
        super("Clock", R.layout.fragment_clock);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fragment_clock, container, false);

        initiateVariables();

        return rootView;
    }

    /**
     * Initiates the UI components of the view.
     */
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

    /**
     * Set the time remaining until regulations are broken.
     * @param timeLeft The remaining time
     */
    public void setTimeLeft(Duration timeLeft){
        this.timeLeft.setText(getTimeAsFormattedString(timeLeft));
    }

    /**
     * Add a new reststop to the bottom of the list. If the list is longer than three it removes the closest reststop.
     * @param newRestStop The new reststop
     */
    public void addNewRestStop(RestStop newRestStop){

        stops.addFirst(newRestStop);
        if(stops.size()>3){
            stops.removeLast();
        }

        setLabels();

    }

    /**
     * Updates the UI
     */
    public void updateUI(){
        setLabels();
    }

    /**
     * Sets the labels of the reststops.
     */
    private void setLabels(){
        Iterator it = stops.descendingIterator();
        if(it.hasNext()) {
            RestStop stop = (RestStop) it.next();
            stopTL1.setText(getTimeAsFormattedString(stop.getTimeLeft()));
            stopN1.setText(stop.getName());
        }
        if(it.hasNext()) {
            RestStop stop = (RestStop) it.next();
            stopTL2.setText(getTimeAsFormattedString(stop.getTimeLeft()));
            stopN2.setText(stop.getName());
        }
        if(it.hasNext()) {
            RestStop stop = (RestStop) it.next();
            stopTL3.setText(getTimeAsFormattedString(stop.getTimeLeft()));
            stopN3.setText(stop.getName());
        }
    }

    /**
     * Takes a duration and returns a formatted string as such "HH:MM"
     * @param time The duration to format.
     * @return A formatted string in the form of "HH:MM"
     */
    private String getTimeAsFormattedString(Duration time){

        Period period = time.toPeriod();
        PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendHours()
                .appendSeparator(":")
                .appendMinutes()
                .toFormatter();
        String result = minutesAndSeconds.print(period);

        return result;
    }
}
