package com.toniebalonie.tjiang11.tcrunch.services;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.firebase.iid.FirebaseInstanceId;
import com.toniebalonie.tjiang11.tcrunch.R;
import com.toniebalonie.tjiang11.tcrunch.activities.StudentTicketListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tjiang11 on 4/9/17.
 */

public class PollingService extends GcmTaskService {

    private static final String TAG = "PollingService";

    //The minimum number of hours that must elapse since the last time a
    //notification was sent to the user before another notification is sent.
    private static final int HOURS_BETWEEN_NOTIFS = 4;
    private static final long NUM_MS_IN_HR = 3600000;

    //The minimum number of minutes that mast elapse since the last time the student
    //submitted a response before sending a notification.
    private static final int MINUTES_SINCE_LAST_ANSWERED = 10;
    private static final int NUM_MS_IN_MINUTE = 60000;

    private String studentId;
    private HashSet<String> answered;

    //Use to check all queries for tickets for specific classes have resolved.
    private HashSet<String> classes;
    private HashMap<String, String> tickets;

    //The last time a notification was sent.
    private static long lastNotificationTime;

    //The last number of unanswered tickets when polling.
    private static long lastNumUnanswered;

    @Override
    public int onRunTask(TaskParams taskParams) {
        answered = new HashSet<>();
        classes = new HashSet<>();
        tickets = new HashMap<>();
        Log.d(TAG, "onRunTask: " + taskParams.getTag());
        studentId = FirebaseInstanceId.getInstance().getId();

        AndroidNetworking.get("https://tcrunch-be6a7.firebaseio.com/answered/{id}.json")
                .addPathParameter("id", studentId)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        recordAnswered(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, anError.getMessage());
                    }
                });

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private void recordAnswered(JSONObject response) {
        Iterator<String> iter = response.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                answered.add(response.get(key).toString());
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        queryClasses();
    }

    private void queryClasses() {
        AndroidNetworking.get("https://tcrunch-be6a7.firebaseio.com/students/{id}.json")
                .addPathParameter("id", studentId)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator<String> iter = response.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            try {
                                JSONObject course = response.getJSONObject(key);
                                String courseId = course.getString("id");
                                classes.add(courseId);
                                queryTicketsForClass(courseId);
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, anError.getMessage());
                    }
                });
    }

    private void queryTicketsForClass(final String courseId) {
        AndroidNetworking.get("https://tcrunch-be6a7.firebaseio.com/tickets/{id}.json")
                .addPathParameter("id", courseId)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator<String> iter = response.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            try {
                                JSONObject ticket = response.getJSONObject(key);
                                String ticketId = ticket.getString("id");
                                String ticketQuestion = ticket.getString("question");
                                tickets.put(ticketId, ticketQuestion);
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                        classes.remove(courseId);
                        if (classes.size() == 0) {
                            checkAnyUnansweredTickets();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, anError.getMessage());
                    }
                });
    }

    private void sendNotification(String title, String msg) {
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, StudentTicketListActivity.class), 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.tcrunch_ic)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setContentIntent(contentIntent)
            //display notification badges
                        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                        .setAutoCancel(true);
        mNotificationManager.notify(0, mBuilder.build());
    }


    private void checkAnyUnansweredTickets() {
        for (String key : answered) {
            tickets.remove(key);
        }
        if (tickets.size() > 0 && (sufficientTimeElapsedSinceLastNotif() || numUnansweredIncreased())) {
            lastNotificationTime = System.currentTimeMillis();
            String msg = (String) tickets.values().toArray()[0];
            sendNotification(
                    "You have " + tickets.size() + " unanswered tickets", msg);
        }
        lastNumUnanswered = tickets.size();
    }

    private boolean sufficientTimeElapsedSinceLastNotif() {
        return System.currentTimeMillis() - lastNotificationTime >
                HOURS_BETWEEN_NOTIFS * NUM_MS_IN_HR;
    }
    private boolean numUnansweredIncreased() {
        return lastNumUnanswered < tickets.size();
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
