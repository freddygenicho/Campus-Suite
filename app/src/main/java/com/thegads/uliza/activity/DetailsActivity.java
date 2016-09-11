package com.thegads.uliza.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thegads.uliza.R;
import com.thegads.uliza.adapter.MyRoomAdapter;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.helper.GlideCircleTransform;
import com.thegads.uliza.helper.SimpleDividerItemDecoration;
import com.thegads.uliza.model.MyRoom;
import com.thegads.uliza.model.User;
import com.thegads.uliza.util.ImageUtil;
import com.thegads.uliza.util.InternetManager;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private User user;
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private ProgressDialog dialog;
    private EditText firstNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText regNoText;
    private String school;
    private String department;
    private String degree;
    private String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView image = (ImageView) findViewById(R.id.profile_image);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("Saving changes...");

        Button button = (Button) findViewById(R.id.btn_save_changes);
        button.setOnClickListener(this);

        firstNameText = (EditText) findViewById(R.id.firstNameText);
        lastNameText = (EditText) findViewById(R.id.lastNameText);
        emailText = (EditText) findViewById(R.id.emailText);
        regNoText = (EditText) findViewById(R.id.regNoText);

        firstNameText.addTextChangedListener(new MyTextWatcher(firstNameText));
        lastNameText.addTextChangedListener(new MyTextWatcher(lastNameText));
        emailText.addTextChangedListener(new MyTextWatcher(emailText));
        regNoText.addTextChangedListener(new MyTextWatcher(regNoText));

        Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        Spinner schoolSpinner = (Spinner) findViewById(R.id.schoolSpinner);
        Spinner deptSpinner = (Spinner) findViewById(R.id.deptSpinner);
        Spinner degreeSpinner = (Spinner) findViewById(R.id.degreeSpinner);


        user = MyApplication.getInstance().getPrefManager().getUser();
        if (user != null) {
            assert image != null;
            Glide.with(this).load(user.getImage_url()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(ImageUtil.requestListener)
                    .error(R.drawable.ic_account_circle_grey_500_48dp)
                    .transform(new GlideCircleTransform(this)).into(image);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(user.getName());

            if (user.getName() != null) {
                String name = user.getName();
                String[] parts = name.split("\\s+");
                String firstName = parts[0];
                String lastName = parts[1];
                firstNameText.setText(firstName);
                lastNameText.setText(lastName);
            }

            if (user.getEmail() != null) {
                emailText.setText(user.getEmail());
            } else {
                emailText.setText("*Missing");
            }

            if (user.getRegNo() != null) {
                regNoText.setText(user.getRegNo());
            } else {
                regNoText.setText("*Missing");
            }

            if (user.getYear() != null) {
                int spinnerPosition;
                ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_of_study, android.R.layout.simple_spinner_dropdown_item);
                yearAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
                yearSpinner.setAdapter(yearAdapter);
                spinnerPosition = yearAdapter.getPosition(user.getYear());
                yearSpinner.setSelection(spinnerPosition);
            }

            if (user.getSchool() != null) {
                int spinnerPosition;
                ArrayAdapter<CharSequence> schoolAdapter = ArrayAdapter.createFromResource(this, R.array.schools, android.R.layout.simple_spinner_dropdown_item);
                schoolAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
                schoolSpinner.setAdapter(schoolAdapter);
                spinnerPosition = schoolAdapter.getPosition(user.getSchool());
                schoolSpinner.setSelection(spinnerPosition);
            }

            if (user.getDepartment() != null) {
                int spinnerPosition;
                ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(this, R.array.departments, android.R.layout.simple_spinner_dropdown_item);
                departmentAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
                deptSpinner.setAdapter(departmentAdapter);
                spinnerPosition = departmentAdapter.getPosition(user.getDepartment());
                degreeSpinner.setSelection(spinnerPosition);
            }

            if (user.getDegree() != null) {
                int spinnerPosition;
                ArrayAdapter<CharSequence> degreeAdapter = ArrayAdapter.createFromResource(this, R.array.degree, android.R.layout.simple_spinner_dropdown_item);
                degreeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
                degreeSpinner.setAdapter(degreeAdapter);
                spinnerPosition = degreeAdapter.getPosition(user.getDegree());
                degreeSpinner.setSelection(spinnerPosition);
            }


        }

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

        deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        if (!InternetManager.isConnected(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Network Error")
                    .setMessage("Couldn't complete the request. Please check your internet and try again")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            validateUserProfile();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            Dialog dialog = builder.create();
            dialog.show();
        } else {

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
                    if (databaseError == null) {
                        dialog.dismiss();
                        MyApplication.getInstance().getPrefManager().saveUser(user);
                        Toast.makeText(DetailsActivity.this, "Changes were saved successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Log.e(TAG, "saving user");
            Log.e(TAG, user.getName() + " " + user.getEmail() + " " + user.getRegNo() + " " + user.getSchool());
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_save_changes) {
            validateUserProfile();
        }
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
                case R.id.emailText:
                    validateEmail();
                    break;
                case R.id.regNoText:
                    validateRegNo();
                    break;
            }
        }
    }
}
