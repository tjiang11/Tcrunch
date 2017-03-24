package com.toniebalonie.tjiang11.tcrunch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

/**
 * Created by tjiang11 on 1/18/17.
 */

public class AddClassDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.addclass_edittext, null);
        final EditText input = (EditText) view.findViewById(R.id.add_class_input);
        final EditText classCodeInput = (EditText) view.findViewById(R.id.class_code);
        builder.setMessage("Add a new class")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((TeacherTicketListActivity) getActivity())
                                .doNewClassDialogPositiveClick(
                                        input.getText().toString(),
                                        classCodeInput.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setView(view);
        return builder.create();
    }
}
