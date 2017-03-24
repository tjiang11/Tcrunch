package com.toniebalonie.tjiang11.tcrunch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by tjiang11 on 1/18/17.
 */

public class AddClassDialog extends DialogFragment {

    private EditText classNameInput;
    private EditText classCodeInput;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.addclass_edittext, null);
        classNameInput = (EditText) view.findViewById(R.id.add_class_input);
        classCodeInput = (EditText) view.findViewById(R.id.class_code);
        builder.setMessage("Add a new class")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
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

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!classNameInput.getText().toString().isEmpty()
                            && !classCodeInput.getText().toString().isEmpty()) {
                        ((TeacherTicketListActivity) getActivity())
                                .doNewClassDialogPositiveClick(
                                        classNameInput.getText().toString(),
                                        classCodeInput.getText().toString());
                        dismiss();
                    }
                    if (classNameInput.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Class name cannot be blank.", Toast.LENGTH_SHORT).show();
                    } else if (classCodeInput.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Class code cannot be blank.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
}
