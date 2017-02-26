package com.toniebalonie.tjiang11.tcrunch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Created by tjiang11 on 2/26/17.
 */

public class StudentInfoDialog extends DialogFragment {

    private static final String text = "<big>" + "<h5>What is Tcrunnch?</h5>" +
            "<p>Tcrunch is a mobile-platform method for exit ticket administration and collection. " +
            "Our app decreases the time it takes to make and launch such a ticket. Our mission is to decrease " +
            "the time for data collection and analysis for teachers who can use this teaching technique to " +
            "improve teaching and education in the classroom.</p>" +

            "<p>Tcrunch was developed from funding from the Johns Hopkins Center for Educational Resources.</p>" +
            "</big>";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TextView infoView = new TextView(getActivity());
        infoView.setPadding(30, 20, 30, 20);
        infoView.setText(Html.fromHtml(text));
        infoView.setMovementMethod(LinkMovementMethod.getInstance());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.setView(infoView);
        return builder.create();
    }
}
