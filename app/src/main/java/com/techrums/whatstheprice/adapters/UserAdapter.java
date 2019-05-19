package com.techrums.whatstheprice.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.techrums.whatstheprice.ui.activities.PostActivity;
import com.techrums.whatstheprice.ui.fragments.HomeFragment;
import com.techrums.whatstheprice.ui.fragments.ProfileFragment;
import com.techrums.whatstheprice.utils.Constants;
import com.techrums.whatstheprice.utils.FirebaseUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    String followername;

    private Context mContext;
    private List<User> mUsers;
    private FirebaseUser mUser;
   // private List<Post> mPosts;

    public UserAdapter(Context mContext, List<User> mUsers/*, List<Post> mPosts*/) {
        this.mContext = mContext;
        this.mUsers = mUsers;




    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,viewGroup,false);
        return new UserAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        final User user=mUsers.get(i);
        //check
      //  final Post post=mPosts.get(i);
        viewHolder.followButton.setVisibility(View.VISIBLE);
        viewHolder.username.setText(user.getUser());

        viewHolder.email.setText(user.getEmail());
        GlideApp.with(mContext).load(user.getPhotoUrl()).fitCenter().into(viewHolder.imageProfile);
        isFollowing(user.getuId(),viewHolder.followButton);
        if(user.getuId().equals(mUser.getUid())){

            viewHolder.followButton.setVisibility(View.GONE);

        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




              /*  Intent intent=new Intent(mContext, ProfileFragment.class);
                intent.putExtra("user",user);*/
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",user.getEmail());
                editor.apply();
                ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();


            }
        });

        viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.followButton.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(mUser.getUid()).child("Following").child(user.getuId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getuId()).child("Followers").child(mUser.getUid()).setValue(true);
                    addNotifications(user);

                }else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(mUser.getUid()).child("Following")
                            .child(user.getuId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getuId())
                            .child("Followers").child(mUser.getUid()).removeValue();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private CircleImageView imageProfile;
        private TextView email;
        private Button followButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username_search);
            imageProfile=itemView.findViewById(R.id.image_user_search);
            email=itemView.findViewById(R.id.user_email_search);
            followButton=itemView.findViewById(R.id.button_follow);
        }
    }

    public void addNotifications(final User user){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Notifications notifications=new Notifications();
                notifications.setNotificationId(user.getuId()+ mUser.getUid());
                notifications.setPost(null);
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            User follower=snapshot.getValue(User.class);
                            if (follower.getuId().equals(mUser.getUid())){
                                followername=follower.getUser();
                                notifications.setUser(follower);


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                notifications.setUser(user);

                notifications.setText(followername+" started following "+user.getUser());

                FirebaseUtils.getNotificationsRef(notifications.getNotificationId()).setValue(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void isFollowing(final String userId, final Button button){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Follow").child(mUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userId).exists()){
                    button.setText("Following");

                }
                else {
                    button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
