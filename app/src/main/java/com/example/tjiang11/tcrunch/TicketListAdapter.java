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

/**
 * Created by tjiang11 on 12/27/16.
 */

public class TicketListAdapter extends RecyclerView.Adapter<TicketListAdapter.ViewHolder> {
    private Ticket[] mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView testView = (TextView) v.findViewById(R.id.test_text);
                    Log.i("Click", "Click ticket " + testView.getText().toString() + " id: " + v.getId());
                }
            });
        }
    }

    public TicketListAdapter(Ticket[] mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView view = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_list_card, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView textView = (TextView) holder.cardView.findViewById(R.id.test_text);
        textView.setText(mDataset[position].getQuestion());
    }
}
