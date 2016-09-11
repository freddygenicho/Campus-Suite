package com.thegads.uliza.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thegads.uliza.R;
import com.thegads.uliza.activity.SettingsActivity;
import com.thegads.uliza.activity.TimetableActivity;
import com.thegads.uliza.adapter.TimetableAdapter;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.model.Timetable;
import com.thegads.uliza.model.User;
import com.thegads.uliza.service.NotifyService;
import com.thegads.uliza.util.InternetManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Freddy Genicho on 8/16/2016.
 */
public class TimetableFragment extends Fragment implements View.OnClickListener {

    public static final String TIMETABLE = "timetable";
    private static final String TIME_TABLE_LIST = "timetableList";
    private AppCompatActivity activity;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout connections_layout;
    // TextView no_data_view;
    CardView no_timetable;
    private String reg;
    private DatabaseReference mDatabase;
    private String academic_year;
    private String semester;
    private ProgressDialog dialog;
    private List<Timetable> timetableList;
    private User user;
    private View cardView;

    public TimetableFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (AppCompatActivity) getActivity();
        this.timetableList = new ArrayList<>();
        user = MyApplication.getInstance().getPrefManager().getUser();
        this.mDatabase = MyApplication.getInstance().getFirebaseDatabase().getReference();
        this.reg = user.getRegNo();
        this.academic_year = formatAcademicYear(MyApplication.getInstance().getPrefManager().getAcademicYear());
        this.semester = MyApplication.getInstance().getPrefManager().getSemester();
        this.dialog = new ProgressDialog(activity);
        dialog.setMessage("Refreshing...");
        dialog.setIndeterminate(true);
        //saveToTimetable();
        //setAlarm();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TIME_TABLE_LIST, (ArrayList<? extends Parcelable>) timetableList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTimetable(view);
        if (savedInstanceState != null) {
            timetableList = savedInstanceState.getParcelableArrayList(TIME_TABLE_LIST);
            updateDisplay(timetableList);
        } else {
            if (isRegSet()) {
                cardView.setVisibility(View.GONE);
                connections_layout.setVisibility(View.GONE);
                updateTimetable();

            } else {
                cardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

        }
    }

    public boolean isRegSet() {
        return reg != null && !reg.trim().isEmpty() && reg.length() > 3;
    }

    public void initTimetable(View view) {
        connections_layout = (LinearLayout) view.findViewById(R.id.internet_error);
        connections_layout.setVisibility(View.GONE);
        no_timetable = (CardView) view.findViewById(R.id.no_data_card);
        cardView = view.findViewById(R.id.reg_set);
        Button button = (Button) view.findViewById(R.id.set_btn);
        Button refreshBtn = (Button) view.findViewById(R.id.refresh_btn);
        Button retry = (Button) view.findViewById(R.id.retry);
        button.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.timetable_swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                R.color.youtube, R.color.colorPrimaryDark);
        recyclerView = (RecyclerView) view.findViewById(R.id.timetable_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        progressBar = (ProgressBar) view.findViewById(R.id.timetable_progressBar);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTimetable();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (InternetManager.isConnected(activity)) {
                    updateTimetable();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(activity, "Can't load data. Check your connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            timetableList = savedInstanceState.getParcelableArrayList(TIME_TABLE_LIST);
            updateDisplay(timetableList);
        } else {
            if (isRegSet()) {
                cardView.setVisibility(View.GONE);
                connections_layout.setVisibility(View.GONE);
                updateTimetable();
            } else {
                cardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void updateTimetable() {
        if (InternetManager.isConnected(activity)) {
            this.timetableList.clear();
            final ChildEventListener mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    progressBar.setVisibility(View.INVISIBLE);
                    loadData(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(activity, "Failed to load post", Toast.LENGTH_SHORT).show();
                }
            };

            mDatabase.child("timetable").child(academic_year).child(semester).child(regInitial(reg)).child(user.getYear()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mDatabase.child("timetable").child(academic_year).child(semester).child(regInitial(reg)).child(user.getYear()).addChildEventListener(mChildEventListener);
                    } else {
                        displayNoResultsLayout();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (databaseError != null) {
                        Toast.makeText(activity, databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            loadDataFromDb();
        }
    }

    private void displayNoResultsLayout() {
        progressBar.setVisibility(View.GONE);
        no_timetable.setVisibility(View.VISIBLE);
    }

    private void loadDataFromDb() {
        progressBar.setVisibility(View.GONE);
        no_timetable.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        this.timetableList.clear();
        timetableList = Timetable.listAll(Timetable.class);
        if (timetableList.size() > 0) {
            updateDisplay(timetableList);
        } else {
            if (!InternetManager.isConnected(activity)) {
                displayConnectionError();
            }
        }
    }

    private void displayConnectionError() {
        progressBar.setVisibility(View.INVISIBLE);
        connections_layout.setVisibility(View.VISIBLE);
        no_timetable.setVisibility(View.GONE);
    }

    private void loadData(DataSnapshot dataSnapshot) {
        Timetable timetable = dataSnapshot.getValue(Timetable.class);
        timetableList.add(timetable);
        if (timetableList.size() > 0) {
            updateDisplay(timetableList);
            saveToDb(timetableList);
        } else {
            no_timetable.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            connections_layout.setVisibility(View.GONE);
        }
    }

    private void saveToDb(List<Timetable> timetableList) {
        Timetable.deleteAll(Timetable.class);
        for (Timetable timetable : timetableList) {
            timetable.save();
        }
    }

    private void updateDisplay(final List<Timetable> timetableList) {
        TimetableAdapter adapter = new TimetableAdapter(activity, timetableList);
        recyclerView.setAdapter(adapter);
        adapter.setTimetable(timetableList);
        adapter.setItemClickListener(new TimetableAdapter.ItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.background:
                        Intent intent = new Intent(activity, TimetableActivity.class);
                        Timetable timetable = timetableList.get(position);
                        intent.putExtra(TIMETABLE, (Serializable) timetable);
                        activity.startActivity(intent);
                        break;
                }
            }
        });
    }

    private void saveToTimetable() {
        Timetable timetable = new Timetable("Introduction to Website Development", "BIT 320", "Monday", "11:00Am",
                "2:00 PM", "LH1/LTB3", "Omari", "071602345", "Omari@gmail.com", "Clifford", "089742345");
        HashMap<String, Object> childUpdate = timetable.toMap();
        mDatabase.child("timetable").child(academic_year).child(semester).child(regInitial(reg)).child(user.getYear()).push().updateChildren(childUpdate);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.set_btn) {
            Intent intent = new Intent(activity, SettingsActivity.class);
            activity.startActivity(intent);
        }

        if (v.getId() == R.id.refresh_btn) {
            dialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    if (isRegSet()) {
                        updateTimetable();
                    } else {
                        Toast.makeText(activity, "Your registration number is not available or is invalid", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 2000);
        }
    }

    private String formatAcademicYear(String academic_year) {
        String[] parts = academic_year.split("/");
        String part1 = parts[0];
        String part2 = parts[1];
        return part1 + "-" + part2;
    }

    private String regInitial(String reg) {
        String[] parts = reg.split("/");
        return parts[0];
    }

    public void setAlarm() {
        Intent intent = new Intent(activity, NotifyService.class);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(activity, 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        Toast.makeText(activity, "Start Alarm", Toast.LENGTH_LONG).show();
    }
}
