package com.techrums.whatstheprice.models;

import java.io.Serializable;

public class Post implements Serializable {

    private User user;
    private String postText;
    private String photoImageUri;
    private String postId;
    private long numLikes;


    private long numDislikes;
    private long numComments;
    private long timeCreated;
    private long price;
    private String shoppingPlace;
    private String product;


    public Post(User user, String postText, String photoImageUri, String postId, long numLikes, long numComments,long numDislikes, long timeCreated, long price, String shoppingPlace, String product) {
        this.user = user;
        this.postText = postText;
        this.photoImageUri = photoImageUri;
        this.postId = postId;
        this.numLikes = numLikes;
        this.numDislikes=numDislikes;
        this.numComments = numComments;
        this.timeCreated = timeCreated;
        this.price=price;
        this.shoppingPlace=shoppingPlace;
        this.product=product;
    }

    public Post() {
    }
    public long getNumDislikes() {
        return numDislikes;
    }

    public void setNumDislikes(long numDislikes) {
        this.numDislikes = numDislikes;
    }
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getShoppingPlace() {
        return shoppingPlace;
    }

    public void setShoppingPlace(String shoppingPlace) {
        this.shoppingPlace = shoppingPlace;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPhotoImageUri() {
        return photoImageUri;
    }

    public void setPhotoImageUri(String photoImageUri) {
        this.photoImageUri = photoImageUri;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }

    public long getNumComments() {
        return numComments;
    }

    public void setNumComments(long numComments) {
        this.numComments = numComments;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

}