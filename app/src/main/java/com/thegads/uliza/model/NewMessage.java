package com.thegads.uliza.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Freddy Genicho on 7/27/2016.
 */
public class NewMessage extends SugarRecord implements Serializable, Parcelable {

    private String chatId;
    private String body;
    private int read;
    private String time;

    public NewMessage() {

    }

    public NewMessage(String chatId, String body, int read, String time) {
        this.chatId = chatId;
        this.body = body;
        this.read = read;
        this.time = time;
    }

    protected NewMessage(Parcel in) {
        chatId = in.readString();
        body = in.readString();
        read = in.readInt();
        time = in.readString();
    }

    public static final Creator<NewMessage> CREATOR = new Creator<NewMessage>() {
        @Override
        public NewMessage createFromParcel(Parcel in) {
            return new NewMessage(in);
        }

        @Override
        public NewMessage[] newArray(int size) {
            return new NewMessage[size];
        }
    };

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(chatId);
        parcel.writeString(body);
        parcel.writeInt(read);
        parcel.writeString(time);
    }
}
