package com.toniebalonie.tjiang11.tcrunch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by tjiang11 on 2/21/17.
 */

public class TeacherInfoDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("How to use Tcrunch");
        builder.setMessage("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur at posuere elit. Vestibulum bibendum magna vel purus rutrum varius. Pellentesque euismod interdum auctor. Suspendisse luctus lobortis odio non consequat. Nam tincidunt ut nisi vitae sodales. Phasellus sapien sapien, porta sed pretium vitae, accumsan ut nibh. Fusce nec sapien rutrum, lobortis est at, sodales nisl. Aenean lacus metus, gravida nec enim eu, accumsan auctor arcu. Sed mattis ultrices odio, vitae congue lacus commodo quis.\n" + "Nullam et ante bibendum, laoreet quam id, euismod risus. In hac habitasse platea dictumst. In gravida urna eget lobortis eleifend. Nunc id enim eget elit pulvinar euismod non vel nibh. Donec ac risus viverra massa finibus hendrerit consectetur ornare arcu. Etiam at laoreet metus. Mauris rutrum nibh at diam porttitor dictum. Nunc libero turpis, ultricies sed nibh non, bibendum mollis urna. Aliquam euismod quam sit amet turpis sagittis maximus. Integer ut commodo est.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        return builder.create();
    }
}
