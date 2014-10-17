package truckerboys.otto.newroute;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Mikael Malmqvist on 2014-10-16.
 * Dialog to display when no destination has been
 * set by the user.
 */
public class NoDestinationDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage("Please set a final destination for your route.").setNeutralButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing
            }
        });

        return dialogBuilder.create();
    }

}
