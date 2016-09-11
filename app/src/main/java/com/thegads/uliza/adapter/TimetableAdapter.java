package com.thegads.uliza.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.thegads.uliza.R;
import com.thegads.uliza.model.Timetable;

import java.util.List;

/**
 * Created by Freddy Genicho on 6/3/2016.
 */
public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Timetable> timetables;
    private ItemClickListener itemClickListener;
    private ColorGenerator generator;

    public TimetableAdapter(Context context, List<Timetable> timetables) {
        this.inflater = LayoutInflater.from(context);
        this.timetables = timetables;
        this.generator = ColorGenerator.MATERIAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.timetable_view, parent, false);
        return new ViewHolder(view);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setTimetable(List<Timetable> timetableList) {
        this.timetables = timetableList;
        notifyItemRangeChanged(0, timetableList.size());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Timetable timetable = timetables.get(position);
        int color = generator.getRandomColor();
        holder.unit.setText(String.format("%s - %s", timetable.getUnitCode(), timetable.getUnitName()));
        holder.background.setBackgroundColor(color);
        holder.day.setText(timetable.getDay());
        holder.time.setText(String.format("%s - %s", timetable.getStartTime(), timetable.getStopTime()));
        holder.venue.setText(timetable.getVenue());
        holder.lec_name.setText(timetable.getLecturerName());
        holder.lec_contact.setText(timetable.getLecturerContacts());
        holder.lec_email.setText(timetable.getLecturerEmail());
    }

    @Override
    public int getItemCount() {
        return timetables.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView unit, time, day, venue, lec_name, lec_contact, lec_email;
        LinearLayout background;

        public ViewHolder(View itemView) {
            super(itemView);
            unit = (TextView) itemView.findViewById(R.id.unit);
            time = (TextView) itemView.findViewById(R.id.time);
            day = (TextView) itemView.findViewById(R.id.day);
            venue = (TextView) itemView.findViewById(R.id.venue);
            lec_name = (TextView) itemView.findViewById(R.id.lec_name);
            lec_contact = (TextView) itemView.findViewById(R.id.lec_contacts);
            lec_email = (TextView) itemView.findViewById(R.id.lec_email);
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
