package truckerboys.otto.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Mikael Malmqvist on 2014-10-16.
 * Dialog to display when an active session is running and
 * the user tries to enter a new route.
 */
public class ActiveSessionDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage("You've got an active session running!\n " +
                "You sure you want to set a new route?").setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

            // User clicks "yes"
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // TODO Handle click
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            // User clicks "cancel"
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // TODO Handle click
            }
        });

        return dialogBuilder.create();
    }

    public interface ActiveSessionDialogFragmentListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
}
