package com.thegads.uliza.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Freddy Genicho on 5/25/2016.
 */
@IgnoreExtraProperties
public class Post extends SugarRecord implements Serializable, Parcelable {

    private String postId;
    private String title;
    private String body;
    private String date;
    private String senderId;
    private String name;
    private String senderTitle;
    private String postImage;
    private String imageUrl;

    @SuppressWarnings("unused")
    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String postId, String title, String body, String date, String senderId, String name, String senderTitle,
                String imageUrl, String postImage) {
        this.postId = postId;
        this.title = title;
        this.body = body;
        this.date = date;
        this.senderId = senderId;
        this.name = name;
        this.senderTitle = senderTitle;
        this.imageUrl = imageUrl;
        this.postImage = postImage;
    }

    protected Post(Parcel in) {
        postId = in.readString();
        title = in.readString();
        body = in.readString();
        date = in.readString();
        senderId = in.readString();
        name = in.readString();
        senderTitle = in.readString();
        postImage = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", postId);
        result.put("name", name);
        result.put("title", title);
        result.put("body", body);
        result.put("date", date);
        result.put("senderId", senderId);
        result.put("senderTitle", senderTitle);
        result.put("imageUrl", imageUrl);
        result.put("postImage", postImage);
        return result;
    }


    public String getPostId() {
        return postId;
    }

    public void setId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSenderTitle() {
        return senderTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPostImage() {
        return postImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(postId);
        parcel.writeString(title);
        parcel.writeString(body);
        parcel.writeString(date);
        parcel.writeString(senderId);
        parcel.writeString(name);
        parcel.writeString(senderTitle);
        parcel.writeString(postImage);
        parcel.writeString(imageUrl);
    }
}
