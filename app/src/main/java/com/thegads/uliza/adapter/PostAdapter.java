package com.thegads.uliza.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.thegads.uliza.R;
import com.thegads.uliza.activity.MoreActivity;
import com.thegads.uliza.helper.GlideCircleTransform;
import com.thegads.uliza.interfaces.RecyclerView_OnClickListener;
import com.thegads.uliza.model.Post;
import com.thegads.uliza.util.ExpandableTextView;
import com.thegads.uliza.util.ImageUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Freddy Genicho on 5/25/2016.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<Post> postList;
    private ColorGenerator generator;
    private int lastPosition = 0;
    private Resources res;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.inflater = LayoutInflater.from(context);
        this.generator = ColorGenerator.MATERIAL;
        this.res = context.getResources();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.post_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Post post = postList.get(position);

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);

        holder.user_name.setText(post.getName());
        holder.user_title.setText(post.getSenderTitle());
        holder.post_body.setText(post.getBody());
        holder.post_title.setText(post.getTitle());
        holder.post_date.setText(post.getDate());

        String letter = String.valueOf(post.getName().charAt(0));
        TextDrawable drawable = TextDrawable.builder().buildRound(letter, generator.getRandomColor());
        Glide.with(context).load(post.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .thumbnail(0.1f)
                .listener(ImageUtil.requestListener)
                .error(drawable).override(40, 40)
                .transform(new GlideCircleTransform(context))
                .into(holder.user_pic);

        if (post.getPostImage() == null) {
            holder.post_image_layout.setVisibility(View.GONE);
        } else {
            Glide.with(context).load(post.getPostImage())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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
                    .into(holder.post_image);
        }


        // Implement click listener over layout
        holder.setClickListener(new RecyclerView_OnClickListener.OnClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.top:
                        Intent intent = new Intent(context, MoreActivity.class);
                        intent.putExtra(MoreActivity.DATA_MODEL, (Serializable) post);

                        Pair<View, String> p1 = Pair.create((View) holder.post_image, res.getString(R.string.postImage));
                        Pair<View, String> p2 = Pair.create((View) holder.post_title, res.getString(R.string.postTitle));
                        Pair<View, String> p3 = Pair.create((View) holder.post_body, res.getString(R.string.postContent));
                        Pair<View, String> p4 = Pair.create((View) holder.post_date, res.getString(R.string.dateTime));
                        Pair<View, String> p5 = Pair.create((View) holder.user_pic, res.getString(R.string.profileImage));
                        Pair<View, String> p6 = Pair.create((View) holder.user_name, res.getString(R.string.senderName));
                        Pair<View, String> p7 = Pair.create((View) holder.user_title, res.getString(R.string.senderTitle));

                        ActivityOptionsCompat options =
                                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1, p2, p3, p4, p5, p6, p7);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            context.startActivity(intent, options.toBundle());
                        } else {
                            context.startActivity(intent);
                        }
                        break;
                }
            }

        });


        if (position > lastPosition) {
            com.thegads.uliza.anim.AnimationUtils.animateView(holder, true);
        } else {
            com.thegads.uliza.anim.AnimationUtils.animateView(holder, false);
        }
        lastPosition = position;
    }

    @Override
    public int getItemCount() {
        return (null != postList ? postList.size() : 0);
    }

    public void setPostList(List<Post> postList) {
        if (postList.size() > 0) {
            this.postList = postList;
            notifyItemRangeChanged(0, postList.size());
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout post_image_layout;
        ProgressBar loading_pb;
        LinearLayout error_layout, top;
        TextView user_name, user_title;
        ImageView user_pic, post_image;
        TextView post_body, post_title, post_date;

        private RecyclerView_OnClickListener.OnClickListener onClickListener;

        public MyViewHolder(View itemView) {
            super(itemView);
            error_layout = (LinearLayout) itemView.findViewById(R.id.error_layout);
            post_image_layout = (RelativeLayout) itemView.findViewById(R.id.post_image_layout);
            loading_pb = (ProgressBar) itemView.findViewById(R.id.loading_progressBar);
            top = (LinearLayout) itemView.findViewById(R.id.top);
            user_pic = (ImageView) itemView.findViewById(R.id.user_pic);
            post_image = (ImageView) itemView.findViewById(R.id.post_image);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_title = (TextView) itemView.findViewById(R.id.user_title);
            post_body = (TextView) itemView.findViewById(R.id.post_body);
            post_title = (TextView) itemView.findViewById(R.id.post_title);
            post_date = (TextView) itemView.findViewById(R.id.post_date);

            top.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.top) {
                if (onClickListener != null) {
                    onClickListener.OnItemClick(v, getAdapterPosition());
                }
            }
        }

        // Setter for listener
        public void setClickListener(RecyclerView_OnClickListener.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

    }

}
