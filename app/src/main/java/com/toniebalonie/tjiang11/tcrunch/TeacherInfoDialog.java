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

            "<p>See examples of exit survey questions about three menu_teacher categories<br>" +
            "<u><a href='https://drive.google.com/open?id=0B90vvtu1KZEMQUIxdk1GZFAtMlk'>Understanding students</a></u><br>" +
            "<u><a href='https://drive.google.com/open?id=0B90vvtu1KZEMWG5nd2lzcjVvcU0'>Evaluating Yourself</a></u><br>" +
            "<u><a href='https://drive.google.com/open?id=0B90vvtu1KZEMTmQ5ZVRvbTF0aVk'>Evaluating your class</a></u></p>" +

            "<h5>What is Tcrunch?</h5>" +
            "<p>Tcrunch is a mobile-platform method for exit ticket administration and collection. Our app decreases the " +
            "time it takes to make and launch such a ticket. Our mission is to decrease the time for data collection and " +
            "analysis for teachers who can use this teaching technique to improve teaching and education in the classroom.</p>" +
            "<p>Tcrunch was developed from funding from the Johns Hopkins Center for Educational Resources.</p>" +

            "<h5>How do I create a class?</h5>" +
            "<p>At the home screen for teachers click \"Add Class\" found in the upper right hand side of the app. Give the new class a unique name and a class code to share with your students so that they can access the tickets for the class. Tap the menu button on the upper left side of the app to access and see the new class you have created.</p>" +

            "<h5>How do I create an exit ticket?</h5>" +
            "<p>After creating a class, select the class that you would like to create an exit ticket in. Click the red \"+\" button in the bottom right hand side of the app. Set a date and time that you would like to launch the exit ticket. If you attempt to select a time in the past, then the time will be set to now and the ticket will launch immediately. Type the question in the field below and click create. If you want to use a multiple choice question, then check the Multiple Choice checkbox and type in your choices.</p>" +

            "<h5>What is a class code?</h5>" +
            "<p>When creating a new class, you will be asked to enter a class code. You will share this class code with your students to allow them to access your created tickets.</p>" +

            "<h5>How do I see my students' responses?</h5>" +
            "<p>Your students responses will come in real time. Click on the exit ticket of interest. You will be able to see the responses below in the order that they were submitted. If the ticket was multiple choice, then you will see a bar graph of student responses. Additionally you can download a csv file with the data to also view the results of the exit ticket.</p>" +

            "<h5>How do I download my results for exit tickets?</h5>" +
            "<p>First click on the exit ticket of interest. Then click on the options menu in the upper right hand side of the app. Click \"Email Ticket Data\" and select how you would like to send the data. It will be sent as a csv file, which you could open on spreadsheet programs such as Microsoft Excel or Google Sheets.</p>" +

            "<h5>How do I delete an exit ticket?</h5>" +
            "<p>First click on the exit ticket of interest. Then click on the options menu in the upper right hand side of the app. Click \"Delete Ticket.\" You will be asked if you are sure you would like to delete the ticket because this action cannot be undone. Click yes if so.</p>" +

            "<h5>How can I have the students answer anonymously?</h5>" +
            "<p>When creating a new ticket, there will be a checkbox labeled \"Anonymous Responses\". If checked, then all student responses will be anonymous. Students will see an indication before submitting that their response will be anonymous. You will still be able to view which students submitted responses after exporting your data via email, but responses will not be tied to their authors.</p>" +

            "<h5>How do I delete a class?</h5>" +
            "<p>Select the class you would like to delete from the menu button on the upper left side of the app. You should now see the class in the menu_teacher screen of the app with any of the exit tickets you have created. Now click the options button in the upper right hand side of the app. Select \"delete class.\" You will be prompted if you want to delete this class. Press yes if this is the right class to delete. This action cannot be undone so make sure this is the class you want to delete.</p>" +

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
