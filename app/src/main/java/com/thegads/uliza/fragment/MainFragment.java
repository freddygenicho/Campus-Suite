package com.thegads.uliza.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.thegads.uliza.R;
import com.thegads.uliza.adapter.PostAdapter;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.model.NewMessage;
import com.thegads.uliza.model.Post;
import com.thegads.uliza.util.Config;
import com.thegads.uliza.util.InternetManager;
import com.thegads.uliza.util.MessageNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;

/**
 * Created by Freddy Genicho on 6/3/2016.
 */
public class MainFragment extends Fragment {

    private static final String TAG = Fragment.class.getSimpleName();
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    public static List<Post> postList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout connections_layout;
    private LinearLayoutManager mLayoutManager;
    private PostAdapter adapter;

    private boolean userScrolled = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private static RelativeLayout bottomLayout;
    private Context context;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        this.mDatabase = MyApplication.getInstance().getFirebaseDatabase().getReference("posts");
        postList = new ArrayList<>();
        this.mLayoutManager = new LinearLayoutManager(context);
        this.adapter = new PostAdapter(context, postList);
        //writeNewPost();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPosts(view);
    }

    public void initPosts(View view) {
        bottomLayout = (RelativeLayout) view.findViewById(R.id.loadItemsLayout_recyclerView);
        connections_layout = (LinearLayout) view.findViewById(R.id.internet_error);
        connections_layout.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.post_swipe);
        progressBar = (ProgressBar) view.findViewById(R.id.posts_progressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.posts_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        Button button = (Button) view.findViewById(R.id.retry);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromServer();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                R.color.youtube, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (InternetManager.isConnected(context)) {
                    loadDataFromServer();
                } else {
                    Toast.makeText(context, "Can't load data. Check your connection", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });

        //implementScrollListener();

        if (InternetManager.isConnected(context)) {
            loadDataFromServer();
        } else {
            loadDataFromDb();
        }

    }


    private void loadDataFromServer() {
        this.postList.clear();
        progressBar.setVisibility(View.VISIBLE);
        ChildEventListener mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBar.setVisibility(View.GONE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                loadData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.w(Config.TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(context, "Failed to load post", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.orderByChild("timestamp").addChildEventListener(mChildEventListener);
    }

    public void loadData(DataSnapshot dataSnapshot) {
        final Post post = dataSnapshot.getValue(Post.class);
        postList.add(post);
        if (postList.size() > 0) {
            updateDisplay(postList);
            saveToDb(postList);
        } else {
            progressBar.setVisibility(View.GONE);
            connections_layout.setVisibility(View.GONE);
        }
    }

    private void saveToDb(List<Post> postList) {
        Post.deleteAll(Post.class);
        for (Post post : postList) {
            post.save();
        }
    }

    private void loadDataFromDb() {
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        this.postList.clear();
        postList = Post.listAll(Post.class);
        if (postList.size() > 0) {
            updateDisplay(postList);
        } else {
            if (!InternetManager.isConnected(context)) {
                displayConnectionError();
            }
        }

    }

    private void displayConnectionError() {
        progressBar.setVisibility(View.INVISIBLE);
        connections_layout.setVisibility(View.VISIBLE);
    }

    public void updateDisplay(final List<Post> postList) {
        adapter = new PostAdapter(context, postList);
        adapter.setPostList(postList);
        recyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));
    }

    // Implement scroll listener
    private void implementScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // If scroll state is touch scroll then set userScrolled true
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Here get the child count, item count and visible items from layout manager

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                // Now check if userScrolled is true and also check if
                // the item is end then update recycler view and set
                // userScrolled to false
                if (userScrolled && (visibleItemCount + pastVisibleItems) == totalItemCount) {
                    userScrolled = false;
                    updateRecyclerView();
                }
            }
        });
    }

    // Method for repopulating recycler view
    private void updateRecyclerView() {

        if (InternetManager.isConnected(context)) {
            // Show Progress Layout
            bottomLayout.setVisibility(View.VISIBLE);
            postList.clear();
            mDatabase.orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    bottomLayout.setVisibility(View.GONE);
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                    updateDisplay(postList);
                    Log.e(TAG, "loading More post : " + postList.size());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            bottomLayout.setVisibility(View.GONE);
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
            updateScrollViewer(true);
        }
    }

    private void updateScrollViewer(boolean isScrollinng) {

    }

    private void writeNewPost() {
        String key = mDatabase.push().getKey();
        Post post = new Post(key, "Campus Suite version 2.0",
                "This is to announce to all our users that Campus suite version 2.0 will be ready for downloads starting next month." +
                        "Be ready for the coolest features.", "22 July 2016", "200", "Anthony Kiragu", "Student - FANERD CO-CEO",
                "http://apod.nasa.gov/apod/image/0412/strange_pryde_big.jpg",
                "http://apod.nasa.gov/apod/image/0412/strange_pryde_big.jpg");
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }


    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     */
    private void handlePushNotification(Intent intent) {
        NewMessage message = (NewMessage) intent.getSerializableExtra("message");
        String chatRoomId = intent.getStringExtra("chat_room_id");

       /* if (message != null && chatRoomId != null) {
            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
        MessageNotification.clearNotifications();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}
