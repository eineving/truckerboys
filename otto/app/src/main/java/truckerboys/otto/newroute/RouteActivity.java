package truckerboys.otto.newroute;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import truckerboys.otto.R;
import truckerboys.otto.utils.eventhandler.EventTruck;
import truckerboys.otto.utils.eventhandler.IEventListener;
import truckerboys.otto.utils.eventhandler.events.Event;
import truckerboys.otto.utils.eventhandler.events.NewDestination;
import truckerboys.otto.utils.eventhandler.events.RefreshHistoryEvent;
import utils.PlacesAutoCompleteAdapter;
import utils.SuggestionProvider;

/**
 * Created by Mikael Malmqvist on 2014-10-02.
 * Activity for when selecting a new route.
 * This class can be seen as the view in the MVP pattern
 * for when selecting a new route.
 */
public class RouteActivity extends Activity implements IEventListener{
    private RoutePresenter routePresenter;
    private RouteModel routeModel = new RouteModel();

    private ListView list;
    private ArrayList<String> strings;

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

    private ImageButton navigate;
    private ImageButton addButton1;
    private ImageButton addButton2;

    private LinearLayout resultsBox;
    private LinearLayout historyBox;
    private RelativeLayout history1;
    private RelativeLayout history2;
    private RelativeLayout history3;

    private String tempLocation = "temp";


    InputMethodManager keyboard;


    public static final String HISTORY = "History_file";

    public RouteActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.fragment_new_route);
        EventTruck.getInstance().subscribe(this);
        coder = new Geocoder(this);
        history = getSharedPreferences(HISTORY, 0);
        routePresenter = new RoutePresenter();
        keyboard = (InputMethodManager)getSystemService(
               this.INPUT_METHOD_SERVICE);

        // Sets ui components
        historyBox = (LinearLayout) findViewById(R.id.history_box);
        // resultsBox.setX(historyBox.getX());
        list = (ListView) findViewById(R.id.list_of_checks);


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

        navigate = (ImageButton) findViewById(R.id.navigate_button);
        addButton1 = (ImageButton) findViewById(R.id.add_button1);
        addButton2 = (ImageButton) findViewById(R.id.add_button2);

        // Loads destination history
        routePresenter.loadHistory(history);


        // Sets the adapter to our PlacesAutoCompleteAdapter which uses the TripPlanner class
        // for getting the results
        search.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1));
        checkpoint.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1));

        strings = new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.text_list_item, strings);

        list.setAdapter(adapter);


        list.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(routeModel.getCheckpoints().contains(((TextView)view).getText())){
                    routeModel.getCheckpoints().remove(((TextView)view).getText());
                    //TODO remove from list view
                    adapter.remove(((TextView)view).getText());
                    adapter.notifyDataSetChanged();

                    list.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 120*strings.size()));
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

                ((PlacesAutoCompleteAdapter)checkpoint.getAdapter()).clear();

            }
        });

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalDestination != null && coder != null && routePresenter != null) {
                    if(finalDestination.getText() != null && !finalDestination.getText().equals("")){

                        routePresenter.sendLocation("" + finalDestination.getText().toString(), coder);

                        if(history != null) {
                            routePresenter.saveHistory(history, "" + finalDestination.getText().toString());
                        }
                    }
                }
            }
        });

        // Handles when user clicks "done" button on keyboard
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(i == KeyEvent.KEYCODE_BACK) {
                    finish();
                }

                // If "done" on keyboard is clicked set search result to most accurate item
                if (i == 66 && search != null && search.getAdapter() != null) {

                    System.out.println("COMPARING: " + tempLocation + " AND " + finalDestination.getText().toString() + "************");

                    if(!search.getAdapter().isEmpty() && !tempLocation.equals(finalDestination.getText().toString())
                            && !search.getAdapter().getItem(0).toString().equals(finalDestination.getText())) {

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
                if(i == 66 && checkpoint != null && checkpoint.getAdapter() != null) {

                    if(!checkpoint.getAdapter().isEmpty()) {

                        checkpoint.setText(checkpoint.getAdapter().getItem(0).toString());
                        checkpoint.clearFocus();
                        addButton2.setVisibility(View.VISIBLE);

                        // Hides keyboard
                        keyboard.hideSoftInputFromWindow(checkpoint.getWindowToken(), 0);

                        ((PlacesAutoCompleteAdapter)checkpoint.getAdapter()).clear();

                    }

                }

                return true;
            }
        });

        // When the user clicks the destination selected
        history1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                routePresenter.sendLocation("" + history1Text.getText(), coder);
            }
        });

        // When the user clicks the destination selected
        history2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                routePresenter.sendLocation("" + history2Text.getText(), coder);

            }
        });

        // When the user clicks the destination selected
        history3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                routePresenter.sendLocation("" + history3Text.getText(), coder);

            }
        });

        // When the user clicks the destination selected
        addButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalDestination != null && search != null) {
                    finalDestination.setText(search.getText());
                    tempLocation = search.getText().toString();


                    addButton1.setVisibility(View.INVISIBLE);
                    search.setText("");
                }
            }
        });

        // When the user clicks the checkpoint selected
        addButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkpoint != null) {

                    addButton2.setVisibility(View.INVISIBLE);


                    // Add it to models list of checkpoints
                    routeModel.getCheckpoints().add(checkpoint.getText() + "");
                    strings.add(checkpoint.getText() + "");
                    adapter.notifyDataSetChanged();

                    list.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 120*strings.size()));

                    checkpoint.setText("");

                }
            }
        });


    }


    @Override
    public void performEvent(Event event) {

        // When a new destination is selected this activity is to be finished
        if(event.isType(NewDestination.class)) {

            // Sends user back to MainActivity after have chosen the destination
            finish();

        }

        if(event.isType(RefreshHistoryEvent.class)) {
            history1Text.setText(((RefreshHistoryEvent)event).getPlace1());
            history2Text.setText(((RefreshHistoryEvent)event).getPlace2());
            history3Text.setText(((RefreshHistoryEvent)event).getPlace3());
        }
    }
}
