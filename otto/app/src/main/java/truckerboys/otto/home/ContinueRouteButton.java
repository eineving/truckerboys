package truckerboys.otto.home;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import truckerboys.otto.R;

/**
 * Class representing the Continue Route button.
 * When clicked the user is redirected to the Continue Route screen.
 * Created by Mikael Malmqvist on 2014-09-24.
 */
public class ContinueRouteButton extends AbstractImageButton{

    public ContinueRouteButton(Context context) {
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
