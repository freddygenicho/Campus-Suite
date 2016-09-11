package com.thegads.uliza.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thegads.uliza.R;
import com.thegads.uliza.model.MyRoom;

import java.util.List;

/**
 * Created by Freddy Genicho on 7/1/2016.
 */
public class MyRoomAdapter extends RecyclerView.Adapter<MyRoomAdapter.ViewHolder> {
    private List<MyRoom> myRoomList;
    private LayoutInflater inflater;

    public MyRoomAdapter(Context context, List<MyRoom> myRoomList) {
        this.myRoomList = myRoomList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_room, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyRoom room = myRoomList.get(position);
        holder.hostel_name.setText(room.getHostelName());
        holder.time.setText(String.format("%d", room.getTime()));
        holder.confirmation.setText(room.getConfirmationCode());
    }

    @Override
    public int getItemCount() {
        return myRoomList.size();
    }

    public void setRooms(List<MyRoom> myRoomList) {
        if (myRoomList.size()>0){
            this.myRoomList = myRoomList;
            notifyItemRangeChanged(0, myRoomList.size());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView hostel_name;
        TextView time;
        TextView confirmation;

        public ViewHolder(View itemView) {
            super(itemView);
            hostel_name = (TextView) itemView.findViewById(R.id.hostel_name);
            time = (TextView) itemView.findViewById(R.id.time);
            confirmation = (TextView) itemView.findViewById(R.id.confirm_code);
        }
    }
}
