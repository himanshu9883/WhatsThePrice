package com.techrums.whatstheprice.utils;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseUtils {

    public static DatabaseReference getUserRef(String email){
        return FirebaseDatabase.getInstance().getReference(Constants.USERS_KEY).child(email);
    }

    public static DatabaseReference getPostRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.POST_KEY);
    }

    public static DatabaseReference getPostLikedRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.POST_LIKED_KEY);
    }

    public static DatabaseReference getPostLikedRef(String postId){
        return getPostLikedRef().child(getCurrentUser().getEmail().replace(".",",")).child(postId);
    }

   /* public static DatabaseReference getPostLikedRef(String postUser,String postId){
        return getPostLikedRef(postId).child(postUser);
    }*/

    public static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

   public static String getUid(){
        String path=FirebaseDatabase.getInstance().getReference().push().toString();
        return path.substring(path.lastIndexOf("/")+1);
    }

 /*   public static String getUid(){
        String path=(FirebaseAuth.getInstance().getCurrentUser().getUid()+System.currentTimeMillis());
        return path;
    }*/

    public static StorageReference getImageRef(){
        return FirebaseStorage.getInstance().getReference(Constants.POST_IMAGES);
    }

    public static DatabaseReference getMyPostRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.MY_POSTS).child(getCurrentUser().getEmail().replace(".",","));
    }

    public static DatabaseReference getCommentsRef(String postId){
        return FirebaseDatabase.getInstance().getReference(Constants.COMMENTS_KEY).child(postId);

    }
    public static DatabaseReference getMyCommentsRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.MY_COMMENTS).child(getCurrentUser().getEmail().replace(".",","));
    }

    public static DatabaseReference getMyRecordRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.USER_RECORD).child(getCurrentUser().getEmail().replace(".",","));

    }

    public static DatabaseReference getNotificationsRef(String notificationId){
        return FirebaseDatabase.getInstance().getReference("Notifications").child(notificationId);
    }
   /* public static DatabaseReference getNotificationsRef(){
        return FirebaseDatabase.getInstance().getReference().child(getPostRef().toString());
    }*/
    public static void addToMyRecord(String node,final String id){
        getMyRecordRef().child(node).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ArrayList<String> myRecordCollection;
                if(mutableData.getValue()==null){
                    myRecordCollection=new ArrayList<String>(1);
                    myRecordCollection.add(id);

                }
                else{
                    myRecordCollection=(ArrayList<String>)mutableData.getValue();
                    myRecordCollection.add(id);
                }
                mutableData.setValue(myRecordCollection);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }


}