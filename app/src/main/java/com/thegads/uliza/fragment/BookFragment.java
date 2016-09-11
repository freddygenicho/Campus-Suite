package com.thegads.uliza.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.thegads.uliza.R;
import com.thegads.uliza.util.ImageUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BookFragment extends DialogFragment implements View.OnClickListener {

    private static final String HOSTEL_ID = "hostelId";
    private static final String HOSTEL_NAME = "hostelName";
    private static final String USER_ID = "userId";
    private static final String USER_NAME = "userName";
    private static final String USER_REG = "userReg";

    private String hostelId;
    private String hostelName;
    private String userId;
    private String userName;
    private String userReg;
    private EditText name_text;
    private EditText regNo_text;
    private EditText phone_text;
    private EditText national_text;
    private ProgressDialog progressDialog;

    public interface BookListener {
        void success(String success);

        void failed(String failed);
    }

    BookListener mBookListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mBookListener = (BookListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    public BookFragment() {
        // Required empty public constructor
    }

    public static BookFragment newInstance(String hostelId, String hostelName, String userId, String userName, String userReg) {
        BookFragment fragment = new BookFragment();
        Bundle args = new Bundle();
        args.putString(HOSTEL_ID, hostelId);
        args.putString(HOSTEL_NAME, hostelName);
        args.putString(USER_ID, userId);
        args.putString(USER_NAME, userName);
        args.putString(USER_REG, userReg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Room Booking");
        progressDialog.setMessage("connecting...");
        progressDialog.setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hostelId = getArguments().getString(HOSTEL_ID);
            hostelName = getArguments().getString(HOSTEL_NAME);
            userId = getArguments().getString(USER_ID);
            userName = getArguments().getString(USER_NAME);
            userReg = getArguments().getString(USER_REG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //noinspection UnnecessaryLocalVariable
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.book_room, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button continue_booking = (Button) view.findViewById(R.id.book);
        Button cancel = (Button) view.findViewById(R.id.cancel);

        this.national_text = (EditText) view.findViewById(R.id.national_text);
        national_text.addTextChangedListener(new MyTextWatcher(national_text));

        this.phone_text = (EditText) view.findViewById(R.id.phone_text);
        phone_text.addTextChangedListener(new MyTextWatcher(phone_text));

        this.regNo_text = (EditText) view.findViewById(R.id.regNo_text);
        regNo_text.addTextChangedListener(new MyTextWatcher(regNo_text));

        this.name_text = (EditText) view.findViewById(R.id.name_text);
        name_text.addTextChangedListener(new MyTextWatcher(name_text));

        getDialog().setTitle(hostelName);

        name_text.setText(userName);
        regNo_text.setText(userReg);

        continue_booking.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.book) {
            progressDialog.show();
            progressDialog.setCancelable(false);
            submitData();
        } else if (id == R.id.cancel) {
            dismiss();
        }
    }


    public class MyTextWatcher implements TextWatcher {
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
                case R.id.name_text:
                    validateName();
                    break;
                case R.id.phone_text:
                    validatePhone();
                    break;
                case R.id.regNo_text:
                    validateRegNo();
                    break;
                case R.id.national_text:
                    validateNationalId();
                    break;
            }
        }
    }

    private boolean validateNationalId() {
        if (national_text.getText().toString().isEmpty()) {
            national_text.setError("National ID number is required");
            return false;
        } else if (national_text.getText().length() > 10) {
            national_text.setError("National ID number can't be greater than 10 characters");
            return false;
        } else if (national_text.getText().length() < 10) {
            national_text.setError("National ID number can't be less than 10 characters");
            return false;
        } else {
            national_text.setError(null);
        }
        return true;
    }

    private boolean validatePhone() {
        if (phone_text.getText().toString().isEmpty() && phone_text.getText().length() < 10) {
            phone_text.setError("Phone number is required");
            return false;
        } else {
            phone_text.setError(null);
        }
        return true;
    }

    public boolean validateName() {
        if (name_text.getText().toString().trim().isEmpty()) {
            name_text.setError("Name is required");
            return false;
        } else {
            name_text.setError(null);
        }
        return true;
    }

    public boolean validateRegNo() {
        if (regNo_text.getText().toString().trim().isEmpty()) {
            regNo_text.setError("Reg No is required");
            return false;
        } else {
            regNo_text.setError(null);
        }
        return true;
    }


    private void submitData() {
        if (!validateName()) {
            return;
        }

        if (!validateRegNo()) {
            return;
        }

        final StringBuilder confirmationCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            confirmationCode.append(random.nextInt(9));
        }

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        String key = mDatabaseReference.child("bookings").push().getKey();
        HashMap<String, Object> hostelValues = new HashMap<>();
        hostelValues.put("id", key);
        hostelValues.put("hostelId", hostelId);
        hostelValues.put("hostelName", hostelName);
        hostelValues.put("userId", userId);
        hostelValues.put("userName", name_text.getText().toString());
        hostelValues.put("regNo", regNo_text.getText().toString());
        hostelValues.put("phoneNumber", phone_text.getText().toString());
        hostelValues.put("nationalId", national_text.getText().toString());
        hostelValues.put("time", ServerValue.TIMESTAMP);
        hostelValues.put("confirmationCode", confirmationCode.toString());

        Map<String, Object> userValues = new HashMap<>();
        userValues.put("hostelId", hostelId);
        userValues.put("hostelName", hostelName);
        userValues.put("phoneNumber", phone_text.getText().toString());
        userValues.put("nationalId", national_text.getText().toString());
        userValues.put("confirmationCode", confirmationCode.toString());
        userValues.put("time", ServerValue.TIMESTAMP);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/bookings/" + key, hostelValues);
        childUpdates.put("/users/" + userId + "/hostel/" + key, userValues);

        mDatabaseReference.push().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                progressDialog.dismiss();
                if (databaseError != null) {
                    mBookListener.failed("Sorry! An error occurred while saving room. Pleas try again" + databaseError.getMessage());
                    getDialog().dismiss();
                } else {
                    mBookListener.success("Data was successfully saved");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Success")
                            .setMessage("Confirmation Code +"+confirmationCode)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getDialog().dismiss();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }

    public void onResume() {
        // Store access variables for window and blank point

        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width

        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing

        super.onResume();
    }
}
