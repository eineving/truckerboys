package truckerboys.otto.home;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import truckerboys.otto.R;

/**
 * Class representing the new route button.
 * When clicked the user is redirected to the new route screen.
 * Created by Mikael Malmqvist on 2014-09-24.
 */
public class NewRouteButton extends AbstractImageButton {
    public NewRouteButton(Context context) {
        super(context);
    }

    @Override
    public void setOnClickAction(final Activity activity) {

        setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // TODO Switch to the new route screen, that's not a tab
            }
        });
    }
}
