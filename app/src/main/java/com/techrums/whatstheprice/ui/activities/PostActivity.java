package com.techrums.whatstheprice.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rey.material.widget.LinearLayout;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.models.Comment;
import com.techrums.whatstheprice.models.Notifications;
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.utils.Constants;
import com.techrums.whatstheprice.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PostActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String BUNDLE_COMMENT="comments";
    private Post mPost;
    private EditText mCommentEditTextView;
    private Comment mcomment;

    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if(savedInstanceState!=null){
            mcomment=(Comment)savedInstanceState.getSerializable(BUNDLE_COMMENT);

        }

        Intent intent=getIntent();
        mPost=(Post)intent.getSerializableExtra(Constants.EXTRA_POST);

        init();
        initPost();
        initCommentSection();

    }


    private void initCommentSection() {
        RecyclerView commentRecyclerView;
        commentRecyclerView = (RecyclerView)findViewById(R.id.comment_recyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this));

        FirebaseRecyclerAdapter<Comment, CommentHolder> commentAdapter;
        commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(
                Comment.class,
                R.layout.row_comment,
                CommentHolder.class,
                FirebaseUtils.getCommentsRef(mPost.getPostId())) {



            @Override
            protected void populateViewHolder(CommentHolder viewHolder, final Comment model, int position) {
                //List<Comment> mComments=new ArrayList<>();
               // model=mComments.get(position);

                viewHolder.setUsername(String.valueOf(model.getUser().getUser()));

                try{
                    viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                }
                catch (Exception e){

                }

                Glide.with(PostActivity.this)
                        .load(model.getUser().getPhotoUrl())
                        .into(viewHolder.commentOwnerDisplay);
                viewHolder.commentTextView.setText(model.getComment());
                viewHolder.timeTextview.setText(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));


            }

        };


        commentRecyclerView.setAdapter(commentAdapter);
    }


    private void initPost() {

        ImageView postOwnerDisplayImageView=(ImageView)findViewById(R.id.tv_post_owner_display);
        TextView postOwnerUsernameTextView=(TextView)findViewById(R.id.tv_post_username);
        ImageView postDisplayImageView=(ImageView)findViewById(R.id.tv_post_display);
        TextView postTimeCreatedTextView=(TextView)findViewById(R.id.post_time);
        final ImageView like=(ImageView)findViewById(R.id.tv_like);
        TextView postTextTextView=(TextView)findViewById(R.id.tv_post_text);
        LinearLayout postLikeLayout=(LinearLayout)findViewById(R.id.like_layout);
        TextView postNumLikesTextView=(TextView)findViewById(R.id.tv_likes);
        LinearLayout postCommentLayout=(LinearLayout)findViewById(R.id.comment_layout);
        TextView postNumCommentTextView=(TextView)findViewById(R.id.tv_comments);
        TextView postPrice=(TextView)findViewById(R.id.post_display_price);
        TextView postPlace=(TextView)findViewById(R.id.post_display_place);
        TextView postProduct=(TextView)findViewById(R.id.post_display_product);


        postOwnerUsernameTextView.setText(mPost.getUser().getUser());
        postTextTextView.setText(mPost.getPostText());
        try{
            postTimeCreatedTextView.setText(DateUtils.getRelativeTimeSpanString(mPost.getTimeCreated()));

        }
        catch(Exception e)
        {

        }
        isLiked(mPost.getPostId(),like);
        nrLikes(postNumLikesTextView,mPost.getPostId(),mPost);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(mPost.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(mPost.getPostId())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        postNumLikesTextView.setText(String.valueOf(mPost.getNumLikes()));
        postPlace.setText(String.valueOf(mPost.getShoppingPlace()));
        postPrice.setText(String.valueOf(mPost.getPrice()));
        postProduct.setText(String.valueOf(mPost.getProduct()));
        postNumCommentTextView.setText(String.valueOf(mPost.getNumComments()));

        Glide.with(PostActivity.this).load(mPost.getUser().getPhotoUrl())
                .into(postOwnerDisplayImageView);


        if(mPost.getPhotoImageUri()!=null){
            postDisplayImageView.setVisibility(View.VISIBLE);
            StorageReference storageReference=FirebaseStorage.getInstance().getReference(mPost.getPhotoImageUri());

            Glide.with(PostActivity.this).load(storageReference).into(postDisplayImageView);
        }else {
            postDisplayImageView.setImageBitmap(null);
            postDisplayImageView.setVisibility(View.GONE);
        }




    }

    private void init() {
        mCommentEditTextView=(EditText)findViewById(R.id.et_comment);
        findViewById(R.id.iv_send).setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {


        switch (view.getId()){
            case R.id.iv_send:
                sendComment();
        }
    }

    private void sendComment() {

        final ProgressDialog progressDialog=new ProgressDialog(PostActivity.this);
        progressDialog.setMessage("Sending");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        mcomment=new Comment();
        final String uid= FirebaseUtils.getUid();
        String strComment=mCommentEditTextView.getText().toString();
        //User user=new User();
        mcomment.setCommentId(uid);
        mcomment.setComment(strComment);
        try{
            mcomment.setTimeCreated(System.currentTimeMillis());

        }
        catch (Exception e){

        }
        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.getValue(User.class);
                        mcomment.setUser(user);

                        //User cuser=dataSnapshot.getValue(User.class);

                        FirebaseUtils.getCommentsRef(mPost.getPostId()).child(uid)
                                .setValue(mcomment);
                      //  mPost.setNumComments();

                        FirebaseUtils.getPostRef().child(mPost.getPostId()).child(Constants.NUM_COMMENTS_KEY)
                                .runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        Long num=(Long)mutableData.getValue();
                                        mutableData.setValue(num+1);
                                        return Transaction.success(mutableData);
                                    }


                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                       // mPost.setNumComments(mPost.getNumComments()+1);
                                        addNotifications(mPost,mcomment.getComment(),mcomment.getUser());

                                        progressDialog.dismiss();
                                        FirebaseUtils.addToMyRecord(Constants.COMMENTS_KEY,uid);
                                       // addToMyCommentsList(firebaseUser.getUid());


                                        /*Intent intent=new Intent(PostActivity.this,PostActivity.class);
                                        startActivity(intent);*/
                                      mCommentEditTextView.setText("");



                                    }
                                });
                    }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        progressDialog.dismiss();
                    }
                });





    }
    private void isLiked(String postId,final ImageView imageView){

        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.alreadylike);
                    imageView.setTag("liked");

                }
                else {
                    imageView.setImageResource(R.drawable.notlike);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrLikes(final TextView likes, String postId,final Post post){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                likes.setText(dataSnapshot.getChildrenCount()+"likes");
                FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.getValue(User.class);
                        //addNotifications(post,user);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void addNotifications(final Post post, final String comment, final User user){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(user.getuId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             Notifications notifications=new Notifications();
             notifications.setUser(user);
             notifications.setNotificationId(user.getuId()+post.getPostId()+System.currentTimeMillis());
            // notifications.setUser_id(user);
              //  notifications.setPost_id(postId);
                notifications.setPost(post);
                notifications.setText("commented on "+post.getUser().getUser()+"'s post : "+comment);
                FirebaseUtils.getNotificationsRef(notifications.getNotificationId()).setValue(notifications);
               // notifications.setPost(true);
              //  FirebaseDatabase.getInstance().getReference("Notifications")
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
     /*   HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("user_id",firebaseUser.getUid());
        hashMap.put("text","commented "+comment+" on your post");
        hashMap.put("post_id",postId);
        hashMap.put("isPost",true);


        reference.push().setValue(hashMap);*/

    }

   /* private void addToMyCommentsList(String CommentId) {
    FirebaseUtils.getMyCommentsRef().setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

        }
    });

    }*/

    public static class CommentHolder extends RecyclerView.ViewHolder {



        ImageView commentOwnerDisplay;
        TextView usernameTextView;
        TextView timeTextview;
        TextView commentTextView;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);


            commentOwnerDisplay=(ImageView)itemView.findViewById(R.id.iv_comment_owner_display);
            timeTextview=(TextView)itemView.findViewById(R.id.tv_time);
            commentTextView=(TextView)itemView.findViewById(R.id.tv_commentDisplay);
            usernameTextView=(TextView)itemView.findViewById(R.id.tv_username);




        }
        public void setUsername(String username){
            usernameTextView.setText(username);
        }
        public void setTime(CharSequence time){
            timeTextview.setText(time);
        }
        public void setComment(String comment_string){
            commentTextView.setText(comment_string);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_COMMENT, mcomment);
        super.onSaveInstanceState(outState);
    }

}