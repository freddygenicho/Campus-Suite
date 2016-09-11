package com.thegads.uliza.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.thegads.uliza.R;
import com.thegads.uliza.adapter.HostelAdapter;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.fragment.BookFragment;
import com.thegads.uliza.model.Hostel;
import com.thegads.uliza.model.User;
import com.thegads.uliza.util.ActionManager;
import com.thegads.uliza.util.Config;
import com.thegads.uliza.util.InternetManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomActivity extends AppCompatActivity implements BookFragment.BookListener {
    private static final String HOSTEL_LIST = "hostelList";
    public static final String HOSTEL = "hostel";
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private String userName;
    private String userId;
    private String userReg;
    private List<Hostel> hostelList;
    private LinearLayout connections_layout;
    private TextView no_data_view;

    private String location;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(HOSTEL_LIST, (ArrayList<? extends Parcelable>) hostelList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        this.hostelList = new ArrayList<>();
        mDatabase = MyApplication.getInstance().getFirebaseDatabase().getReference("hostels");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpSpinner(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        connections_layout = (LinearLayout) findViewById(R.id.internet_error);
        assert connections_layout != null;
        connections_layout.setVisibility(View.GONE);
        no_data_view = (TextView) findViewById(R.id.no_data_view);
        assert no_data_view != null;
        no_data_view.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.hostel_recycler);
        progressBar = (ProgressBar) findViewById(R.id.hostel_progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.room_swipe);

        User user = MyApplication.getInstance().getPrefManager().getUser();
        if (user != null) {
            userName = user.getName();
            userId = user.getId();
            if (user.getRegNo() != null) {
                userReg = user.getRegNo();
            } else {
                userReg = "";
            }

        }

        HostelAdapter adapter = new HostelAdapter(this, hostelList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (InternetManager.isConnected(getApplicationContext())) {
                    loadDataFromServer(location);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (savedInstanceState != null) {
            if (hostelList.size() > 0) {
                hostelList.clear();
            }
            hostelList = savedInstanceState.getParcelableArrayList(HOSTEL_LIST);
            updateDisplay(hostelList);
        } else {
            if (InternetManager.isConnected(this)) {
                loadDataFromServer(location);
            } else {
                loadDataFromDb();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (hostelList.size() > 0) {
                hostelList.clear();
            }
            hostelList = savedInstanceState.getParcelableArrayList(HOSTEL_LIST);
            updateDisplay(hostelList);
        }
    }

    public void setUpSpinner(Toolbar toolbar) {
        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        final String[] hostels = getResources().getStringArray(R.array.hostels);
        spinner.setAdapter(new MyAdapter(toolbar.getContext(), hostels));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = hostels[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    private void loadDataFromDb() {
        this.hostelList.clear();
        hostelList = Hostel.listAll(Hostel.class);
        if (hostelList.size() > 0) {
            updateDisplay(hostelList);
        } else {
            if (!InternetManager.isConnected(this)) {
                displayNoConnection();
            }
        }
    }

    private void displayNoConnection() {
        progressBar.setVisibility(View.INVISIBLE);
        connections_layout.setVisibility(View.VISIBLE);
        no_data_view.setVisibility(View.GONE);
    }

    public void updateDisplay(final List<Hostel> hostels) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressBar.setVisibility(View.GONE);
        HostelAdapter adapter = new HostelAdapter(this, hostels);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new HostelAdapter.ItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.book_btn:
                        openBookDialog(hostels.get(position));
                        break;
                    case R.id.more_btn:
                        openMoreActivity(hostels.get(position));
                        break;
                    case R.id.btn_call:
                        initCall(hostels.get(position));
                        break;
                    case R.id.btn_message:
                        sendMessage(hostels.get(position));
                        break;

                    case R.id.hostel_img:
                        openMoreActivity(hostels.get(position));
                        break;
                }
            }
        });
    }

    private void loadDataFromServer(String location) {
        this.hostelList.clear();
        final ChildEventListener mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBar.setVisibility(View.GONE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                populateAdapter(dataSnapshot);
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
                Toast.makeText(getApplicationContext(), "Failed to load post", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.child("hostels").child(location).addChildEventListener(mChildEventListener);
    }

    private void populateAdapter(DataSnapshot dataSnapshot) {
        Hostel hostel = dataSnapshot.getValue(Hostel.class);
        hostelList.add(hostel);
        if (hostelList.size() > 0) {
            updateDisplay(hostelList);
            saveToDb(hostelList);
        } else {
            no_data_view.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            connections_layout.setVisibility(View.GONE);
        }
    }

    private void saveToDb(List<Hostel> hostelList) {
        Hostel.deleteAll(Hostel.class);
        for (Hostel hostel : hostelList) {
            hostel.save();
        }
    }

    private void initCall(Hostel hostel) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        String phoneNumber = hostel.getCaretakerNumber();
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Sorry! No app found to complete this action", Toast.LENGTH_LONG).show();
        }
    }

    public void sendMessage(Hostel hostel) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + hostel.getCaretakerNumber()));  // This ensures only SMS apps respond
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Sorry! No app found to complete this action", Toast.LENGTH_LONG).show();
        }
    }

    private void openMoreActivity(Hostel hostel) {
        Intent intent = new Intent(this, HostelActivity.class);
        intent.putExtra(HOSTEL, (Serializable) hostel);
        startActivity(intent);
    }

    private void openBookDialog(Hostel hostel) {
        DialogFragment dialogFragment = BookFragment.newInstance(hostel.getUid(), hostel.getName(), userId, userName, userReg);
        dialogFragment.show(getSupportFragmentManager(), "book_fragment");
    }

    private void saveNewHostel() {
        String key = mDatabase.child("hostels").push().getKey();
        Hostel hostel = new Hostel(key, "Hillview Hostels", "Kabianga Market", "Ksh 5000/ Per Sem",
                "Rooms are spacious and are accommodated by four room mates",
                "http://apod.nasa.gov/apod/image/0412/strange_pryde_big.jpg", "John Kibet", "+254716010434", 20);
        Map<String, Object> postValues = hostel.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/hostels/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hostel_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_my_room:
                launchMyRoomActivity();
                break;
            case R.id.action_terms_and_conditions:
                String url = getString(R.string.term_and_conditions_url);
                ActionManager.openWebPage(this, url);
                break;
            case R.id.action_payment:
                launchPaymentMethodActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchPaymentMethodActivity() {
        // Intent intent = new Intent(this,MyRoomActivity.class);
        //startActivity(intent);
    }

    private void launchMyRoomActivity() {
        //Intent intent = new Intent(this,PaymentMethodActivity.class);
        //startActivity(intent);
    }

    @Override
    public void success(String success) {
        Toast.makeText(this, success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failed(String failed) {
        Toast.makeText(this, failed, Toast.LENGTH_SHORT).show();
    }


    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Resources.Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Resources.Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }

}
