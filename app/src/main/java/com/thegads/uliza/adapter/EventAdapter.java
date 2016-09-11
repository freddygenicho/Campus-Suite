package com.thegads.uliza.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thegads.uliza.R;
import com.thegads.uliza.helper.GlideCircleTransform;
import com.thegads.uliza.model.Event;
import com.thegads.uliza.util.ImageUtil;

import java.util.List;

/**
 * Created by Freddy Genicho on 6/2/2016.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Event> eventList;
    private ColorGenerator generator;
    private int color;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.eventList = eventList;
        this.generator = ColorGenerator.MATERIAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.event_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Event event = eventList.get(position);
        holder.title.setText(event.getTitle());
        holder.details.setText(event.getDetails());
        holder.date.setText(event.getDate());
        holder.time.setText(event.getTime());
        holder.name.setText(event.getSenderName());
        holder.background.setBackgroundColor(generator.getRandomColor());
        color = generator.getRandomColor();
        String text = String.valueOf(event.getSenderName().charAt(0));
        TextDrawable drawable = TextDrawable.builder().buildRound(text, color);

        if (event.getEventImage() == null) {
            holder.bg_image.setBackgroundColor(color);
            if (event.getSenderImage() != null) {
                Glide.with(context).load(event.getSenderImage())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .thumbnail(0.1f)
                        .listener(ImageUtil.requestListener)
                        .into(holder.bg_image);
            }
        } else {
            Glide.with(context).load(event.getEventImage())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .thumbnail(0.1f)
                    .listener(ImageUtil.requestListener)
                    .into(holder.bg_image);
        }

        Glide.with(context).load(event.getSenderImage())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(ImageUtil.requestListener)
                .error(drawable)
                .transform(new GlideCircleTransform(context))
                .into(holder.profile);
    }

    public void setEvents(List<Event> eventList) {
        if (eventList.size() > 0) {
            this.eventList = eventList;
            notifyItemRangeChanged(0, eventList.size());
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout background;
        CardView cardView;
        ImageView bg_image, profile;
        TextView title, details, time, date, name;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            background = (RelativeLayout) itemView.findViewById(R.id.event_background);
            title = (TextView) itemView.findViewById(R.id.title);
            name = (TextView) itemView.findViewById(R.id.name);
            details = (TextView) itemView.findViewById(R.id.event_details);
            time = (TextView) itemView.findViewById(R.id.event_time);
            date = (TextView) itemView.findViewById(R.id.event_date);
            profile = (ImageView) itemView.findViewById(R.id.profile);
            bg_image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
