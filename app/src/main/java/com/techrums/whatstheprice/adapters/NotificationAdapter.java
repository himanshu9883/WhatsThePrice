package com.techrums.whatstheprice.adapters;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.techrums.whatstheprice.GlideApp;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.models.Notifications;
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.ui.activities.HomeActivity;
import com.techrums.whatstheprice.ui.activities.PostActivity;
import com.techrums.whatstheprice.ui.fragments.HomeFragment;
import com.techrums.whatstheprice.ui.fragments.NotificationsFragment;
import com.techrums.whatstheprice.ui.fragments.ProductFragment;
import com.techrums.whatstheprice.ui.fragments.ProfileFragment;
import com.techrums.whatstheprice.ui.fragments.SettingsFragment;
import com.techrums.whatstheprice.utils.Constants;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context mContext;
    private List<Notifications> mNotification;
    private FirebaseUser mUser;

    public NotificationAdapter(Context mContext, List<Notifications> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

  /*  public NotificationAdapter(List<Notifications> notificationsList) {


    }*/

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.notification_item,viewGroup,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        final Notifications classnotifications=mNotification.get(i);
       //Notification notifications=mNotification.get(i);

        viewHolder.text.setText(classnotifications.getText());
     //   getUserInfo(viewHolder.image_profile,viewHolder.username,classnotifications.getUser().getuId());
        GlideApp.with(mContext).load(classnotifications.getUser().getPhotoUrl()).into(viewHolder.image_profile);
        viewHolder.username.setText(classnotifications.getUser().getUser());


        //  if (classnotifications.isPost()){
        if (classnotifications.getPost()!=null){
            viewHolder.post_image.setVisibility(View.VISIBLE);
            //  getPostImage(viewHolder.post_image,classnotifications.getPost().getPostId());
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(classnotifications.getPost().getPhotoImageUri());
            GlideApp.with(mContext).load(storageReference).centerCrop().fitCenter().into(viewHolder.post_image);

        }
        else {
            viewHolder.post_image.setVisibility(View.GONE);
        }

    /*    }else{
            viewHolder.post_image.setVisibility(View.GONE);
        }*/
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts").child(classnotifications.getPost().getPostId());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                            SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                            editor.putString("postId",classnotifications.getPost().getPostId());
                            editor.apply();

                            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProductFragment()).commit();

                        }
                        else {
                            Toast.makeText(mContext,"Post doesn't exist",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

              //  if(classnotifications.getPost()){


             //}else{

                /*    SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",classnotifications.getUser().getuId());
                    editor.apply();*/
                  /*  Intent intent=new Intent(getContext(), PostActivity.class);
                    intent.putExtra(Constants.EXTRA_POST,model);*/


                    //Make sure that fragment container is the correct thing

                  //  ((FragmentActivity)mContext).getFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
               // }
            }
        });
    }

    @Override
    public int getItemCount() {

        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image_profile,post_image;
        public TextView username,text;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            image_profile=itemView.findViewById(R.id.image_profile_nf);
            post_image=itemView.findViewById(R.id.post_image_nf);
            username=itemView.findViewById(R.id.username_nf);
            text=itemView.findViewById(R.id.comment_nf);
        }
    }

  /*  private void getUserInfo(final ImageView imageView, final TextView username, String publisher_id){

       // Post mPost=new Post();
        //Check this getUser() for error. Try to replace with getUid()
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(publisher_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //User user=dataSnapshot.getValue(User.class);
                //check for notification class change it with user class if problem occurs
                Notifications notifications =dataSnapshot.getValue(Notifications.class);
                GlideApp.with(mContext).load(notifications.getUser().getPhotoUrl()).into(imageView);
                username.setText(notifications.getUser().getUser());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
  /*  private void getPostImage(final ImageView imageView, String postId){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post=dataSnapshot.getValue(Post.class);
                StorageReference storageReference= FirebaseStorage.getInstance().getReference(post.getPhotoImageUri());
                GlideApp.with(mContext).load(storageReference).centerCrop().fitCenter().into(imageView);
               // GlideApp.with(mContext).load(post.getPhotoImageUri()).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

}
