package com.thegads.uliza.fragment;


import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thegads.uliza.R;
import com.thegads.uliza.adapter.PastPaperAdapter;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.model.PastPaper;
import com.thegads.uliza.model.User;
import com.thegads.uliza.util.Config;
import com.thegads.uliza.util.InternetManager;
import com.thegads.uliza.util.MarshMallowPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PastpaperFragment extends Fragment {
    private static final String PAST_PAPERS = "past_papers";
    private DatabaseReference mDatabaseReference;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<PastPaper> pastPaperList;
    private String degree;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout connections_layout;
    private CardView cardView;
    private AppCompatActivity activity;
    private PastPaper pastPaper;

    public PastpaperFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        this.pastPaperList = new ArrayList<>();
        this.mDatabaseReference = MyApplication.getInstance().getFirebaseDatabase().getReference();
        User user = MyApplication.getInstance().getPrefManager().getUser();
        this.degree = user.getRegNo();
        //savePastPaper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_papers, container, false);
        return view;
    }

    public boolean isRegSet() {
        return degree != null && !degree.trim().isEmpty() && degree.length() > 3;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PAST_PAPERS, (ArrayList<? extends Parcelable>) pastPaperList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null){
            displayNoPastpapersView(false);
            pastPaperList = savedInstanceState.getParcelableArrayList(PAST_PAPERS);
            updateUI(pastPaperList);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        if (savedInstanceState != null){
            pastPaperList = savedInstanceState.getParcelableArrayList(PAST_PAPERS);
            updateUI(pastPaperList);
        }else {
            if (InternetManager.isConnected(getActivity())) {
                if (isRegSet()) {
                    loadDataFromServer();
                }

            } else {
                loadDataFromDb();
            }
        }
    }

    public void initView(View view) {
        connections_layout = (LinearLayout) view.findViewById(R.id.internet_error);
        connections_layout.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.past_recycler);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                R.color.youtube, R.color.colorPrimaryDark);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        cardView = (CardView) view.findViewById(R.id.no_data_card);

        Button btn_complaint = (Button) view.findViewById(R.id.btn_complaint);
        btn_complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
            }
        });
        connections_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromServer();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (InternetManager.isConnected(getActivity())) {
                    loadDataFromServer();
                } else {
                    Toast.makeText(getActivity(), "Can't load data. Check your connection", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private void loadDataFromDb() {
        progressBar.setVisibility(View.GONE);
        displayNoPastpapersView(false);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        this.pastPaperList.clear();
        pastPaperList = PastPaper.listAll(PastPaper.class);
        if (pastPaperList.size() > 0) {
            updateUI(pastPaperList);
        } else {
            if (!InternetManager.isConnected(getActivity())) {
                displayConnectionError();
            }
        }
    }

    private void displayConnectionError() {
        progressBar.setVisibility(View.INVISIBLE);
        connections_layout.setVisibility(View.VISIBLE);
        displayNoPastpapersView(false);
    }

    private String regInitial(String reg) {
        String[] parts = reg.split("/");
        return parts[0];
    }

    public void loadDataFromServer() {
        this.pastPaperList.clear();
        final ChildEventListener mChildEventListener = new ChildEventListener() {
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
                Toast.makeText(getActivity(), "Failed to load post", Toast.LENGTH_SHORT).show();
            }
        };


        mDatabaseReference.child("past_papers").child(regInitial(degree)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mDatabaseReference.child("past_papers").child(regInitial(degree)).addChildEventListener(mChildEventListener);
                    displayNoPastpapersView(false);
                } else {
                    displayNoPastpapersView(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.w(Config.TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getActivity(), "Failed to load post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayNoPastpapersView(boolean dismiss) {
        if (dismiss) {
            cardView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            cardView.setVisibility(View.GONE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void loadData(DataSnapshot dataSnapshot) {
        PastPaper pastPaper = dataSnapshot.getValue(PastPaper.class);
        pastPaperList.add(pastPaper);
        if (pastPaperList.size() > 0) {
            updateUI(pastPaperList);
            saveToDb(pastPaperList);
        } else {
            displayNoPastpapersView(false);
            progressBar.setVisibility(View.GONE);
            connections_layout.setVisibility(View.GONE);
        }
    }

    private void saveToDb(List<PastPaper> pastPaperList) {
        PastPaper.deleteAll(PastPaper.class);
        for (PastPaper pastPaper : pastPaperList) {
            pastPaper.save();
        }
    }

    private void updateUI(final List<PastPaper> pastPaperList) {
        PastPaperAdapter adapter = new PastPaperAdapter(getActivity(), pastPaperList);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new PastPaperAdapter.ItemClickListener() {
            @Override
            public void itemClick(View v, int position) {
                pastPaper = pastPaperList.get(position);
                allowStoragePermission(pastPaper);
            }
        });
    }

    public void allowStoragePermission(PastPaper pastPaper) {
        //Check if permission is granted or not
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            MarshMallowPermission.requestStoragePermission(activity);
        else
            //Toast.makeText(activity, "Permission is already granted.", Toast.LENGTH_SHORT).show();
        downLoadPastPaper(pastPaper.getLink(), pastPaper.getName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MarshMallowPermission.MarshmallowIntentId.WRITE_EXTERNAL_STORAGE_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // task you need to do.
                    if (pastPaper!=null){
                        downLoadPastPaper(pastPaper.getLink(), pastPaper.getName());
                    }
                    //Toast.makeText(activity, "Storage Permission granted.", Toast.LENGTH_SHORT).show();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(activity, "Storage Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void downLoadPastPaper(String link, String title) {
        try {
            String nameOfFile = URLUtil.guessFileName(link, null, MimeTypeMap.getFileExtensionFromUrl(link));
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));
            request.setTitle(title)
                    .setDescription("File is being downloaded...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nameOfFile)
                    .allowScanningByMediaScanner();
            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } catch (SecurityException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("No permission to write to storage")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            Dialog dialog = builder.create();
            dialog.show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void savePastPaper() {
        PastPaper pastPaper = new PastPaper("BIT 320", "Introduction to web design", "link", "4", "2014/2015", "2");
        HashMap<String, Object> childUpdate = pastPaper.toMap();
        mDatabaseReference.child("past_papers")
                .child(regInitial(degree)).push().updateChildren(childUpdate);
    }
}
