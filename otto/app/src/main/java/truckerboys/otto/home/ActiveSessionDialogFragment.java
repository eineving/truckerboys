package truckerboys.otto.home;

import android.app.Activity;
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

    // Use this instance of the interface to deliver action events
    ActiveSessionDialogFragmentListener myListener;

    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage("You've got an active session running!\n " +
                "You sure you want to set a new route?").setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

            // User clicks "yes"
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                myListener.onDialogPositiveClick(ActiveSessionDialogFragment.this);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            // User clicks "cancel"
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                myListener.onDialogNegativeClick(ActiveSessionDialogFragment.this);
            }
        });

        return dialogBuilder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            myListener = (ActiveSessionDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public interface ActiveSessionDialogFragmentListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
}
