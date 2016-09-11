package com.thegads.uliza.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Freddy Genicho on 6/2/2016.
 */
@IgnoreExtraProperties
public class Event extends SugarRecord implements Serializable, Parcelable {
    private String eventId;
    private String date;
    private String time;
    private String details;
    private String eventImage;
    private String senderImage;
    private String senderName;
    private String title;

    public Event() {

    }

    public Event(String eventId, String title, String date, String time, String details, String eventImage, String senderImage, String senderName) {
        this.eventId = eventId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.details = details;
        this.eventImage = eventImage;
        this.senderImage = senderImage;
        this.senderName = senderName;
    }

    protected Event(Parcel in) {
        eventId = in.readString();
        date = in.readString();
        time = in.readString();
        details = in.readString();
        eventImage = in.readString();
        senderImage = in.readString();
        senderName = in.readString();
        title = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", eventId);
        result.put("title", title);
        result.put("details", details);
        result.put("date", date);
        result.put("time", time);
        result.put("senderName", senderName);
        result.put("eventImage", eventImage);
        result.put("senderImage", senderImage);
        return result;
    }

    public String getEventId() {
        return eventId;
    }

    public void setId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eventId);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(details);
        parcel.writeString(eventImage);
        parcel.writeString(senderImage);
        parcel.writeString(senderName);
        parcel.writeString(title);
    }
}
