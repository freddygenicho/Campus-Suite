package com.thegads.uliza.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;

/**
 * Created by Freddy Genicho on 7/1/2016.
 */
@IgnoreExtraProperties
public class MyRoom extends SugarRecord {
    private String roomId;
    private String hostelName;
    private long time;
    private String confirmationCode;

    public MyRoom() {

    }

    public String getRoomId() {
        return roomId;
    }

    public String getHostelName() {
        return hostelName;
    }

    public long getTime() {
        return time;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }
}
