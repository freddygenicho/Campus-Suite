package com.thegads.uliza.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Freddy Genicho on 5/29/2016.
 */
public class Hostel extends SugarRecord implements Serializable, Parcelable {
    @Exclude
    private String uid;
    private String name;
    private String location;
    private String rent;
    private String description;
    private String imageUrl;
    private String caretakerName;
    private String caretakerNumber;
    private int available;

    public Hostel() {

    }

    public Hostel(String uid, String name, String location, String rent,
                  String description, String imageUrl, String caretakerName, String caretakerNumber, int available) {
        this.uid = uid;
        this.name = name;
        this.location = location;
        this.rent = rent;
        this.description = description;
        this.imageUrl = imageUrl;
        this.caretakerName = caretakerName;
        this.caretakerNumber = caretakerNumber;
        this.available = available;
    }

    protected Hostel(Parcel in) {
        uid = in.readString();
        name = in.readString();
        location = in.readString();
        rent = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        caretakerName = in.readString();
        caretakerNumber = in.readString();
        available = in.readInt();
    }

    public static final Creator<Hostel> CREATOR = new Creator<Hostel>() {
        @Override
        public Hostel createFromParcel(Parcel in) {
            return new Hostel(in);
        }

        @Override
        public Hostel[] newArray(int size) {
            return new Hostel[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("location", location);
        result.put("rent", rent);
        result.put("description", description);
        result.put("imageUrl", imageUrl);
        result.put("caretakerName", caretakerName);
        result.put("caretakerNumber", caretakerNumber);
        result.put("available",available);
        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaretakerName() {
        return caretakerName;
    }

    public void setCaretakerName(String caretakerName) {
        this.caretakerName = caretakerName;
    }

    public String getCaretakerNumber() {
        return caretakerNumber;
    }

    public void setCaretakerNumber(String caretakerNumber) {
        this.caretakerNumber = caretakerNumber;
    }

    public int getAvailable() {
        return available;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(name);
        parcel.writeString(location);
        parcel.writeString(rent);
        parcel.writeString(description);
        parcel.writeString(imageUrl);
        parcel.writeString(caretakerName);
        parcel.writeString(caretakerNumber);
        parcel.writeInt(available);
    }
}
