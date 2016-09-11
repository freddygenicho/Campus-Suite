package com.thegads.uliza.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thegads.uliza.R;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.helper.GlideCircleTransform;
import com.thegads.uliza.model.User;
import com.thegads.uliza.util.ImageUtil;
import com.thegads.uliza.util.InternetManager;

import java.util.Arrays;


/**
 * Created by Freddy Genicho on 5/29/2016.
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private ProgressDialog dialog;
    private EditText firstNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText regNoText;
    private ImageView pic_view;
    private Spinner yearSpinner;
    private Spinner schoolSpinner;
    private Spinner departmentSpinner;
    private Spinner degreeSpinner;

    private String year;
    private String school;
    private String department;
    private String degree;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        this.user = MyApplication.getInstance().getPrefManager().getUser();

        initUI();

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_of_study, android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<CharSequence> schoolAdapter = ArrayAdapter.createFromResource(this, R.array.schools, android.R.layout.simple_spinner_dropdown_item);
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        schoolSpinner.setAdapter(schoolAdapter);

        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(this, R.array.departments, android.R.layout.simple_spinner_dropdown_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        departmentSpinner.setAdapter(departmentAdapter);

        ArrayAdapter<CharSequence> degreeAdapter = ArrayAdapter.createFromResource(this, R.array.degree, android.R.layout.simple_spinner_dropdown_item);
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        degreeSpinner.setAdapter(degreeAdapter);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("Getting you started...");

        if (user != null) {
            assert null != pic_view;
            Glide.with(this).load(user.getImage_url())
                    .listener(ImageUtil.requestListener)
                    .error(R.drawable.ic_account_circle_grey_500_48dp)
                    .transform(new GlideCircleTransform(this))
                    .crossFade()
                    .into(pic_view);

            if (user.getName() != null) {
                String name = user.getName();
                String[] parts = name.split("\\s+");
                String firstName = parts[0];
                String lastName = parts[1];
                firstNameText.setText(firstName);
                lastNameText.setText(lastName);
            }

            if (user.getRegNo() != null) {
                regNoText.setText(user.getRegNo());
            }

            if (user.getEmail() != null) {
                emailText.setText(user.getEmail());
            }

            if (user.getDegree() != null) {
                degree = user.getDegree();
            }

            if (user.getDepartment() != null) {
                department = user.getDepartment();
            }

            if (user.getSchool() != null) {
                school = user.getSchool();
            }

            if (user.getYear() != null) {
                year = user.getYear();
            }

            if (user.getYear() != null) {
                int spinnerPosition;
                spinnerPosition = yearAdapter.getPosition(user.getYear());
                yearSpinner.setSelection(spinnerPosition);
            }

            if (user.getSchool() != null) {
                int spinnerPosition;
                spinnerPosition = schoolAdapter.getPosition(user.getSchool());
                schoolSpinner.setSelection(spinnerPosition);
            }

            if (user.getDepartment() != null) {
                int spinnerPosition;
                spinnerPosition = departmentAdapter.getPosition(user.getDepartment());
                degreeSpinner.setSelection(spinnerPosition);
            }

            if (user.getDegree() != null) {
                int spinnerPosition;
                spinnerPosition = degreeAdapter.getPosition(user.getDegree());
                degreeSpinner.setSelection(spinnerPosition);
            }

        } else {
            Log.e(TAG, "user is null");
        }

    }

    private void validateUserProfile() {

        if (!validateFirstName()) {
            return;
        }

        if (!validateLastName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validateRegNo()) {
            return;
        }

        saveUserProfile();

    }

    private void saveUserProfile() {

        if (InternetManager.isConnected(this)) {
            dialog.show();
            user.setName(firstNameText.getText().toString() + " " + lastNameText.getText().toString());
            user.setEmail(emailText.getText().toString());
            user.setRegNo(regNoText.getText().toString());
            user.setSchool(school);
            user.setDepartment(department);
            user.setDegree(degree);
            user.setYear(year);

            unsubscribeFromTopics();
            subscribeToTopics(user);

            DatabaseReference mDatabase = MyApplication.getInstance().getFirebaseDatabase().getReference();
            mDatabase.child("users").child(user.getId()).setValue(user, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    dialog.dismiss();
                    MyApplication.getInstance().getPrefManager().saveUser(user);
                    MyApplication.getInstance().getPrefManager().setFirstTimeLaunch(true);
                    MyApplication.getInstance().getPrefManager().setIsLoggedIn(true);
                    Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            Log.e(TAG, "saving user");
            Log.e(TAG, user.getName() + " " + user.getEmail() + " " + user.getRegNo() + " " + user.getSchool());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet");
            builder.setMessage("Can't complete this action. Please your internet connection and try again.");
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    validateUserProfile();
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

    private void initUI() {
        pic_view = (ImageView) findViewById(R.id.imageView);
        firstNameText = (EditText) findViewById(R.id.firstNameText);
        lastNameText = (EditText) findViewById(R.id.lastNameText);
        emailText = (EditText) findViewById(R.id.emailText);
        regNoText = (EditText) findViewById(R.id.regNoText);
        yearSpinner = (Spinner) findViewById(R.id.spinnerYear);
        schoolSpinner = (Spinner) findViewById(R.id.spinnerSchool);
        departmentSpinner = (Spinner) findViewById(R.id.spinnerDepartment);
        degreeSpinner = (Spinner) findViewById(R.id.spinnerDegree);

        firstNameText.addTextChangedListener(new MyTextWatcher(firstNameText));
        lastNameText.addTextChangedListener(new MyTextWatcher(lastNameText));
        emailText.addTextChangedListener(new MyTextWatcher(emailText));
        regNoText.addTextChangedListener(new MyTextWatcher(regNoText));

        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] schools = getResources().getStringArray(R.array.schools);
                school = schools[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] departments = getResources().getStringArray(R.array.departments);
                department = departments[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        degreeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] degrees = getResources().getStringArray(R.array.degree);
                degree = degrees[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] yearOfStudy = getResources().getStringArray(R.array.year_of_study);
                year = yearOfStudy[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_next) {
            validateUserProfile();
            return true;
        } else if (id == R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void subscribeToTopics(User user) {
        if (user != null) {
            FirebaseMessaging.getInstance().subscribeToTopic("kabianga");

            if (user.getSchool() != null) {
                FirebaseMessaging.getInstance().subscribeToTopic(user.getSchool().replaceAll("\\s+", "_"));
            }

            if (user.getDepartment() != null) {
                FirebaseMessaging.getInstance().subscribeToTopic(user.getDepartment().replaceAll("\\s+", "_"));
            }

            if (user.getDegree() != null) {
                FirebaseMessaging.getInstance().subscribeToTopic(user.getDegree().replaceAll("\\s+", "_"));
            }

            if (user.getRegNo() != null) {
                FirebaseMessaging.getInstance().subscribeToTopic(user.getRegNo().replaceAll("/", ""));
            }

            Log.e(TAG, " subscribing to topics");
        }
    }

    public void unsubscribeFromTopics() {
        User user = MyApplication.getInstance().getPrefManager().getUser();
        if (user != null) {
            if (user.getSchool() != null) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getSchool().replaceAll("\\s+", "_"));
            }

            if (user.getSchool() != null) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getSchool().replaceAll("\\s+", "_"));
            }

            if (user.getDepartment() != null) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getDepartment().replaceAll("\\s+", "_"));
            }

            if (user.getDegree() != null) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getDegree().replaceAll("\\s+", "_"));
            }

            if (user.getRegNo() != null) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getRegNo().replaceAll("/", "_"));
                Log.e(TAG, user.getRegNo().replaceAll("/", "_"));
            }

            Log.e(TAG, "unsubscribing from topics");
        }
    }

    public void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public boolean validateFirstName() {
        if (firstNameText.getText().toString().trim().isEmpty() || firstNameText.length() < 2) {
            firstNameText.setError("Must be more than 2 characters");
            requestFocus(firstNameText);
            return false;
        } else {
            firstNameText.setError(null);
        }
        return true;
    }

    public boolean validateLastName() {
        if (lastNameText.getText().toString().trim().isEmpty() || lastNameText.length() < 2) {
            lastNameText.setError("Must be more than 2 characters");
            requestFocus(lastNameText);
            return false;
        } else {
            lastNameText.setError(null);
        }
        return true;
    }

    public boolean validateRegNo() {
        if (regNoText.getText().toString().trim().isEmpty() || regNoText.length() > 10 || regNoText.length() < 9) {
            regNoText.setError("Must be less than 10 characters");
            requestFocus(regNoText);
            return false;
        } else {
            regNoText.setError(null);
        }
        return true;
    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public boolean validateEmail() {
        // Check for a valid email address.
        if (TextUtils.isEmpty(emailText.getText().toString())) {
            emailText.setError(getString(R.string.error_field_required));
            requestFocus(emailText);
            return false;
        } else if (!isEmailValid(emailText.getText().toString())) {
            emailText.setError(getString(R.string.error_invalid_email));
            requestFocus(emailText);
            return false;
        }

        return true;
    }


    private class MyTextWatcher implements TextWatcher {
        View view;

        public MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.firstNameText:
                    validateFirstName();
                    break;
                case R.id.lastNameText:
                    validateLastName();
                    break;
                case R.id.email:
                    validateEmail();
                    break;
                case R.id.regNoText:
                    validateRegNo();
                    break;
            }
        }
    }

}
