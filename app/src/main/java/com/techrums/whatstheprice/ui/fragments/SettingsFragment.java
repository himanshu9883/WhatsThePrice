package com.techrums.whatstheprice.ui.fragments;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.squareup.picasso.Picasso;
import com.techrums.whatstheprice.GlideApp;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.ui.activities.AboutUsActivity;
import com.techrums.whatstheprice.ui.activities.Backable;
import com.techrums.whatstheprice.ui.activities.HomeActivity;
import com.techrums.whatstheprice.ui.activities.LoginActivity;
import com.techrums.whatstheprice.utils.Constants;
import com.techrums.whatstheprice.utils.FirebaseUtils;


import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsFragment extends Fragment implements Backable {

    private CircleImageView Profile_pic;
    private TextView name,email;
    private Button about_us_btn,profile_btn,share_btn,logout_btn;
    private Toolbar mtoolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference UserRef,postsRef;

    String profileid;
    private String current_user_Id;
    private FirebaseDatabase mDatabase;
    private View view;
    private StorageReference UserProfileImageRef;

    public SettingsFragment(){

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth=FirebaseAuth.getInstance();
        view=inflater.inflate(R.layout.fragment_settings, container, false);
        Profile_pic=(CircleImageView) view.findViewById(R.id.photo_settings);
        name=(TextView)view.findViewById(R.id.username_settings);
        UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
        if(mAuth.getCurrentUser()!=null){
            current_user_Id=mAuth.getCurrentUser().getUid();

        }
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        email=(TextView)view.findViewById(R.id.userEmail_settings);
        about_us_btn=(Button)view.findViewById(R.id.about_app);
        share_btn=(Button)view.findViewById(R.id.share_settings);
        profile_btn=(Button)view.findViewById(R.id.profile_button);
        logout_btn=(Button)view.findViewById(R.id.logout_settings);


        about_us_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutIntent=new Intent(getActivity(), AboutUsActivity.class);
                startActivity(aboutIntent);
            }
        });


        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               userInfo();
            }
        });


        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent loginintent=new Intent(getActivity(), LoginActivity.class);
                startActivity(loginintent);
            }
        });
        init();


        UserRef= FirebaseDatabase.getInstance().getReference().child("users");



        UserRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                if(dataSnapshot.exists()) {
                    if(dataSnapshot.hasChild("user")){
                        String Fullname = dataSnapshot.child("Username").getValue().toString();
                        name.setText(Fullname);
                    }
                   /* if(dataSnapshot.hasChild("About")){
                        String username = dataSnapshot.child("About").getValue().toString();
                        name.setText(username);
                    }*/
                    if(dataSnapshot.hasChild("email")){
                        String emaildisplay = dataSnapshot.child("email").getValue().toString();
                        email.setText(emaildisplay);
                    }




                   /* if(dataSnapshot.hasChild("profile image")){
                        String image = dataSnapshot.child("profile image").getValue().toString();

                        // StorageReference storageReference= FirebaseStorage.getInstance().getReference();
                        //StorageReference storageReference=UserProfileImageRef.
                        //Glide.with(getActivity()).load(storageReference).into(viewHolder.postDisplayImageView);
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(Profile_pic);


                    }*/
                    else{
                        Toast.makeText(getActivity(),"profile name doesn't exist",Toast.LENGTH_SHORT).show();
                    }




                    //}
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        return view;



    }

    private void userInfo() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);


                    if (user.getuId().equals(firebaseUser.getUid())) {
                        profileid = user.getEmail();
                        SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("profileid", profileid);
                        editor.apply();
                        ((HomeActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onBackPressed() {

        // Logic here...

        ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        return true;
    }


    private void init() {
        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.getValue(User.class);
                        name.setText(user.getUser());
                        email.setText(user.getEmail());
                        GlideApp.with(getContext()).load(user.getPhotoUrl()).into(Profile_pic);



    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}