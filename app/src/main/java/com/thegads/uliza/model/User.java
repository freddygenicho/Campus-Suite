package com.thegads.uliza.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

/**
 * Created by Freddy Genicho on 5/25/2016.
 */
@IgnoreExtraProperties
public class User {

    @Exclude
    private String id;
    private String name;
    private String regNo;
    private String year;
    private String email;
    private String image_url;
    private String school;
    private String department;
    private String degree;

    public User() {

    }

    public User(String id, String name, String regNo, String year, String email, String image_url,
                String school, String department, String degree) {
        this.id = id;
        this.name = name;
        this.regNo = regNo;
        this.year = year;
        this.email = email;
        this.image_url = image_url;
        this.school = school;
        this.department = department;
        this.degree = degree;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("regNo", regNo);
        result.put("year", year);
        result.put("email", email);
        result.put("image_url", image_url);
        result.put("school", school);
        result.put("department", department);
        result.put("degree", degree);
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

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSchool() {
        return school;
    }

    public String getDepartment() {
        return department;
    }

    public String getDegree() {
        return degree;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
