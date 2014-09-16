package truckerboys.otto.presenter;

import android.app.Activity;

import truckerboys.otto.model.IModel;
import truckerboys.otto.view.IView;


/**
 *
 * An abstract representation of an android Activity.
 * Created by Martin Nilsson on 16/09/2014.
 */
public abstract class AbstractPresenter extends Activity{
    private IModel mModel;
    private IView mView;

}
