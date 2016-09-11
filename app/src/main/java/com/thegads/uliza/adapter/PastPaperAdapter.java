package com.thegads.uliza.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thegads.uliza.R;
import com.thegads.uliza.model.PastPaper;

import java.util.List;

/**
 * Created by Freddy Genicho on 6/12/2016.
 */
public class PastPaperAdapter extends RecyclerView.Adapter<PastPaperAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<PastPaper> pastPaperList;
    private ItemClickListener itemClickListener;

    public PastPaperAdapter(Context context, List<PastPaper> pastPaperList) {
        this.inflater = LayoutInflater.from(context);
        this.pastPaperList = pastPaperList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.past_paper_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PastPaper pastPaper = pastPaperList.get(position);
        holder.title.setText(pastPaper.getCode());
        holder.name.setText(pastPaper.getName());
        holder.year.setText(String.format("Year : %s", pastPaper.getYear()));
        holder.academic_year.setText(pastPaper.getAcademic_year());
        holder.semester.setText(String.format("Semester : %s", pastPaper.getSemester()));
    }

    @Override
    public int getItemCount() {
        return pastPaperList.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, name, year, academic_year, semester;
        Button download;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            name = (TextView) itemView.findViewById(R.id.name);
            year = (TextView) itemView.findViewById(R.id.year);
            academic_year = (TextView) itemView.findViewById(R.id.academic_year);
            semester = (TextView) itemView.findViewById(R.id.semester);
            download = (Button) itemView.findViewById(R.id.download);

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.itemClick(v, getPosition());
                    }
                }
            });
        }
    }

    public interface ItemClickListener {
        void itemClick(View v, int position);
    }
}
