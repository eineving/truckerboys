package truckerboys.otto.newroute;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import truckerboys.otto.R;
import truckerboys.otto.placeSuggestion.PlacesAutoCompleteAdapter;
import truckerboys.otto.utils.eventhandler.EventBus;
import truckerboys.otto.utils.eventhandler.events.EventType;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.RouteRequestEvent;
import truckerboys.otto.utils.eventhandler.events.RefreshHistoryEvent;

/**
 * Created by Mikael Malmqvist on 2014-10-02.
 * Activity for when selecting a new route.
 * This class can be seen as the view in the MVP pattern.
 * for when selecting a new route.
 */
public class RouteActivity extends Activity implements IEventListener {
    private RoutePresenter routePresenter;
    private RouteModel routeModel = new RouteModel();

    private ListView checkpointList;
    private ArrayList<String> checkpointsStrings;

    private AutoCompleteTextView search;
    private AutoCompleteTextView checkpoint;
    // Geocoder to use when sending a location with the eventTruck
    private Geocoder coder;
    private SharedPreferences history;

    private TextView result;
    private TextView history1Text;
    private TextView history2Text;
    private TextView history3Text;
    private TextView finalDestination;
    // private TextView finalCheckpoints;

    private TextView navigate;
    private ImageButton addButton1;
    private ImageButton addButton2;
    private ImageButton removeDestinationButton;

    private LinearLayout resultsBox;
    private LinearLayout historyBox;
    private RelativeLayout history1;
    private RelativeLayout history2;
    private RelativeLayout history3;

    private String tempLocation = "temp";

    // Dialogs
    private NoDestinationDialog dialog = new NoDestinationDialog();
    private ProgressDialog spinnerDialog;


    private Handler locationHandler = new Handler();
    InputMethodManager keyboard;


    public static final String HISTORY = "History_file";

    public RouteActivity() {

    }

    public void startSpinner() {

        if (finalDestination != null && coder != null && routePresenter != null) {
            if (finalDestination.getText() != null
                    && !finalDestination.getText().equals("")) {

                spinnerDialog = new ProgressDialog(this);
                spinnerDialog.setMessage("Loading your route");
                spinnerDialog.setTitle("Loading");
                spinnerDialog.setCancelable(true);
                spinnerDialog.show();

                handleNavigate();

            } else {

                // if no final destination is set
                dialog.show(RouteActivity.this.getFragmentManager(), "No Destination");
            }

        }


    }

    public void handleNavigate() {


        routePresenter.sendLocation("" + finalDestination.getText().toString(),
                routeModel.getCheckpoints(), coder);

        if (history != null) {
            routePresenter.saveHistory(history, ""
                    + finalDestination.getText().toString());
        }




    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.fragment_new_route);
        EventBus.getInstance().subscribe(this, EventType.ROUTE, EventType.STATISTICS);
        coder = new Geocoder(this);
        history = getSharedPreferences(HISTORY, 0);
        routePresenter = new RoutePresenter();
        keyboard = (InputMethodManager) getSystemService(
                this.INPUT_METHOD_SERVICE);

        // Sets the UI components
        initzialiseUI();

        // Loads destination history
        routePresenter.loadHistory(history);


        // Sets the adapter to our PlacesAutoCompleteAdapter which uses the TripPlanner class
        // for getting the results from the Places API
        search.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1));
        checkpoint.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1));

        // Sets arraylist for checkpoints
        checkpointsStrings = new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.text_list_item,
                checkpointsStrings);

        checkpointList.setAdapter(adapter);


        // Sets listeners to UI components

        // Removes checkpoint
        checkpointList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (routeModel.getCheckpoints().contains(((TextView) view).getText())) {
                    routeModel.getCheckpoints().remove(((TextView) view).getText());
                    adapter.remove(((TextView) view).getText());
                    adapter.notifyDataSetChanged();

                    checkpointList.setLayoutParams(new LinearLayout.LayoutParams(
                            checkpointList.getWidth(), 150 * checkpointsStrings.size()));
                }
            }
        });



        // Handles when user selects an item from the drop-down menu
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                addButton1.setVisibility(View.VISIBLE);

                // Hides keyboard
                keyboard.hideSoftInputFromWindow(search.getWindowToken(), 0);

            }
        });

        // Handles when user selects an item from the drop-down menu
        checkpoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                addButton2.setVisibility(View.VISIBLE);
                checkpoint.clearFocus();

                // Hides keyboard
                keyboard.hideSoftInputFromWindow(checkpoint.getWindowToken(), 0);

                ((PlacesAutoCompleteAdapter) checkpoint.getAdapter()).clear();

            }
        });

        removeDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeDestinationButton.setVisibility(View.INVISIBLE);
                finalDestination.setText("");
            }
        });

        removeDestinationButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clickFX(removeDestinationButton, motionEvent);

                return false;
            }
        });

        navigate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                clickFX(navigate, motionEvent);

                return false;
            }
        });


        // Navigate button
        navigate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startSpinner();
            }
        });

        // Handles when user clicks "done" button on keyboard
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_BACK) {
                    finish();
                }

                // If "done" on keyboard is clicked set search result to most accurate item
                if (i == 66 && search != null && search.getAdapter() != null) {

                    if (!search.getAdapter().isEmpty() && !tempLocation.equals(
                            finalDestination.getText().toString())
                            && !search.getAdapter().getItem(0).toString().equals(
                            finalDestination.getText())) {

                        search.setText(search.getAdapter().getItem(0).toString());
                        search.clearFocus();
                        addButton1.setVisibility(View.VISIBLE);

                        // Hides keyboard
                        keyboard.hideSoftInputFromWindow(search.getWindowToken(), 0);

                    }

                }

                return true;
            }
        });

        // Handles when user clicks "done" button on keyboard
        checkpoint.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                // If "done" on keyboard is clicked set search result to most accurate item
                if (i == 66 && checkpoint != null && checkpoint.getAdapter() != null) {

                    if (!checkpoint.getAdapter().isEmpty()) {

                        checkpoint.setText(checkpoint.getAdapter().getItem(0).toString());
                        checkpoint.clearFocus();
                        addButton2.setVisibility(View.VISIBLE);

                        // Hides keyboard
                        keyboard.hideSoftInputFromWindow(checkpoint.getWindowToken(), 0);

                        ((PlacesAutoCompleteAdapter) checkpoint.getAdapter()).clear();

                    }

                }

                return true;
            }
        });

        // When the user clicks the destination selected
        history1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finalDestination != null && history1Text.getText() != null
                        && !history1Text.getText().equals("")) {

                    finalDestination.setText(history1Text.getText());
                    tempLocation = history1Text.getText().toString();

                    removeDestinationButton.setVisibility(View.VISIBLE);
                }
            }
        });

        history1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                clickFX(history1, motionEvent);

                return false;
            }
        });

        // When the user clicks the destination selected
        history2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finalDestination != null && history2Text.getText() != null
                        && !history2Text.getText().equals("")) {

                    finalDestination.setText(history2Text.getText());
                    tempLocation = history2Text.getText().toString();

                    removeDestinationButton.setVisibility(View.VISIBLE);
                }

            }
        });

        history2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                clickFX(history2, motionEvent);

                return false;
            }
        });

        // When the user clicks the destination selected
        history3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finalDestination != null && history3Text.getText() != null
                        && !history3Text.getText().equals("")) {

                    finalDestination.setText(history3Text.getText());
                    tempLocation = history3Text.getText().toString();

                    removeDestinationButton.setVisibility(View.VISIBLE);
                }

            }
        });




        history3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                clickFX(history3, motionEvent);

                return false;
            }
        });

        // When the user clicks the destination selected
        addButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finalDestination != null && search != null) {
                    finalDestination.setText(search.getText());
                    tempLocation = search.getText().toString();


                    addButton1.setVisibility(View.INVISIBLE);
                    removeDestinationButton.setVisibility(View.VISIBLE);
                    search.setText("");
                }
            }
        });


        addButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clickFX(addButton1, motionEvent);

                return false;
            }
        });

        // When the user clicks the checkpoint selected
        addButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkpoint != null) {

                    addButton2.setVisibility(View.INVISIBLE);


                    // Add it to models list of checkpoints
                    routeModel.getCheckpoints().add(checkpoint.getText() + "");
                    checkpointsStrings.add(checkpoint.getText() + "");
                    adapter.notifyDataSetChanged();

                    checkpointList.setLayoutParams(new LinearLayout.LayoutParams(
                            checkpointList.getWidth(), 150 * checkpointsStrings.size()));

                    checkpoint.setText("");

                }
            }
        });

        addButton2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clickFX(addButton2, motionEvent);

                return false;
            }
        });


    }

    /**
     * Click effect for when a clickable is touched.
     * @param view the controller clicked
     * @param motionEvent up/down
     */
    public void clickFX(View view, MotionEvent motionEvent) {

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            view.setBackgroundColor(Color.LTGRAY);

        } else {
            view.setBackgroundColor(Color.TRANSPARENT);

        }
    }

    /**
     * Initzialises the ui components.
     */
    private void initzialiseUI() {

        historyBox = (LinearLayout) findViewById(R.id.history_box);
        checkpointList = (ListView) findViewById(R.id.list_of_checks);

        history1 = (RelativeLayout) findViewById(R.id.history1);
        history2 = (RelativeLayout) findViewById(R.id.history2);
        history3 = (RelativeLayout) findViewById(R.id.history3);
        historyBox = (LinearLayout) findViewById(R.id.history_box);

        history1Text = (TextView) findViewById(R.id.history1_text);
        history2Text = (TextView) findViewById(R.id.history2_text);
        history3Text = (TextView) findViewById(R.id.history3_text);

        search = (AutoCompleteTextView) findViewById(R.id.search_text_view);
        checkpoint = (AutoCompleteTextView) findViewById(R.id.checkpoint_text);

        finalDestination = (TextView) findViewById(R.id.final_destination_text);

        navigate = (TextView) findViewById(R.id.navigate_button);
        addButton1 = (ImageButton) findViewById(R.id.add_button1);
        addButton2 = (ImageButton) findViewById(R.id.add_button2);
        removeDestinationButton = (ImageButton) findViewById(R.id.remove_destination);

    }


    @Override
    public void performEvent(Event event) {

        // When a new destination is selected this activity is to be finished
        if (event.isType(RouteRequestEvent.class)) {
            spinnerDialog.dismiss();
            // Sends user back to MainActivity after have chosen the destination
            finish();
        }

        if (event.isType(RefreshHistoryEvent.class)) {
            history1Text.setText(((RefreshHistoryEvent) event).getPlace1());
            history2Text.setText(((RefreshHistoryEvent) event).getPlace2());
            history3Text.setText(((RefreshHistoryEvent) event).getPlace3());
        }
    }
}
