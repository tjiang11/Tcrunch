package com.example.tjiang11.tcrunch;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tjiang11.tcrunch.models.Ticket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by tjiang11 on 1/6/17.
 */

public class TicketSection extends StatelessSection {

    private String title;
    private ArrayList<Ticket> mDataset;
    private ItemClickListener clickListener;

    public TicketSection(String title, ArrayList<Ticket> tickets, ItemClickListener itc) {
        super(R.layout.ticket_section_header, R.layout.ticket_list_card);
        this.title = title;
        this.mDataset = tickets;
        this.clickListener = itc;
    }

    @Override
    public int getContentItemsTotal() {
        return mDataset.size();
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new TicketHeaderViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        if (mDataset.size() >= 1) {
            return new TicketItemViewHolder(view);
        } else {
            View emptyView = LayoutInflater.from(view.getContext()).inflate(R.layout.empty_section, (ViewGroup) view.getParent(), false);
            return new EmptySectionViewHolder(emptyView);
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        TicketHeaderViewHolder mViewHolder = (TicketHeaderViewHolder) holder;
        mViewHolder.header.setText(title);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mDataset.size() == 0) {
            EmptySectionViewHolder emptySectionViewHolder = (EmptySectionViewHolder) holder;
            emptySectionViewHolder.emptySectionMessage.setText("You have no "
                + this.title.toLowerCase() + " tickets.");
        } else {
            if (holder instanceof EmptySectionViewHolder) return;
            TicketItemViewHolder mViewHolder = (TicketItemViewHolder) holder;
            TextView textView = (TextView) mViewHolder.cardView.findViewById(R.id.test_text);
            textView.setText(mDataset.get(position).getQuestion());

            TextView ticketTime = (TextView) mViewHolder.cardView.findViewById(R.id.ticket_card_time);

            Date date = new Date(mDataset.get(position).getStartTime());
            SimpleDateFormat formatter = new SimpleDateFormat("EEEEEE M/d h:mm a", Locale.US);
            String dateFormatted = formatter.format(date);

            ticketTime.setText(dateFormatted);
        }
    }

    public class TicketItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        public TicketItemViewHolder(View v) {
            super(v);
            cardView = (CardView) v;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (TicketSection.this.clickListener != null) {
                TicketSection.this.clickListener.onClick(v, getAdapterPosition(), TicketSection.this.title.toLowerCase());
            }
        }
    }

    public static class TicketHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView header;
        public TicketHeaderViewHolder(View v) {
            super(v);
            header = (TextView) v.findViewById(R.id.header_title);
        }
    }

    public static class EmptySectionViewHolder extends RecyclerView.ViewHolder {
        private TextView emptySectionMessage;
        public EmptySectionViewHolder(View v) {
            super(v);
            emptySectionMessage = (TextView) v.findViewById(R.id.empty_section_message);
        }
    }
}

