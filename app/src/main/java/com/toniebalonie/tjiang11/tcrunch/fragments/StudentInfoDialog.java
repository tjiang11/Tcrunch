package com.toniebalonie.tjiang11.tcrunch.fragments;

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

            "<h5>Why does the app need my name?</h5>" +
            "<p>This will be how the teacher will recognize if you submitted the exit ticket or not. Your response will be tied to your name, unless explicitly marked by the teacher to be a ticket with only anonymous responses. If this is the case, you will see an indication on the submit response page that your response will be anonymous.</p>" +

            "<h5>What is the class code and how do I get it?</h5>" +
            "<p>The class code is a unique code defined for every class created by a teacher in Tcrunch. The teacher will have to provide this information to students.</p>" +

            "<h5>How do I know that I submitted the ticket correctly?</h5>" +
            "<p>When you log into your app you, you will see exit tickets either under sections labeled \"Not Answered\" or \"Answered.\" If you see your exit ticket in the \"Answered\" section, then you know that you have successfully submitted your exit ticket.</p>" +

            "<h5>How do I see the exit tickets for just one class?</h5>" +
            "<p>Click the menu button on the upper left hand side of the app. From there you can select the class of interest. Now only the exit tickets for this class will be available to see.</p>" +

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
