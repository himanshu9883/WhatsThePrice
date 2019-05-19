package com.techrums.whatstheprice;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView Profile_pic;
    private TextView followers,name,following,photosno,email,age;

    private Toolbar mtoolbar;
    private FirebaseAuth mAuth;

    private DatabaseReference UserRef,postsRef;

    private String current_user_Id;
    private FirebaseDatabase mDatabase;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Profile_pic=(CircleImageView)findViewById(R.id.photo_profile);
        followers=(TextView)findViewById(R.id.followers);
        following=(TextView)findViewById(R.id.followings);
        photosno=(TextView)findViewById(R.id.no_photos);
        email=(TextView)findViewById(R.id.emailProfile);
        age=(TextView)findViewById(R.id.number_profile);
        name=(TextView)findViewById(R.id.username_profile);
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottom_navigation_profile);


        mAuth=FirebaseAuth.getInstance();
        current_user_Id=mAuth.getCurrentUser().getUid();




        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");



        UserRef.child(current_user_Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                if(dataSnapshot.exists()) {
                    if(dataSnapshot.hasChild("Username")){
                        String Fullname = dataSnapshot.child("Username").getValue().toString();
                        name.setText(Fullname);
                    }
                    if(dataSnapshot.hasChild("Age")){
                        String Age = dataSnapshot.child("Age").getValue().toString();
                        age.setText(Age);
                    }
                    if(dataSnapshot.hasChild("Email")){
                        String emaildisplay = dataSnapshot.child("Email").getValue().toString();
                        email.setText(emaildisplay);
                    }




                    if(dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();


                        Picasso.get().load(image).placeholder(R.drawable.profile).into(Profile_pic);

                    }
                    else{
                        Toast.makeText(ProfileActivity.this,"profile name doesn't exist",Toast.LENGTH_SHORT).show();
                    }




                    //}
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });










        showProfile();
    }

    private void showProfile() {

    }
}
