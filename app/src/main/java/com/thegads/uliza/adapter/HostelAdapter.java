package com.thegads.uliza.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.thegads.uliza.R;
import com.thegads.uliza.model.Hostel;
import com.thegads.uliza.util.ImageUtil;

import java.util.List;

/**
 * Created by Freddy Genicho on 5/29/2016.
 */
public class HostelAdapter extends RecyclerView.Adapter<HostelAdapter.ViewHolder> {

    private List<Hostel> hostels;
    private LayoutInflater inflater;
    private Context context;
    private ItemClickListener itemClickListener;

    public HostelAdapter(Context context, List<Hostel> hostels) {
        this.inflater = LayoutInflater.from(context);
        this.hostels = hostels;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.hostel_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Hostel hostel = hostels.get(position);
        holder.hostel_name.setText(hostel.getName());
        holder.hostel_description.setText(hostel.getDescription());
        holder.hostel_rent.setText(hostel.getRent());
        holder.hostel_location.setText(hostel.getLocation());
        holder.caretaker_name.setText(hostel.getCaretakerName());
        holder.caretaker_number.setText(hostel.getCaretakerNumber());
        if (hostel.getAvailable() == 0) {
            holder.availableRooms.setText("Fully Occupied");
            holder.availableRooms.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.availableRooms.setText(hostel.getAvailable());
        }
        Glide.with(context)
                .load(hostel.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.loading_pb.setVisibility(View.GONE);
                        holder.error_layout.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.loading_pb.setVisibility(View.GONE);
                        holder.error_layout.setVisibility(View.GONE);
                        return false;
                    }
                })
                .crossFade()
                .into(holder.hostel_image);
    }

    @Override
    public int getItemCount() {
        return hostels.size();
    }

    public void setHostels(List<Hostel> hostels) {
        this.hostels = hostels;
        notifyItemRangeChanged(0, hostels.size());
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView hostel_name;
        TextView availableRooms;
        TextView hostel_location;
        TextView hostel_rent;
        TextView hostel_description;
        TextView caretaker_name;
        TextView caretaker_number;
        ImageView btn_call;
        ImageView btn_message;
        Button see_more_btn;
        Button book_btn;
        ImageView hostel_image;
        ProgressBar loading_pb;
        LinearLayout error_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            error_layout = (LinearLayout) itemView.findViewById(R.id.error_layout);
            loading_pb = (ProgressBar) itemView.findViewById(R.id.loading_progressBar);
            hostel_name = (TextView) itemView.findViewById(R.id.hostel_name);
            hostel_location = (TextView) itemView.findViewById(R.id.hostel_location);
            hostel_rent = (TextView) itemView.findViewById(R.id.hostel_rent);
            hostel_description = (TextView) itemView.findViewById(R.id.hostel_description);
            hostel_image = (ImageView) itemView.findViewById(R.id.hostel_img);
            caretaker_name = (TextView) itemView.findViewById(R.id.caretaker_name);
            caretaker_number = (TextView) itemView.findViewById(R.id.care_taker_number);
            see_more_btn = (Button) itemView.findViewById(R.id.more_btn);
            book_btn = (Button) itemView.findViewById(R.id.book_btn);
            btn_call = (ImageView) itemView.findViewById(R.id.btn_call);
            btn_message = (ImageView) itemView.findViewById(R.id.btn_message);
            availableRooms = (TextView) itemView.findViewById(R.id.available_rooms);

            hostel_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(v, getPosition());
                    }
                }
            });


            book_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(v, getPosition());
                    }
                }
            });

            see_more_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(v, getPosition());
                    }
                }
            });

            btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(v, getPosition());
                    }
                }
            });

            btn_message.setOnClickListener(new View.OnClickListener() {
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
