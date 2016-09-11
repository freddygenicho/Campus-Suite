package com.thegads.uliza.app;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.google.firebase.database.FirebaseDatabase;
import com.orm.SugarApp;
import com.thegads.uliza.activity.LoginActivity;
import com.thegads.uliza.util.PrefManager;

/**
 * Created by Freddy Genicho on 5/26/2016.
 */
public class MyApplication extends SugarApp {
    private static MyApplication sInstance;
    private PrefManager pref;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized MyApplication getInstance(){
        return sInstance;
    }

    public PrefManager getPrefManager() {
        if (pref == null) {
            pref = new PrefManager(this);
        }
        return pref;
    }

    public FirebaseDatabase getFirebaseDatabase(){
        return FirebaseDatabase.getInstance();
    }

    public void logout(){
        pref.clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
