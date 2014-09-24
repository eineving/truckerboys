package truckerboys.otto.home;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageButton;

/**
 * Created by root on 2014-09-24.
 */
public abstract class AbstractImageButton extends ImageButton {
    public AbstractImageButton(Context context) {
        super(context);
    }

    public abstract void setOnClickAction(Activity activity);

}
