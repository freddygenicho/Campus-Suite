package com.thegads.uliza.model;

import java.util.HashMap;

/**
 * Created by Freddy Genicho on 6/4/2016.
 */
public class Comment {
    String id;
    String name;
    String time;
    String message;
    String senderId;

    public Comment() {

    }

    public Comment(String id, String name, String time, String message, String senderId) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.time = time;
        this.senderId = senderId;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("time", time);
        result.put("message", message);
        result.put("userId", senderId);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
