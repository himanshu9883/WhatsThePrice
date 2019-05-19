package com.techrums.whatstheprice.ui.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techrums.whatstheprice.GlideApp;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.models.Notifications;
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.ui.activities.HomeActivity;
import com.techrums.whatstheprice.utils.FirebaseUtils;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {
    String sex;
private EditText username,bio;
private Button saveButton;
private TextView email;
private ImageView dp;
private FirebaseUser firebaseUser;
String profileEmail;
private Spinner spin;
    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_edit_profile, container, false);
        bio=view.findViewById(R.id.editBio);
        spin = (Spinner)view.findViewById(R.id.spin);
        String sexlist[]={"Male","Female","Others"};
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,sexlist);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);


        username=view.findViewById(R.id.editUser);
        saveButton=view.findViewById(R.id.saveUserbutton);
        dp=view.findViewById(R.id.editpostdp);
        email=view.findViewById(R.id.emailEditPro);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs=getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileEmail=prefs.getString("profileEmail","none");

        showProfile();
        return view;
    }

    private void showProfile() {
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sex=spin.getSelectedItem().toString();
                if(sex.equals("Male")){
                    Toast.makeText(getContext(),"Male",Toast.LENGTH_SHORT).show();

                }
                else if(sex.equals("Female")){
                    Toast.makeText(getContext(),"Female",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),"Others",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users").child(profileEmail.replace(".",","));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getActivity()==null){
                    return;
                }
                else {

                    final User user = dataSnapshot.getValue(User.class);
                    username.setText(user.getUser());
                    email.setText(user.getEmail());
                    if(user.getBio()!=null){
                        bio.setText(user.getBio());
                    }
                    if(user.getPhotoUrl()!=null){
                        GlideApp.with(getActivity()).load(user.getPhotoUrl()).into(dp);

                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                save(user,sex);
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void save(final User user,String sex) {

            String UserEdit=username.getText().toString();
            String bioEdit=bio.getText().toString();
            user.setUser(UserEdit);
            user.setBio(bioEdit);
            user.setSex(sex);
        FirebaseUtils.getUserRef(profileEmail.replace(".",",")).setValue(user);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    if(post.getUser().getEmail().equals(profileEmail)){
                        post.setUser(user);
                        FirebaseUtils.getPostRef().child(post.getPostId()).setValue(post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Notifications");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){{
                    Notifications notifications=snapshot.getValue(Notifications.class);
                    if(notifications.getUser().getEmail().equals(profileEmail)){
                        notifications.setUser(user);
                        FirebaseUtils.getNotificationsRef(notifications.getNotificationId()).setValue(notifications);

                    }
                }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileEmail",profileEmail);
        editor.apply();
        ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
        }
    }


