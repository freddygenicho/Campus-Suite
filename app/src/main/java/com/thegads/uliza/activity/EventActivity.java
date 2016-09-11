package com.thegads.uliza.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.thegads.uliza.R;
import com.thegads.uliza.adapter.EventAdapter;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.model.Event;
import com.thegads.uliza.util.InternetManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = EventActivity.class.getSimpleName();
    private static final String EVENT_LIST = "event_list";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Event> eventList;
    private LinearLayout connections_layout;
    private TextView no_data_view;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EVENT_LIST, (ArrayList<? extends Parcelable>) eventList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.mDatabase = MyApplication.getInstance().getFirebaseDatabase().getReference();
        this.eventList = new ArrayList<>();
        initView();

        if (savedInstanceState != null) {
            eventList = savedInstanceState.getParcelableArrayList(EVENT_LIST);
            updateUI(eventList);
        } else {
            if (InternetManager.isConnected(getApplicationContext())) {
                loadDataFromServer();
            } else {
                loadFromDb();
            }
        }
    }

    public void initView() {
        connections_layout = (LinearLayout) findViewById(R.id.internet_error);
        connections_layout.setVisibility(View.GONE);
        no_data_view = (TextView) findViewById(R.id.no_data_view);
        no_data_view.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.event_swipe);
        recyclerView = (RecyclerView) findViewById(R.id.event_recycler);
        progressBar = (ProgressBar) findViewById(R.id.event_progressBar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL));
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                R.color.youtube, R.color.colorPrimaryDark);

        connections_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromServer();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (InternetManager.isConnected(getApplicationContext())) {
                    loadDataFromServer();
                } else {
                    Toast.makeText(getApplicationContext(), "Can't load data. Check your connection", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            eventList = savedInstanceState.getParcelableArrayList(EVENT_LIST);
            updateUI(eventList);
        } else {
            if (InternetManager.isConnected(getApplicationContext())) {
                loadDataFromServer();
            } else {
                loadFromDb();
            }
        }
    }

    public void loadDataFromServer() {
        this.eventList.clear();
        final ChildEventListener mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBar.setVisibility(View.INVISIBLE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                updateDisplay(dataSnapshot);
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
                Log.e(TAG, databaseError.getMessage());
            }
        };
        mDatabase.child("events").addChildEventListener(mChildListener);

    }

    public void updateDisplay(DataSnapshot dataSnapshot) {
        Event event = dataSnapshot.getValue(Event.class);
        eventList.add(event);
        if (eventList.size() > 0) {
            updateUI(eventList);
            saveToDB(eventList);
        } else {
            no_data_view.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            connections_layout.setVisibility(View.GONE);
        }
    }

    private void saveToDB(List<Event> eventList) {
        Event.deleteAll(Event.class);
        for (Event event : eventList) {
            event.save();
        }
    }

    public void updateUI(final List<Event> eventList) {
        EventAdapter adapter = new EventAdapter(getApplicationContext(), eventList);
        adapter.setEvents(eventList);
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(adapter);
        adapter.setEvents(eventList);
    }

    private void loadFromDb() {
        progressBar.setVisibility(View.GONE);
        no_data_view.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        this.eventList.clear();
        eventList = Event.listAll(Event.class);
        if (eventList.size() > 0) {
            updateUI(eventList);
        } else {
            if (!InternetManager.isConnected(getApplicationContext())) {
                displayNoConnection();
            }
        }

    }

    private void displayNoConnection() {
        progressBar.setVisibility(View.INVISIBLE);
        connections_layout.setVisibility(View.VISIBLE);
        no_data_view.setVisibility(View.GONE);
    }

    private void writeEvent() {
        String key = mDatabase.child("events").push().getKey();
        Event event = new Event(key, "Cultural Week", "June 1,2016", "8:00 Am", "Commencement of cultural week and all the activities expected",
                "https://cdn.theatlantic.com/assets/media/img/photo/2015/11/images-from-the-2016-sony-world-pho/s01_130921474920553591/main_900.jpg"
                , "https://cdn.theatlantic.com/assets/media/img/photo/2015/11/images-from-the-2016-sony-world-pho/s01_130921474920553591/main_900.jpg",
                "Freddy genicho");
        HashMap<String, Object> eventUpdate = event.toMap();
        mDatabase.child("events").child(key).updateChildren(eventUpdate);
    }

}
