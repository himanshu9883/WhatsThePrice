package com.techrums.whatstheprice.models;

import java.io.Serializable;

public class Notifications implements Serializable {

   // private String user_id;
    private String text;
   // private String post_id;
    //private boolean isPost;
    private User user;
    private Post mpost;
    private String notificationId;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Post getPost() {
        return mpost;
    }

    public void setPost(Post mpost) {
        this.mpost = mpost;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Notifications( String text,User user,Post mpost,String notificationId) {
       // this.user_id = user_id;
        this.text = text;
      //  this.post_id = post_id;
       // this.isPost = isPost;
        this.user=user;
        this.mpost=mpost;
        this.notificationId=notificationId;
    }

    public Notifications() {
    }

  /*  public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }*/

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

  /*  public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }*/

 /*   public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }*/



}
