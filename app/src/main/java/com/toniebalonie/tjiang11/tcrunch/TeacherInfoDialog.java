package com.toniebalonie.tjiang11.tcrunch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Created by tjiang11 on 2/21/17.
 */

public class TeacherInfoDialog extends DialogFragment {

    private static final String text = "<big>" + "<h5>What is an exit survey or exit ticket?</h5>" +
            "<p>An exit survey or exit ticket is designed to collect immediate feedback at the end " +
            "of lecture usually regarding student comprehension, engagement, teaching strategy effectiveness, " +
            "or classroom dynamic. Traditionally it was done on 3x5 cards and collected at the end of class.</p>" +

            "<p>See examples of exit survey questions about three main categories<br>" +
            "<u><a href='https://drive.google.com/open?id=0B90vvtu1KZEMQUIxdk1GZFAtMlk'>Understanding students</a></u><br>" +
            "<u><a href='https://drive.google.com/open?id=0B90vvtu1KZEMWG5nd2lzcjVvcU0'>Evaluating Yourself</a></u><br>" +
            "<u><a href='https://drive.google.com/open?id=0B90vvtu1KZEMTmQ5ZVRvbTF0aVk'>Evaluating your class</a></u></p>" +

            "<h5>What is Tcrunch?</h5>" +
            "<p>Tcrunch is a mobile-platform method for exit ticket administration and collection. Our app decreases the " +
            "time it takes to make and launch such a ticket. Our mission is to decrease the time for data collection and " +
            "analysis for teachers who can use this teaching technique to improve teaching and education in the classroom.</p>" +
            "<p>Tcrunch was developed from funding from the Johns Hopkins Center for Educational Resources.</p>" + "</big>";

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
