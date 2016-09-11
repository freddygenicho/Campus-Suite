package com.thegads.uliza.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.thegads.uliza.R;
import com.thegads.uliza.fragment.TimetableFragment;
import com.thegads.uliza.model.Timetable;

public class TimetableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Timetable timetable = (Timetable) getIntent().getSerializableExtra(TimetableFragment.TIMETABLE);

        TextView lecName = (TextView) findViewById(R.id.lecturer_name);
        TextView lecEmail = (TextView) findViewById(R.id.lecturer_email);
        TextView lecContacts = (TextView) findViewById(R.id.lecturer_contacts);
        TextView day = (TextView) findViewById(R.id.day);
        TextView time = (TextView) findViewById(R.id.time);
        TextView venue = (TextView) findViewById(R.id.venue);
        TextView catOneDay = (TextView) findViewById(R.id.cat_i_day);
        TextView catOneTime = (TextView) findViewById(R.id.cat_i_time);
        TextView catOneVenue = (TextView) findViewById(R.id.cat_i_venue);
        TextView catTwoDay = (TextView) findViewById(R.id.cat_ii_day);
        TextView catTwoTime = (TextView) findViewById(R.id.cat_ii_time);
        TextView catTwoVenue = (TextView) findViewById(R.id.cat_ii_venue);
        TextView examDay = (TextView) findViewById(R.id.exam_day);
        TextView examTime = (TextView) findViewById(R.id.exam_time);
        TextView examVenue = (TextView) findViewById(R.id.exam_venue);
        TextView invigilator = (TextView) findViewById(R.id.invigilators);
        TextView classRepName = (TextView) findViewById(R.id.class_rep_name);
        TextView classRepContact = (TextView) findViewById(R.id.class_rep_contacts);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(timetable.getUnitCode());
        getSupportActionBar().setSubtitle(timetable.getUnitName());

        assert lecName != null;
        lecName.setText(timetable.getLecturerName());

        assert lecEmail != null;
        lecEmail.setText(timetable.getLecturerEmail());

        assert lecContacts != null;
        lecContacts.setText(timetable.getLecturerContacts());

        assert day != null;
        day.setText(timetable.getDay());

        assert time != null;
        time.setText(String.format("%s : %s", timetable.getStartTime(), timetable.getStopTime()));

        assert venue != null;
        venue.setText(timetable.getVenue());

        assert catOneDay != null;
        catOneDay.setText(timetable.getCatOneDay());

        assert catOneTime != null;
        catOneTime.setText(timetable.getCatOneTime());

        assert catOneVenue != null;
        catOneVenue.setText(timetable.getCatOneVenue());

        assert catTwoDay != null;
        catTwoDay.setText(timetable.getCatTwoDay());

        assert catTwoTime != null;
        catTwoTime.setText(timetable.getCatTwoTime());

        assert catTwoVenue != null;
        catTwoVenue.setText(timetable.getCatTwoVenue());

        assert examDay != null;
        examDay.setText(timetable.getExamDay());

        assert examTime != null;
        examTime.setText(timetable.getExamTime());

        assert examVenue != null;
        examVenue.setText(timetable.getExamVenue());

        assert invigilator != null;
        invigilator.setText(timetable.getInvigilators());

        assert classRepName != null;
        classRepName.setText(timetable.getClassRepName());

        assert classRepContact != null;
        classRepContact.setText(timetable.getClassRepContacts());

    }

}
