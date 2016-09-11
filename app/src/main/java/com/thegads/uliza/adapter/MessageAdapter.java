package com.thegads.uliza.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thegads.uliza.R;
import com.thegads.uliza.interfaces.RecyclerView_OnClickListener;
import com.thegads.uliza.model.NewMessage;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<NewMessage> messageList;
    private static RecyclerView_OnClickListener.OnClickListener onClickListener;
    private Resources res;

    public MessageAdapter(Context context, List<NewMessage> messageList) {
        this.messageList = messageList;
        this.res = context.getResources();
    }

    public void setMessage(List<NewMessage> message) {
        this.messageList = message;
        notifyItemRangeChanged(0,messageList.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView from, time, body;
        CardView cardView;

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            from = (TextView) v.findViewById(R.id.sender);
            time = (TextView) v.findViewById(R.id.time);
            body = (TextView) v.findViewById(R.id.content);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null) {
                        onClickListener.OnItemClick(view, getPosition());
                    }
                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewMessage message = messageList.get(position);

        String chatId = formatTopic(message.getChatId());

        if (message.getRead() == 1) {
            holder.from.setText(chatId);
            holder.body.setText(message.getBody());
            holder.time.setText(message.getTime());
        } else {
            holder.cardView.setCardBackgroundColor(res.getColor(R.color.bg_bubble_self));
            holder.from.setTextColor(res.getColor(R.color.primary_text));
            holder.body.setTextColor(res.getColor(R.color.primary_text));
            holder.time.setTextColor(res.getColor(R.color.primary_text));

            holder.from.setText(chatId);
            holder.body.setText(message.getBody());
            holder.time.setText(message.getTime());
        }


    }

    private static String formatTopic(String academic_year) {
        String[] parts = academic_year.split("/");
        return parts[2].replaceAll("_", " ");
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void setOnClickListener(RecyclerView_OnClickListener.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
