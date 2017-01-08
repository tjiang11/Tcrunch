package com.example.tjiang11.tcrunch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tjiang11.tcrunch.models.Response;

import java.util.ArrayList;

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
        public ResponseViewHolder(View v) {
            super(v);
            responseView = (TextView) v.findViewById(R.id.response_item_text_view);
            authorView = (TextView) v.findViewById(R.id.response_item_author);
        }
    }
}
