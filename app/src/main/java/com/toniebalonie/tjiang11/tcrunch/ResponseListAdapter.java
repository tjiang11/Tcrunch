package com.toniebalonie.tjiang11.tcrunch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toniebalonie.tjiang11.tcrunch.models.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tjiang11 on 1/7/17.
 */

public class ResponseListAdapter extends RecyclerView.Adapter<ResponseListAdapter.ResponseViewHolder> {

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
