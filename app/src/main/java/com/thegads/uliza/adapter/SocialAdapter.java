package com.thegads.uliza.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thegads.uliza.R;

/**
 * Created by Freddy Genicho on 6/26/2016.
 */
public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ItemClickListener itemClickListener;
    private String links[];
    private int colors[];

    public SocialAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.links = context.getResources().getStringArray(R.array.social_title);
        this.colors = context.getResources().getIntArray(R.array.socials_colors);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.social_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.background.setBackgroundColor(colors[position]);
        holder.textView.setText(links[position]);
    }

    @Override
    public int getItemCount() {
        return links.length;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout background;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.links);
            background = (LinearLayout) itemView.findViewById(R.id.background);
            background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(v, getPosition());
                    }
                }
            });
        }
    }

    public interface ItemClickListener {
        void onClick(View v, int position);
    }
}
