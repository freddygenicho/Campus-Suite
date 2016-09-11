package com.thegads.uliza.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thegads.uliza.model.User;

import java.util.Calendar;

/**
 * Created by Freddy Genicho on 5/24/2016.
 */
public class PrefManager {

    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_PHOTO_URL = "photo_url";
    private static final String KEY_USER_REG_NO = "registration";
    private static final String KEY_USER_SCHOOL = "school";
    private static final String KEY_USER_DEPT = "department";
    private static final String KEY_USER_DEGREE = "degree";
    private static final String KEY_USER_YEAR = "year";
    private static final String KEY_USER_SEMESTER = "semester";
    private static final String KEY_USER_ACADEMIC_YEAR = "academic_year";
    private static final String KEY_REFRESHED_TOKEN = "token";


    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_PROFILE_SET = "isProfileSet";
    private static String IS_LOGGED_IN = "isLoggedIn";

    public PrefManager(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public void saveUser(User user){
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHOTO_URL, user.getImage_url());
        editor.putString(KEY_USER_REG_NO, user.getRegNo());
        editor.putString(KEY_USER_SCHOOL, user.getSchool());
        editor.putString(KEY_USER_DEPT, user.getDepartment());
        editor.putString(KEY_USER_DEGREE, user.getDegree());
        editor.putString(KEY_USER_YEAR, user.getYear());
        editor.commit();
        Log.e(Config.TAG, "User from provider is stored in shared preferences. " + user.getName()
                + ", " + user.getEmail() + " , " + user.getId() + " " + user.getImage_url());
    }

    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, email, regNo, image_url, school, dept, degree, year;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_USER_NAME, null);
            email = pref.getString(KEY_USER_EMAIL, null);
            regNo = pref.getString(KEY_USER_REG_NO, null);
            image_url = pref.getString(KEY_USER_PHOTO_URL, null);
            school = pref.getString(KEY_USER_SCHOOL, null);
            dept = pref.getString(KEY_USER_DEPT, null);
            degree = pref.getString(KEY_USER_DEGREE, null);
            year = pref.getString(KEY_USER_YEAR, "1");
            return new User(id, name, regNo, year, email, image_url, school, dept, degree);
        }
        return null;
    }


    public void clear() {
        editor.clear();
        editor.commit();
    }

    public String getAcademicYear() {
        return pref.getString(KEY_USER_ACADEMIC_YEAR, default_academic_year());
    }

    public String getSemester() {
        return pref.getString(KEY_USER_SEMESTER, defaultSemester());
    }

    private String default_academic_year() {
        String academic_year;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = (calendar.get(Calendar.MONTH)) + 1;

        if (month <= 4) {
            academic_year = (year - 1) + "/" + year;

        } else if (month >= 9) {
            academic_year = year + "/" + (year + 1);
        } else {
            academic_year = year - 1 + "/" + year;
        }
        return academic_year;
    }

    private String defaultSemester() {
        String semester;
        Calendar calendar = Calendar.getInstance();
        int month = (calendar.get(Calendar.MONTH)) + 1;

        if (month <= 4) {
            semester = "2";

        } else if (month >= 9) {
            semester = "1";
        } else {
            semester = "2";
        }
        return semester;
    }

    public String getRefreshedToken() {
        return pref.getString(KEY_REFRESHED_TOKEN, null);
    }

    public void setRefreshedToken(String refreshedToken) {
        editor.putString(KEY_REFRESHED_TOKEN, refreshedToken);
    }

    public void setAcademicYear(String academicYear) {
        editor.putString(KEY_USER_ACADEMIC_YEAR, academicYear);
    }

    public void setUserSemester(String semester) {
        editor.putString(KEY_USER_SEMESTER, semester);
    }

    public boolean isProfileSet() {
        return pref.getBoolean(IS_PROFILE_SET, false);
    }

    public void setIsProfileSet(boolean isProfileSet) {
        editor.putBoolean(IS_PROFILE_SET,isProfileSet);
    }
}
