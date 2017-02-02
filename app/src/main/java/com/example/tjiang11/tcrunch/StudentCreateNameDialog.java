package com.example.tjiang11.tcrunch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by tjiang11 on 2/1/17.
 */

public class StudentCreateNameDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.addclass_edittext, null);
        final EditText input = (EditText) view.findViewById(R.id.add_class_input);
        input.setHint("Your Name");
        builder.setMessage("What's your name?")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((StudentTicketListActivity) getActivity())
                                .doCreateNameDialogClick(input.getText().toString());
                    }
                });
        builder.setView(view);
        return builder.create();
    }
}
