package com.toniebalonie.tjiang11.tcrunch.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.opencsv.CSVWriter;
import com.toniebalonie.tjiang11.tcrunch.R;
import com.toniebalonie.tjiang11.tcrunch.models.Response;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.toniebalonie.tjiang11.tcrunch.activities.LoginActivity.PREFS_NAME;

public class TeacherTicketDetailActivity extends AppCompatActivity {

    private static final String TAG = TeacherTicketDetailActivity.class.getName();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private SharedPreferences sharedPrefs;

    private ResponseListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mDatabaseReferenceResponses;
    private DatabaseReference mDatabaseReference;

    private TextView responsesText;

    private ArrayList<Response> responseList;
    private ArrayList<String> userResponseList;

    private String question;
    private String questionDate;
    private ArrayList<String> answerChoices;

    private String ticketId;
    private String classId;
    private String className;

    private boolean anonymous;

    private XYPlot plot;

    private enum QuestionType {
        FREE_RESPONSE,
        MULTIPLE_CHOICE
    }

    private QuestionType questionType;

    private HashMap<String, Integer> responseMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_ticket_detail);
        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();
        ticketId = (String) bundle.get("ticket_id");
        question = (String) bundle.get("question");
        answerChoices = bundle.getStringArrayList("answer_choices");
        if (answerChoices.isEmpty()) {
            questionType = QuestionType.FREE_RESPONSE;
        } else {
            questionType = QuestionType.MULTIPLE_CHOICE;
        }
        long startTime = (long) bundle.get("start_time");
        classId = (String) bundle.get("class_id");
        className = (String) bundle.get("class_name");
        anonymous = (boolean) bundle.get("anonymous");

        getSupportActionBar().setTitle(className);

        responseList = new ArrayList<Response>();
        userResponseList = new ArrayList<String>();

        responsesText = (TextView) findViewById(R.id.responses_text);

        if (questionType == QuestionType.MULTIPLE_CHOICE) {
            plot = (XYPlot) findViewById(R.id.plot);
            plot.setVisibility(View.VISIBLE);
            plot.setTitle("");
            plot.setDomainBoundaries(0, 5, BoundaryMode.AUTO);
            plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
            plot.setRangeBoundaries(0, 1000, BoundaryMode.AUTO);
            plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
            plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);
            plot.setRangeLabel("Number of Responses");
            plot.setDomainLabel("");
            plot.getLayoutManager().remove(plot.getLegend());
            plot.getBackgroundPaint().setColor(Color.parseColor("#FAFAFA"));
            plot.setPlotMargins(0, 0, 0, 0);
            plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
            plot.getBorderPaint().setColor(Color.TRANSPARENT);
            plot.getGraph().getBackgroundPaint().setColor(Color.TRANSPARENT);
            plot.getGraph().getDomainGridLinePaint().setColor(Color.TRANSPARENT);
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setColor(Color.TRANSPARENT);
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                private String[] LABELS = {"A", "B", "C", "D", "E"};
                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                    ArrayList<String> myLabels = new ArrayList<String>();
                    myLabels.add("");
                    for (int i = 0; i < answerChoices.size() ; i++) {
                        myLabels.add(LABELS[i]);
                    }
                    int parsedInt =  Math.round(Float.parseFloat(obj.toString()));
                    if (parsedInt >= myLabels.size()) {
                        return toAppendTo;
                    }
                    if (parsedInt > -1) {
                        String labelString = myLabels.get(parsedInt);
                        toAppendTo.append(labelString);
                    }
                    return toAppendTo;
                }

                @Override
                public Object parseObject(String source, ParsePosition pos) {
                    return java.util.Arrays.asList(LABELS).indexOf(source);
                }
            });
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                    int parsedInt = Math.round(Float.parseFloat(obj.toString()));
                    toAppendTo.append(Integer.toString(parsedInt));
                    return toAppendTo;
                }

                @Override
                public Object parseObject(String source, ParsePosition pos) {
                    return null;
                }
            });
        }

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReferenceResponses = FirebaseDatabase.getInstance().getReference().child("responses").child(ticketId);
        mDatabaseReferenceResponses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                responseList.clear();
                responseMap.clear();
                for (int i = 0; i < answerChoices.size(); i++) {
                    responseMap.put(answerChoices.get(i), 0);
                }
                for (DataSnapshot responseSnapshot : dataSnapshot.getChildren()) {
                    Response resp = responseSnapshot.getValue(Response.class);
                    if (anonymous) {
                        userResponseList.add(resp.getAuthor());
                        resp.setAuthor("Anonymous");
                    }
                    responseList.add(resp);
                }
                Collections.sort(responseList, Response.ResponseTimeComparator);
                String responseTextString;
                if (responseList.size() == 1) {
                    responseTextString = "RESPONSE";
                } else {
                    responseTextString = "RESPONSES";
                }
                String resp = responseList.size() + " " + responseTextString;
                responsesText.setText(resp);
                mAdapter.notifyDataSetChanged();

                if (questionType == QuestionType.MULTIPLE_CHOICE) {
                    for (int i = 0; i < responseList.size(); i++) {
                        responseMap.put(responseList.get(i).getResponse(), responseMap.get(responseList.get(i).getResponse()) + 1);
                    }
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    list.add(0);
                    for (int i = 0; i < answerChoices.size(); i++) {
                        list.add(responseMap.get(answerChoices.get(i)));
                    }
                    list.add(0);
                    XYSeries bars = new SimpleXYSeries(list, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
                    MyBarFormatter barFormatter = new MyBarFormatter(Color.GREEN, Color.BLACK);
                    plot.clear();
                    plot.addSeries(bars, barFormatter);
                    MyBarRenderer renderer = plot.getRenderer(MyBarRenderer.class);
                    renderer.setBarOrientation(MyBarRenderer.BarOrientation.SIDE_BY_SIDE);
                    renderer.setBarGroupWidth(MyBarRenderer.BarGroupWidthMode.FIXED_GAP, 30);

                    int maxNumResponses = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) > maxNumResponses) {
                            maxNumResponses = list.get(i);
                        }
                    }

                    if (maxNumResponses > 10) {
                        plot.setRangeStep(StepMode.SUBDIVIDE, 10);
                    }
                    plot.redraw();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "response reference cancelled");
            }
        });

        TextView questionText = (TextView) findViewById(R.id.ticket_detail_question);
        TextView startTimeText = (TextView) findViewById(R.id.ticket_detail_start_time);
        RecyclerView responsesText = (RecyclerView) findViewById(R.id.ticket_detail_responses_recycler_view);
        if (questionType == QuestionType.FREE_RESPONSE) {
            responsesText.setVisibility(View.VISIBLE);
        }
        responsesText.setFocusable(false);

        mAdapter = new ResponseListAdapter(responseList);
        responsesText.setAdapter(mAdapter);

        mLinearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        responsesText.setLayoutManager(mLinearLayoutManager);

        questionText.setText(question);

        Date date = new Date(startTime);
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, h:mm a", Locale.US);
        questionDate = formatter.format(date);
        startTimeText.setText(questionDate);

        RecyclerView mcLegend = (RecyclerView) findViewById(R.id.multiple_choice_plot_legend);
        mcLegend.setFocusable(false);
        mcLegend.setAdapter(new LegendAdapter(answerChoices, this));
        mcLegend.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teacher_ticket_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.email_ticket_data) {
            exportTicketToCSV();
            return true;
        }

        if (id == R.id.delete_ticket) {
            final TeacherTicketDetailActivity parent = this;
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure you want to delete this ticket?")
                    .setMessage("This action cannot be undone.")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabaseReference.child("tickets").child(classId).child(ticketId).removeValue();
                            mDatabaseReference.child("responses").child(ticketId).removeValue();
                            Toast.makeText(parent, "Ticket deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportTicketToCSV() {
        CSVWriter csvWriter;
        verifyStoragePermissions(this);
        try {
            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "TicketData.csv";
            String filePath = baseDir + File.separator + fileName;
            File ticketDataFile = new File(filePath);
            csvWriter = new CSVWriter(new FileWriter(filePath));
            String[] classInfo = { "Class", className };
            String[] questionInfo = { "Question Text", question };
            String[] launchDate = { "Launch Time", questionDate };
            String[] numResponses = { "# Responses", Integer.toString(responseList.size()) };
            csvWriter.writeNext(classInfo);
            csvWriter.writeNext(questionInfo);
            csvWriter.writeNext(launchDate);
            csvWriter.writeNext(numResponses);
            csvWriter.writeNext(new String[] {});

            if (!anonymous) {
                String[] headers = {"USER", "RESPONSE", "TIME"};
                csvWriter.writeNext(headers);
                for (Response r : responseList) {
                    Date date = new Date(r.getTime());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy EEEE, MMMM d, h:mm a", Locale.US);
                    questionDate = formatter.format(date);
                    String[] data = {r.getAuthor(), r.getResponse(), questionDate};
                    csvWriter.writeNext(data);
                }
            } else {
                String[] usersHeader = {"USERS"};
                String[] responseHeader = {"RESPONSES"};
                csvWriter.writeNext(usersHeader);
                for (String u : userResponseList) {
                    String[] data = {u};
                    csvWriter.writeNext(data);
                }
                csvWriter.writeNext(new String[] {});
                csvWriter.writeNext(responseHeader);
                Collections.shuffle(responseList);
                for (Response r : responseList) {
                    String[] data = {r.getResponse()};
                    csvWriter.writeNext(data);
                }
            }
            csvWriter.close();

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/csv");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, sharedPrefs.getString("email", ""));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tcrunch: Your ticket data");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Your ticket data is attached.");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(ticketDataFile));

            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, e.toString());
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}

class MyBarFormatter extends BarFormatter
{
    public MyBarFormatter(int fillColor, int borderColor)
    {
        super(fillColor, borderColor);
    }

    @Override public Class<? extends SeriesRenderer> getRendererClass()
    {
        return MyBarRenderer.class;
    }

    @Override public SeriesRenderer getRendererInstance(XYPlot plot)
    {
        return new MyBarRenderer(plot);
    }
}

class MyBarRenderer extends BarRenderer<MyBarFormatter>
{
    public MyBarRenderer(XYPlot plot)
    {
        super(plot);
    }

    public MyBarFormatter getFormatter(int index, XYSeries series)
    {
        switch (index) {
            //For some reason breaks when trying to grab color resource.
            case 1:
                return new MyBarFormatter(Color.parseColor("#FF0000"), Color.TRANSPARENT);
            case 2:
                return new MyBarFormatter(Color.parseColor("#0dafe5"), Color.TRANSPARENT);
            case 3:
                return new MyBarFormatter(Color.parseColor("#4dce29"), Color.TRANSPARENT);
            case 4:
                return new MyBarFormatter(Color.parseColor("#f4df42"), Color.TRANSPARENT);
            case 5:
                return new MyBarFormatter(Color.parseColor("#dd77ff"), Color.TRANSPARENT);
            default:
                return new MyBarFormatter(Color.GRAY, Color.TRANSPARENT);
        }
    }
}

class LegendAdapter extends RecyclerView.Adapter<LegendAdapter.LegendItemViewHolder> {

    private ArrayList<String> mLegendItems;
    private Context ctx;

    public LegendAdapter(ArrayList<String> items, Context ctx) {
        this.mLegendItems = items;
        this.ctx = ctx;
    }

    @Override
    public LegendItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View legendItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.legend_item, parent, false);
        return new LegendItemViewHolder(legendItem);
    }

    @Override
    public void onBindViewHolder(LegendItemViewHolder holder, int position) {
        holder.itemTextView.setText(mLegendItems.get(position));
        int color;
        switch (position) {
            case 0:
                color = R.color.choiceOne;
                break;
            case 1:
                color = R.color.choiceTwo;
                break;
            case 2:
                color = R.color.choiceThree;
                break;
            case 3:
                color = R.color.choiceFour;
                break;
            case 4:
                color = R.color.choiceFive;
                break;
            default:
                color = Color.GRAY;
        }
        holder.colorLabel.setColorFilter(ContextCompat.getColor(ctx, color));
    }

    @Override
    public int getItemCount() {
        return mLegendItems.size();
    }

    public static class LegendItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemTextView;
        private ImageView colorLabel;
        public LegendItemViewHolder(View v) {
            super(v);
            itemTextView = (TextView) v.findViewById(R.id.item_text_view);
            colorLabel = (ImageView) v.findViewById(R.id.color_label);
        }
    }
}

class ResponseListAdapter extends RecyclerView.Adapter<ResponseListAdapter.ResponseViewHolder> {

    private ArrayList<Response> mResponses;

    public ResponseListAdapter(ArrayList<Response> responses) {
        this.mResponses = responses;
    }

    @Override
    public void onBindViewHolder(ResponseViewHolder holder, int position) {
        holder.responseView.setText(mResponses.get(position).getResponse());
        holder.authorView.setText(mResponses.get(position).getAuthor());

        Date date = new Date(mResponses.get(position).getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("EEEEEE M/d h:mm a", Locale.US);
        String dateFormatted = formatter.format(date);
        holder.timeView.setText(dateFormatted);
    }

    @Override
    public int getItemCount() {
        return mResponses.size();
    }

    @Override
    public ResponseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View responseItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.response_item, parent, false);
        return new ResponseViewHolder(responseItem);
    }

    public static class ResponseViewHolder extends RecyclerView.ViewHolder {
        private TextView responseView;
        private TextView authorView;
        private TextView timeView;
        public ResponseViewHolder(View v) {
            super(v);
            responseView = (TextView) v.findViewById(R.id.response_item_text_view);
            authorView = (TextView) v.findViewById(R.id.response_item_author);
            timeView = (TextView) v.findViewById(R.id.response_item_time);
        }
    }
}