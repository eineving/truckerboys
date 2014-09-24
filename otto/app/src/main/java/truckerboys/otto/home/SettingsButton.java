package truckerboys.otto.home;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import truckerboys.otto.R;


/**
 * Class representing the settings button.
 * When clicked the user is redirected to the settings screen.
 * Created by Mikael Malmqvist on 2014-09-24.
 */
public class SettingsButton extends AbstractImageButton {
    public SettingsButton(Context context) {
        super(context);
    }

    @Override
    public void setOnClickAction(final Activity activity) {

        setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ((ViewPager) activity.findViewById(R.id.pager)).setCurrentItem(6);
            }
        });
    }
}
