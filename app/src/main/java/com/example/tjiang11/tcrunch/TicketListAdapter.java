package com.example.tjiang11.tcrunch;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tjiang11.tcrunch.models.Ticket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tjiang11 on 12/27/16.
 */

public class TicketListAdapter extends RecyclerView.Adapter<TicketListAdapter.ViewHolder> {
    private ArrayList<Ticket> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView testView = (TextView) v.findViewById(R.id.test_text);
                    TextView ticketTime = (TextView) v.findViewById(R.id.ticket_card_time);
                    Log.i("Click", "Click ticket " + testView.getText().toString() + " id: " + testView);
                }
            });
        }
    }

    public TicketListAdapter(ArrayList<Ticket> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView view = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_list_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView textView = (TextView) holder.cardView.findViewById(R.id.test_text);
        textView.setText(mDataset.get(position).getQuestion());

        TextView ticketTime = (TextView) holder.cardView.findViewById(R.id.ticket_card_time);

        Date date = new Date(mDataset.get(position).getStartTime());
        SimpleDateFormat formatter = new SimpleDateFormat("EEEEEE M/d h:mm a", Locale.US);
        String dateFormatted = formatter.format(date);

        ticketTime.setText(dateFormatted);
    }


}
