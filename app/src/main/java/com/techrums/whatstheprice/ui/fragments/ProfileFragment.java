package com.techrums.whatstheprice.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.techrums.whatstheprice.adapters.My_foto_Adapter;
import com.techrums.whatstheprice.adapters.ProductsListAdapter;
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.ui.activities.Backable;
import com.techrums.whatstheprice.ui.activities.HomeActivity;
import com.techrums.whatstheprice.utils.Constants;
import com.techrums.whatstheprice.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements Backable {

private CircleImageView profilePic,profileHeader;
private TextView email,username;
private TextView followers,following,posts;
private Button editButton,followButton;
String profileId,profileEmail;
private ImageView sex;
private TextView bio;
RecyclerView recyclerView;
RecyclerView postRecyclerView;
My_foto_Adapter my_foto_adapter;
List<Post> postList;
ProductsListAdapter productsListAdapter;
ImageView myFotos,savedPosts;
//private User currentUser;
private FirebaseUser firebaseUser;
private Context mContext;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        myFotos=view.findViewById(R.id.my_fotos);
        bio=view.findViewById(R.id.bioProfilefrag);
        sex=view.findViewById(R.id.sexCheck);
        profileHeader=view.findViewById(R.id.pro);
        savedPosts=view.findViewById(R.id.my_posts);
        profilePic=(CircleImageView)view.findViewById(R.id.displayPicture);
        username=(TextView)view.findViewById(R.id.nameProfilefrag);
        email=view.findViewById(R.id.emailProfilefrag);
        followers=view.findViewById(R.id.followersProfilefrag);
        following=view.findViewById(R.id.followingsProfilefrag);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        editButton=view.findViewById(R.id.editProfileFrag);
        postRecyclerView=view.findViewById(R.id.my_posts_recycler);
        recyclerView=view.findViewById(R.id.my_fotos_recycler);
        GridLayoutManager linearLayoutManager=new GridLayoutManager(getActivity(),3);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getContext()) ;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);
        postRecyclerView.setHasFixedSize(true);

        postList=new ArrayList<>();
        my_foto_adapter=new My_foto_Adapter(getActivity(),postList);
        recyclerView.setAdapter(my_foto_adapter);

        postRecyclerView.setLayoutManager(linearLayoutManager1);

        productsListAdapter = new ProductsListAdapter(getActivity(),postList);
        postRecyclerView.setAdapter(productsListAdapter);

        posts=view.findViewById(R.id.no_photosProfilefrag);



          SharedPreferences prefs=getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
            profileEmail=prefs.getString("profileid",null);
            userInfo();
        myFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRecyclerView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
        savedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                postRecyclerView.setVisibility(View.VISIBLE);
                My_fotos();
            }
        });
        return view;
    }
    @Override
    public boolean onBackPressed() {

        // Logic here...

       ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SearchFragment()).commit();
        return true;
    }
    private void initProfile() {

    }


    private void userInfo(){
        FirebaseUtils.getUserRef(profileEmail.replace(".",","))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //getContext
                if(getActivity()==null){
                    return;
                }else {
                       final User user = dataSnapshot.getValue(User.class);

                        if(user.getPhotoUrl()!=null){
                            GlideApp.with(getActivity()).load(user.getPhotoUrl()).into(profilePic);
                            GlideApp.with(getActivity()).load(user.getPhotoUrl()).into(profileHeader);


                        }
                        if(user.getSex()!=null){
                            sex.setVisibility(View.VISIBLE);

                            if(user.getSex().equals("Male")){
                                sex.setImageResource(R.drawable.boysex);
                            }
                            else if(user.getSex().equals("Female")){
                                sex.setImageResource(R.drawable.girlsex);
                            }
                            else if(user.getSex().equals("Others")){
                                sex.setVisibility(View.INVISIBLE);
                            }
                        }
                        if(user.getBio()!=null){
                            bio.setVisibility(View.VISIBLE);
                            bio.setText(user.getBio());
                        }
                            username.setText(user.getUser());
                            email.setText(user.getEmail());

                            profileId=user.getuId();
                    if (profileId.equals(firebaseUser.getUid())){
                        editButton.setText("Edit Profile");
                    }
                    else {
                        checkFollow();
                    }
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String btn=editButton.getText().toString();

              /*  if (profileId.equals(firebaseUser.getUid())){
                    editButton.setText("Edit Profile");
                }*/
                            if(btn.equals("Edit Profile")){
                                //go to  edit profile
                                SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                                editor.putString("profileEmail",profileEmail);
                                editor.apply();

                                ((HomeActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EditProfileFragment()).commit();
                            }
                            else if(btn.equals("Follow")){
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Follow").child(firebaseUser.getUid()).child("Following").child(profileId).setValue(true);
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Follow").child(profileId).child("Followers").child(firebaseUser.getUid()).setValue(true);

                            }
                            else if(btn.equals("Following")){
                                FirebaseDatabase.getInstance().getReference().
                                        child("Follow").child(firebaseUser.getUid()).child("Following").child(profileId).removeValue();
                                FirebaseDatabase.getInstance().getReference().
                                        child("Follow").child(profileId).child("Followers").child(firebaseUser.getUid()).removeValue();

                            }
                        }
                    });
                    initProfile();


                    // final String profileId=getArguments().getString("profileid");


                    // initProfile();

                    getFollowers();
                    getNrPosts();
                    My_fotos();
                    getFollowings();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(profileId).exists()){
                    editButton.setText("Following");
                }
                else {
                    editButton.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void My_fotos(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    if (post.getUser().getuId().equals(profileId)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                my_foto_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getFollowers(){
     DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(profileId).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void getFollowings(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(profileId).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getNrPosts(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    if(post.getUser().getuId().equals(profileId)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
