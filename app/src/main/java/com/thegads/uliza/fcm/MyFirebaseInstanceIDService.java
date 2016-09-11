package com.thegads.uliza.fcm;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.model.User;

/**
 * Created by Freddy Genicho on 5/23/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        Log.e(TAG, "Registering token to server");
        if (!refreshedToken.trim().isEmpty() && refreshedToken.trim().length() > 0) {
            User user = MyApplication.getInstance().getPrefManager().getUser();
            if (user != null) {
                String id = user.getId();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(id).child("token").setValue(refreshedToken);
                MyApplication.getInstance().getPrefManager().setRefreshedToken(refreshedToken);
            } else {
                Log.e(TAG, "saving to pref because user is null");
                MyApplication.getInstance().getPrefManager().setRefreshedToken(refreshedToken);
            }
        }
    }
}
