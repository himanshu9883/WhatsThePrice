package com.techrums.whatstheprice.ui.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.techrums.whatstheprice.GlideApp;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.adapters.CommentAdapter;
import com.techrums.whatstheprice.models.Comment;
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.utils.Constants;
import com.techrums.whatstheprice.utils.FirebaseUtils;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;


    EditText addComment;
    CircleImageView image_profile;
    ImageView post;
    String postId;
    String publisherId;
    Post mPost;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycler_view_comments);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(commentAdapter);

        Toolbar toolbar=findViewById(R.id.CommentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addComment=findViewById(R.id.add_comment);
        image_profile=findViewById(R.id.image_profile_comments);
        post=findViewById(R.id.post);
        Intent intent=getIntent();

      // postId=intent.getStringExtra(Constants.EXTRA_POST);
        mPost=(Post)intent.getSerializableExtra(Constants.EXTRA_POST);
        postId=mPost.getPostId();
        publisherId=mPost.getUser().getuId();
       post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addComment.getText().toString().equals("")){
                    Toast.makeText(CommentActivity.this,"write something",Toast.LENGTH_SHORT).show();
                }else{
                    addComment();
                }
            }
        });
       getImage();

    }

   private void addComment() {

        DatabaseReference reference= FirebaseUtils.getCommentsRef(postId);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("comment",addComment.getText().toString());
        //hashMap.put("publisher",firebaseUser.getUid());
        hashMap.put("commentId",firebaseUser.getUid());
        hashMap.put("timeCreated",System.currentTimeMillis());
        reference.push().setValue(hashMap);
        addComment.setText("");
       FirebaseUtils.getPostRef().child(mPost.getPostId()).child(Constants.NUM_COMMENTS_KEY)
               .runTransaction(new Transaction.Handler() {
                   @Override
                   public Transaction.Result doTransaction(MutableData mutableData) {
                       long num=(long)mutableData.getValue();
                       mutableData.setValue(num+1);
                       return Transaction.success(mutableData);
                   }

                   @Override
                   public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                       //progressDialog.dismiss();
                       FirebaseUtils.addToMyRecord(Constants.COMMENTS_KEY,FirebaseUtils.getUid());
                      // addNotifications(mPost.getPostId(),comment.getComment(),mPost.getUser().getuId());

                   }

               });
    }

    private void getImage(){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                GlideApp.with(getApplicationContext()).load(user.getPhotoUrl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
