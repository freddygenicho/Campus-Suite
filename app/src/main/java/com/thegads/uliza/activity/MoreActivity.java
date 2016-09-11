package com.thegads.uliza.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thegads.uliza.R;
import com.thegads.uliza.adapter.CommentAdapter;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.helper.GlideCircleTransform;
import com.thegads.uliza.helper.SimpleDividerItemDecoration;
import com.thegads.uliza.model.Comment;
import com.thegads.uliza.model.Post;
import com.thegads.uliza.model.User;
import com.thegads.uliza.util.ImageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoreActivity extends AppCompatActivity {

    public static final String DATA_MODEL = "post";
    private Post post;

    RelativeLayout post_image_layout;
    ProgressBar loading_pb;
    LinearLayout error_layout, top;
    TextView user_name, user_title;
    ImageView user_pic, post_image;
    TextView post_body, post_title, post_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        post = (Post) getIntent().getSerializableExtra(DATA_MODEL);

        DatabaseReference mRef = MyApplication.getInstance().getFirebaseDatabase().getReference();
        mRef.keepSynced(true);


        error_layout = (LinearLayout) findViewById(R.id.error_layout);
        post_image_layout = (RelativeLayout) findViewById(R.id.post_image_layout);
        loading_pb = (ProgressBar) findViewById(R.id.loading_progressBar);
        top = (LinearLayout) findViewById(R.id.top);
        user_pic = (ImageView) findViewById(R.id.user_pic);
        post_image = (ImageView) findViewById(R.id.post_image);
        user_name = (TextView) findViewById(R.id.user_name);
        user_title = (TextView) findViewById(R.id.user_title);
        post_body = (TextView) findViewById(R.id.post_body);
        post_title = (TextView) findViewById(R.id.post_title);
        post_date = (TextView) findViewById(R.id.post_date);

        if (post.getPostImage() == null) {
            post_image_layout.setVisibility(View.GONE);
        } else {
            assert post_image != null;
            Glide.with(this).load(post.getPostImage())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            loading_pb.setVisibility(View.GONE);
                            error_layout.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            loading_pb.setVisibility(View.GONE);
                            error_layout.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(post_image);
        }

        ColorGenerator generator = ColorGenerator.MATERIAL;
        String letter = String.valueOf(post.getName().charAt(0));
        TextDrawable drawable = TextDrawable.builder().buildRound(letter, generator.getRandomColor());

        Glide.with(this).load(post.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .thumbnail(0.1f)
                .listener(ImageUtil.requestListener)
                .error(drawable).override(40, 40)
                .transform(new GlideCircleTransform(this))
                .into(user_pic);

        //noinspection ConstantConditions
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_name.setText(post.getName());
        user_title.setText(post.getSenderTitle());
        post_title.setText(post.getTitle());
        post_body.setText(post.getBody());
        post_date.setText(post.getDate());

    }


    public void share(Post post) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        String message = post.getTitle() + "\n" + post.getBody() + "\n" + post.getDate();
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("text/plain");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_share:
                share(post);
                return true;
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
