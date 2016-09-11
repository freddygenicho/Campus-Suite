package com.thegads.uliza.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;

import java.util.HashMap;

/**
 * Created by Freddy Genicho on 6/12/2016.
 */
@IgnoreExtraProperties
public class PastPaper extends SugarRecord implements Parcelable {
    private String code;
    private String name;
    private String link;
    private String year;
    private String academic_year;
    private String semester;

    public PastPaper(){

    }

    public PastPaper(String code, String name, String link, String year, String academic_year, String semester) {
        this.code = code;
        this.name = name;
        this.link = link;
        this.year = year;
        this.academic_year = academic_year;
        this.semester = semester;
    }

    protected PastPaper(Parcel in) {
        code = in.readString();
        name = in.readString();
        link = in.readString();
        year = in.readString();
        academic_year = in.readString();
        semester = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(link);
        dest.writeString(year);
        dest.writeString(academic_year);
        dest.writeString(semester);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PastPaper> CREATOR = new Creator<PastPaper>() {
        @Override
        public PastPaper createFromParcel(Parcel in) {
            return new PastPaper(in);
        }

        @Override
        public PastPaper[] newArray(int size) {
            return new PastPaper[size];
        }
    };

    public HashMap<String, Object> toMap(){
        HashMap<String ,Object> map = new HashMap<>();
        map.put("code",code);
        map.put("name",name);
        map.put("link",link);
        map.put("year",year);
        map.put("academic_year",academic_year);
        map.put("semester",semester);
        return map;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getYear() {
        return year;
    }

    public String getAcademic_year() {
        return academic_year;
    }

    public String getSemester() {
        return semester;
    }
}
