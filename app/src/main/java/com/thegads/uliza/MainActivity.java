package com.thegads.uliza;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.thegads.uliza.activity.AboutActivity;
import com.thegads.uliza.activity.DetailsActivity;
import com.thegads.uliza.activity.EventActivity;
import com.thegads.uliza.activity.LoginActivity;
import com.thegads.uliza.activity.MapsActivity;
import com.thegads.uliza.activity.RoomActivity;
import com.thegads.uliza.activity.SearchActivity;
import com.thegads.uliza.activity.SettingsActivity;
import com.thegads.uliza.activity.SocialActivity;
import com.thegads.uliza.adapter.FragmentViewPagerAdapter;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.model.NewMessage;
import com.thegads.uliza.model.User;
import com.thegads.uliza.util.ActionManager;
import com.thegads.uliza.util.Config;
import com.thegads.uliza.util.InternetManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private User user;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private AHBottomNavigationViewPager viewPager;
    private AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Check for login session. If not logged in launch login activity **/
        if (!MyApplication.getInstance().getPrefManager().isLoggedIn()) {
            launchLoginActivity();
            Log.e(TAG, "Launching login activity");
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert user != null;
        user = MyApplication.getInstance().getPrefManager().getUser();

        setNavigationHeader();
        setUpNavDrawer(toolbar);
        if (user != null) {
            checkInternetConnection();
        }
        initUI();
        updateTimeline();
    }

    private void initUI() {
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_home_white_36dp, R.color.color_tab_1);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_date_range_white_36dp, R.color.color_tab_3);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.tab_4, R.drawable.ic_insert_drive_file_white_36dp, R.color.color_tab_4);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem(R.string.tab_5, R.drawable.ic_notifications_white_36dp, R.color.color_tab_5);

        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item3);
        bottomNavigationItems.add(item4);
        bottomNavigationItems.add(item5);

        // Set background color
        assert bottomNavigation != null;
        bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));

        // Disable the translation inside the CoordinatorLayout
        //bottomNavigation.setBehaviorTranslationEnabled(false);

        bottomNavigation.addItems(bottomNavigationItems);
        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

        // Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(true);

        // Force the titles to be displayed (against Material Design guidelines!)
        bottomNavigation.setForceTitlesDisplay(true);

        // Use colored navigation with circle reveal effect
        bottomNavigation.setColored(true);

        // Set current item programmatically
        bottomNavigation.setCurrentItem(0);

        long unread_count = NewMessage.count(NewMessage.class, "read = ?", new String[]{"1"});
        if (unread_count > 0) {
            bottomNavigation.setNotification(unread_count + "", 3);
        }

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        displayHomeTitle();
                        break;
                    case 1:
                        displayTimetableTitle();
                        break;
                    case 2:
                        displayPastPaperTitle();
                        break;
                    case 3:
                        displayNotification();
                        break;
                    default:
                        //noinspection ConstantConditions
                        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                        getSupportActionBar().setSubtitle("");
                        break;
                }
                viewPager.setCurrentItem(position, false);
            }
        });

        viewPager.setOffscreenPageLimit(4);
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    private void displayHomeTitle() {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setSubtitle("");
    }

    private void displayTimetableTitle() {
        if (user != null) {
            if (user.getDegree() != null) {
                //noinspection ConstantConditions
                getSupportActionBar().setTitle(user.getDegree());
            } else {
                //noinspection ConstantConditions
                getSupportActionBar().setTitle(getResources().getString(R.string.tab_3));
            }

            if (MyApplication.getInstance().getPrefManager().getAcademicYear() != null
                    && MyApplication.getInstance().getPrefManager().getSemester() != null) {
                getSupportActionBar().setSubtitle(String.format("%s  %s Semester",
                        MyApplication.getInstance().getPrefManager().getAcademicYear(),
                        MyApplication.getInstance().getPrefManager().getSemester()));
            }
        }
    }

    private void displayPastPaperTitle() {
        if (user != null) {
            if (user.getDegree() != null) {
                //noinspection ConstantConditions
                getSupportActionBar().setTitle(user.getDegree());
            } else {
                //noinspection ConstantConditions
                getSupportActionBar().setTitle(getResources().getString(R.string.tab_4));
            }

            if (MyApplication.getInstance().getPrefManager().getAcademicYear() != null
                    && MyApplication.getInstance().getPrefManager().getSemester() != null) {
                getSupportActionBar().setSubtitle(getResources().getString(R.string.tab_4));
            }
        }
    }

    private void displayNotification() {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(getResources().getString(R.string.tab_5));
        getSupportActionBar().setSubtitle("");
    }

    private void setUpNavDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void setNavigationHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_help) {
            ActionManager.openWebPage(this, "http://www.gads.com/help");

        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            overridePendingTransition(R.anim.pull_in_left, R.anim.pull_in_right);

        } else if (id == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(this, DetailsActivity.class));
            overridePendingTransition(R.anim.pull_in_left, R.anim.pull_in_right);
        } else if (id == R.id.nav_event) {
            startActivity(new Intent(this, EventActivity.class));

        } else if (id == R.id.nav_hostel) {
            startActivity(new Intent(this, RoomActivity.class));

        } else if (id == R.id.nav_location) {
            startActivity(new Intent(this, MapsActivity.class));

        } else if (id == R.id.nav_social) {
            startActivity(new Intent(this, SocialActivity.class));

        } else if (id == R.id.nav_help) {
            ActionManager.openWebPage(this, "http://www.gads.com/help");

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.pull_in_left, R.anim.pull_in_right);

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
            overridePendingTransition(R.anim.pull_in_left, R.anim.pull_in_right);

        } else if (id == R.id.nav_sign_out) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        if (InternetManager.isConnected(this)) {
            FirebaseAuth.getInstance().signOut();
            MyApplication.getInstance().logout();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet");
            builder.setMessage("Can't complete this action. Please your internet connection and try again.");
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            Dialog dialog = builder.create();
            dialog.show();
        }
    }

    private void updateUserProfile() {
        final DatabaseReference mDatabase = MyApplication.getInstance().getFirebaseDatabase().getReference("users").child(user.getId());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, Object> userUpdate = user.toMap();
                    mDatabase.updateChildren(userUpdate);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void checkInternetConnection() {
        if (InternetManager.isConnected(this)) {
            // add this device to my connections list
            // this value could contain info about the device or a timestamp too
            DatabaseReference con = FirebaseDatabase.getInstance().getReference().child("users");
            con.child(user.getId()).child("connections").setValue(Boolean.TRUE);

            updateUserProfile();

            // when this device disconnects, remove it
            con.child(user.getId()).child("connections").onDisconnect().removeValue();

            // when I disconnect, update the last time I was seen online
            con.child(user.getId()).child("lastOnline").onDisconnect().setValue(ServerValue.TIMESTAMP);

            //save registration token
            con.child(user.getId()).child("token").setValue(MyApplication.getInstance().getPrefManager().getRefreshedToken());
        }
    }


    public void updateTimeline() {
        final String academic_year;
        final String semester;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = (calendar.get(Calendar.MONTH)) + 1;

        if (month <= 4) {
            academic_year = (year - 1) + "/" + year;
            semester = "2";

        } else if (month >= 9) {
            academic_year = year + "/" + (year + 1);
            semester = "1";
        } else {
            academic_year = year - 1 + "/" + year;
            semester = "2";
        }

        MyApplication.getInstance().getPrefManager().setUserSemester(semester);
        MyApplication.getInstance().getPrefManager().setAcademicYear(academic_year);
    }

    public void showKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.e(Config.TAG, e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        long unread_count = NewMessage.count(NewMessage.class, "read = ?", new String[]{"1"});
        if (unread_count > 0) {
            bottomNavigation.setNotification(unread_count + "", 3);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        long unread_count = NewMessage.count(NewMessage.class, "read = ?", new String[]{"1"});
        if (unread_count > 0) {
            bottomNavigation.setNotification(unread_count + "", 3);
        }
    }

}
