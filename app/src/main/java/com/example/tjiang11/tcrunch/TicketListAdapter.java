package com.example.tjiang11.tcrunch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tjiang11.tcrunch.models.Ticket;

/**
 * Created by tjiang11 on 12/27/16.
 */

public class TicketListAdapter extends RecyclerView.Adapter<TicketListAdapter.ViewHolder> {
    private Ticket[] mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout textView;
        public ViewHolder(LinearLayout v) {
            super(v);
            textView = v;
        }
    }

    public TicketListAdapter(Ticket[] mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.test, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView textView = (TextView) holder.textView.findViewById(R.id.test_text);
        textView.setText("HIII");
    }
}
