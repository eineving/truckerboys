package truckerboys.otto;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import truckerboys.otto.utils.LocationHandler;
import truckerboys.otto.utils.tabs.SlidingTabLayout;
import truckerboys.otto.utils.tabs.TabPagerAdapter;

public class MainActivity extends FragmentActivity {
    private ViewPager viewPager;
    private TabPagerAdapter pagerAdapter;
    private LocationHandler locationHandler;
    private OTTO otto;

    public static final String SETTINGS = "Settings_file";
    public static final String STATS = "Stats_file";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        otto = new OTTO();
        locationHandler = new LocationHandler(this);

        //Create standard view with a ViewPager and corresponding tabs.
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), otto.getViews());
        viewPager.setAdapter(pagerAdapter);

        /*
         * Make sure we never have to reload any of the tabs after the app has been started.
         * This makes the users interaction experience with the app alot smoother.
         */
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount() - 1);


        //Use Googles 'SlidingTabLayout' to display tabs for all views.
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab_slider);
        slidingTabLayout.setViewPager(viewPager);


        // Turns display properties (alive on/off) based on saved settings file
        if(getSharedPreferences(SETTINGS, 0).getBoolean("displayAlive", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Turns system sound on/off based on saved settings file
        if(getSharedPreferences(SETTINGS, 0).getBoolean("sound", true)) {
            ((AudioManager)this.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } else {
            ((AudioManager)this.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }



        // Restore preferences from last session
        // restorePreferences();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
