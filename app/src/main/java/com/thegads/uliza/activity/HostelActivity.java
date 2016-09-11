package com.thegads.uliza.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thegads.uliza.R;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.fragment.BookFragment;
import com.thegads.uliza.model.Hostel;
import com.thegads.uliza.model.User;
import com.thegads.uliza.util.ActionManager;
import com.thegads.uliza.util.ImageUtil;

public class HostelActivity extends AppCompatActivity implements BookFragment.BookListener {

    private static final int PERMISSION_CALL_PHONE_REQUEST = 100;
    private static final int PERMISSION_SEND_MESSAGE_REQUEST = 200;

    private String userId;
    private String userName;
    private String userReg;
    private String careTakerNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        Hostel hostel = (Hostel) getIntent().getSerializableExtra(RoomActivity.HOSTEL);
        final String id = hostel.getUid();
        final String name = hostel.getName();
        String imageUrl = hostel.getImageUrl();
        String description = hostel.getDescription();
        String location = hostel.getLocation();
        String rent = hostel.getLocation();
        String careTakerName = hostel.getCaretakerName();
        careTakerNumber = hostel.getCaretakerNumber();

        ImageView imageView = (ImageView) findViewById(R.id.hostel_view);
        TextView description_text = (TextView) findViewById(R.id.description);
        TextView rent_text = (TextView) findViewById(R.id.rent);
        TextView location_text = (TextView) findViewById(R.id.location);
        TextView caretaker_name_text = (TextView) findViewById(R.id.caretaker_name);
        TextView caretaker_number_text = (TextView) findViewById(R.id.caretaker_contacts);
        TextView availableRooms = (TextView) findViewById(R.id.available_rooms);
        ImageView btn_call = (ImageView) findViewById(R.id.btn_call);
        ImageView btn_message = (ImageView) findViewById(R.id.btn_message);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = BookFragment.newInstance(id, name, userId, userName, userReg);
                dialogFragment.show(getSupportFragmentManager(), "book_fragment");
            }
        });

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);

        assert imageView != null;
        Glide.with(this).load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(ImageUtil.requestListener)
                .error(R.drawable.no_image)
                .into(imageView);

        assert description_text != null;
        description_text.setText(description);

        assert rent_text != null;
        rent_text.setText(rent);

        assert location_text != null;
        location_text.setText(location);

        assert caretaker_name_text != null;
        caretaker_name_text.setText(careTakerName);

        assert caretaker_number_text != null;
        caretaker_number_text.setText(careTakerNumber);

        availableRooms.setText(hostel.getAvailable());

        assert btn_call != null;
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermissionToCallPhone();
            }
        });

        assert btn_message != null;
        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermissionToSendMessage();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getPermissionToSendMessage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                String message = "To send a message you have to grant the App the permission";
                new PermissionTask().execute(message);
            }

            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_MESSAGE_REQUEST);
        }
    }

    private void getPermissionToCallPhone() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    String message = "To make a call you have to grant the App the permission";
                    new PermissionTask().execute(message);
                }
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL_PHONE_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CALL_PHONE_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ActionManager.initCall(this, careTakerNumber);
            }
        } else if (requestCode == PERMISSION_SEND_MESSAGE_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ActionManager.sendMessage(this, careTakerNumber);
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showReasonDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void success(String success) {
        Toast.makeText(this, success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failed(String failed) {
        Toast.makeText(this, failed, Toast.LENGTH_SHORT).show();
    }

    public class PermissionTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            showReasonDialog(params[0]);
            return null;
        }
    }
}
