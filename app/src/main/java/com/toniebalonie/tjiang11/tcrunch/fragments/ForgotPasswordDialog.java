package com.toniebalonie.tjiang11.tcrunch.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.toniebalonie.tjiang11.tcrunch.R;

/**
 * Created by tjiang11 on 3/12/17.
 */

public class ForgotPasswordDialog extends DialogFragment {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText inputEmail;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View view = getActivity().getLayoutInflater().inflate(R.layout.forgot_password_dialog, null);
        inputEmail = (EditText) view.findViewById(R.id.forgot_password_email);
        builder.setView(view);
        builder.setMessage("Reset Your Password")
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
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
                    if (!inputEmail.getText().toString().isEmpty()) {
                        mAuth.sendPasswordResetEmail(inputEmail.getText().toString());
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Please enter an email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
