package com.thegads.uliza.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thegads.uliza.R;
import com.thegads.uliza.model.Comment;

import java.util.List;

/**
 * Created by Freddy Genicho on 6/4/2016.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> commentList;
    private LayoutInflater inflater;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.inflater = LayoutInflater.from(context);
        this.commentList = commentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.comment_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.name.setText(comment.getName());
        holder.time.setText(comment.getTime());
        holder.message.setText(comment.getMessage());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, message;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.user_name_comment);
            time = (TextView) itemView.findViewById(R.id.comment_time);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }
}
