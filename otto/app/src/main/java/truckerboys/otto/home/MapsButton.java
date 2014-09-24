package truckerboys.otto.home;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import truckerboys.otto.R;

/**
 * Class representing the maps button.
 * When clicked the user is redirected to the maps screen.
 * Created by Mikael Malmqvist on 2014-09-24.
 */
public class MapsButton extends AbstractImageButton {
    public MapsButton(Context context) {
        super(context);
    }

    @Override
    public void setOnClickAction(final Activity activity) {

        setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ((ViewPager) activity.findViewById(R.id.pager)).setCurrentItem(2);
            }
        });
    }

}
