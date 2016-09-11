package com.thegads.uliza.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Freddy Genicho on 6/3/2016.
 */
@IgnoreExtraProperties
public class Timetable extends SugarRecord implements Serializable, Parcelable {
    private String unitId;
    private String unitName;
    private String unitCode;
    private String day;
    private String startTime;
    private String stopTime;
    private String venue;
    private String lecturerName;
    private String lecturerContacts;
    private String lecturerEmail;
    private String classRepName;
    private String classRepContacts;
    private String catOneDay;
    private String catOneTime;
    private String catOneVenue;
    private String catTwoDay;
    private String catTwoTime;
    private String catTwoVenue;
    private String examDay;
    private String examTime;
    private String examVenue;
    private String invigilators;

    public Timetable() {

    }

    public Timetable(String unitName, String unitCode, String day,
                     String startTime, String stopTime, String venue, String lecturerName,
                     String lecturerContacts, String lecturerEmail, String classRepName, String classRepContacts) {
        this.unitName = unitName;
        this.unitCode = unitCode;
        this.day = day;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.venue = venue;
        this.lecturerName = lecturerName;
        this.lecturerContacts = lecturerContacts;
        this.lecturerEmail = lecturerEmail;
        this.classRepName = classRepName;
        this.classRepContacts = classRepContacts;
    }

    protected Timetable(Parcel in) {
        unitId = in.readString();
        unitName = in.readString();
        unitCode = in.readString();
        day = in.readString();
        startTime = in.readString();
        stopTime = in.readString();
        venue = in.readString();
        lecturerName = in.readString();
        lecturerContacts = in.readString();
        lecturerEmail = in.readString();
        classRepName = in.readString();
        classRepContacts = in.readString();
        catOneDay = in.readString();
        catOneTime = in.readString();
        catOneVenue = in.readString();
        catTwoDay = in.readString();
        catTwoTime = in.readString();
        catTwoVenue = in.readString();
        examDay = in.readString();
        examTime = in.readString();
        examVenue = in.readString();
        invigilators = in.readString();
    }

    public static final Creator<Timetable> CREATOR = new Creator<Timetable>() {
        @Override
        public Timetable createFromParcel(Parcel in) {
            return new Timetable(in);
        }

        @Override
        public Timetable[] newArray(int size) {
            return new Timetable[size];
        }
    };

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("unitCode", unitCode);
        result.put("unitName", unitName);
        result.put("day", day);
        result.put("startTime", startTime);
        result.put("stopTime", stopTime);
        result.put("venue", venue);
        result.put("lecturerName", lecturerName);
        result.put("lecturerContacts", lecturerContacts);
        result.put("lecturerEmail", lecturerEmail);
        result.put("classRepName", classRepName);
        result.put("classRepContacts", classRepContacts);
        return result;
    }

    public void setId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public String getVenue() {
        return venue;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public String getLecturerContacts() {
        return lecturerContacts;
    }

    public String getLecturerEmail() {
        return lecturerEmail;
    }

    public String getClassRepName() {
        return classRepName;
    }

    public String getClassRepContacts() {
        return classRepContacts;
    }

    public String getCatOneDay() {
        return catOneDay;
    }

    public String getCatOneTime() {
        return catOneTime;
    }

    public String getCatOneVenue() {
        return catOneVenue;
    }

    public String getCatTwoDay() {
        return catTwoDay;
    }

    public String getCatTwoTime() {
        return catTwoTime;
    }

    public String getCatTwoVenue() {
        return catTwoVenue;
    }

    public String getExamDay() {
        return examDay;
    }

    public String getExamTime() {
        return examTime;
    }

    public String getExamVenue() {
        return examVenue;
    }

    public String getInvigilators() {
        return invigilators;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(unitId);
        parcel.writeString(unitName);
        parcel.writeString(unitCode);
        parcel.writeString(day);
        parcel.writeString(startTime);
        parcel.writeString(stopTime);
        parcel.writeString(venue);
        parcel.writeString(lecturerName);
        parcel.writeString(lecturerContacts);
        parcel.writeString(lecturerEmail);
        parcel.writeString(classRepName);
        parcel.writeString(classRepContacts);
        parcel.writeString(catOneDay);
        parcel.writeString(catOneTime);
        parcel.writeString(catOneVenue);
        parcel.writeString(catTwoDay);
        parcel.writeString(catTwoTime);
        parcel.writeString(catTwoVenue);
        parcel.writeString(examDay);
        parcel.writeString(examTime);
        parcel.writeString(examVenue);
        parcel.writeString(invigilators);
    }
}
