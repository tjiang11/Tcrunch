package com.toniebalonie.tjiang11.tcrunch.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.toniebalonie.tjiang11.tcrunch.R;
import com.toniebalonie.tjiang11.tcrunch.activities.StudentTicketListActivity;
import com.toniebalonie.tjiang11.tcrunch.activities.TeacherTicketListActivity;

/**
 * Created by tjiang11 on 1/27/17.
 */

public class StudentAddClassDialog extends DialogFragment {

    private EditText input;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.add_class_student, null);
        input = (EditText) view.findViewById(R.id.class_code);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Course Code");
        builder.setMessage("Join a class")
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!input.getText().toString().isEmpty()) {
                        ((StudentTicketListActivity) getActivity())
                                .doNewClassDialogPositiveClick(input.getText().toString(), d);
                    }
                    if (input.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Class code cannot be blank.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
