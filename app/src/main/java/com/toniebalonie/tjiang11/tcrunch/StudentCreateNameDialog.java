package com.toniebalonie.tjiang11.tcrunch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.R.id.input;

/**
 * Created by tjiang11 on 2/1/17.
 */

public class StudentCreateNameDialog extends DialogFragment {

    private EditText input;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.create_name_student, null);
        input = (EditText) view.findViewById(R.id.student_name);
        builder.setMessage("What's your full name?")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    //Do nothing here because we override this button later to change the close behaviour.
                    //However, we still need this because on older versions of Android unless we
                    //pass a handler the button doesn't get instantiated
                    }
                });
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!input.getText().toString().isEmpty()) {
                        ((StudentTicketListActivity) getActivity())
                                .doCreateNameDialogClick(input.getText().toString());
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Your name cannot be blank", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
