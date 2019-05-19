package com.techrums.whatstheprice.models;

import java.io.Serializable;

public class User implements Serializable {
    private String user;
    private String email;
    private String photoUrl;
    private String uId;
    private String bio;
    private String sex;






    public User(String user, String email, String photoUrl, String uId, String bio, String sex) {
        this.user = user;
        this.email = email;
        this.photoUrl = photoUrl;
        this.uId = uId;
        this.bio=bio;
        this.sex=sex;

    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}