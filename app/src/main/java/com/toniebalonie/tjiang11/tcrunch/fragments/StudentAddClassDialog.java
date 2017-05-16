package com.toniebalonie.tjiang11.tcrunch.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.toniebalonie.tjiang11.tcrunch.R;
import com.toniebalonie.tjiang11.tcrunch.activities.StudentTicketListActivity;

/**
 * Created by tjiang11 on 1/27/17.
 */

public class StudentAddClassDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.add_class_student, null);
        final EditText input = (EditText) view.findViewById(R.id.class_code);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Course Code");
        builder.setMessage("Join a class")
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((StudentTicketListActivity) getActivity())
                                .doNewClassDialogPositiveClick(input.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        builder.setView(view);
        return builder.create();
    }
}
