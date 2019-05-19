package com.techrums.whatstheprice.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.techrums.whatstheprice.GlideApp;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.models.Notifications;
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.ui.activities.Backable;
import com.techrums.whatstheprice.ui.activities.HomeActivity;
import com.techrums.whatstheprice.ui.activities.PostActivity;
import com.techrums.whatstheprice.utils.Constants;
import com.techrums.whatstheprice.utils.FirebaseUtils;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProductFragment extends Fragment implements Backable {

    private FirebaseUser firebaseUser;
    private ImageView product_image;
    private CircleImageView product_owner;
    private TextView product,price,caption,place,likes,comments,username,caption_user,dislikes;
    private TextView time;
    String postId;
    LinearLayout postLikeLayout,postCommentLayout;
    ImageView like,dislike,trash;
    public ProductFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_product, container, false);
        product_image=view.findViewById(R.id.tv_post_display);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        product_owner=view.findViewById(R.id.tv_post_owner_display);
        product=view.findViewById(R.id.post_display_product);
        price=view.findViewById(R.id.post_display_price);
        place=view.findViewById(R.id.post_display_place);
        caption=view.findViewById(R.id.tv_post_text);
        likes=view.findViewById(R.id.tv_likes);
        username=view.findViewById(R.id.tv_post_username);
        comments=view.findViewById(R.id.tv_comments);
        postLikeLayout=(LinearLayout)view.findViewById(R.id.like_layout);
        postCommentLayout=(LinearLayout)view.findViewById(R.id.comment_layout);

        trash=(ImageView)view.findViewById(R.id.trash);
        caption_user=view.findViewById(R.id.caption_user);
        time=view.findViewById(R.id.post_time);
        like=view.findViewById(R.id.tv_like);
        dislike=view.findViewById(R.id.tv_dislike);
        dislikes=view.findViewById(R.id.tv_dislikes);

        SharedPreferences prefs=getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postId=prefs.getString("postId","none");


        seeProduct();

        return view;
    }

    private void seeProduct() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getActivity()==null){
                    return;
                }
                else {

                    final Post post=dataSnapshot.getValue(Post.class);
                     if(post.getPhotoImageUri() != null){
                        StorageReference storageReference= FirebaseStorage.getInstance().getReference(post.getPhotoImageUri());

                        GlideApp.with(getActivity()).load(storageReference).fitCenter().into(product_image);


                    }

                    if(post.getUser().getPhotoUrl()!=null){
                        GlideApp.with(getActivity()).load(post.getUser().getPhotoUrl()).into(product_owner);

                    }



                    if(post.getUser().getuId().equals(firebaseUser.getUid())){
                        trash.setVisibility(View.VISIBLE);
                        trash.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeProduct(post);
                            }
                        });
                    }
                    else {
                        trash.setVisibility(View.INVISIBLE);
                    }

                    caption_user.setText(post.getUser().getUser().toUpperCase());
                   // time.setText(String.valueOf(post.getTimeCreated()));

                    username.setText(post.getUser().getUser().toUpperCase());
                    place.setText(post.getShoppingPlace().toUpperCase());
                    product.setText(String.valueOf(post.getProduct()).toUpperCase());
                    price.setText(String.valueOf(post.getPrice()));
                    time.setText(DateUtils.getRelativeTimeSpanString(post.getTimeCreated()));

                   // time.setText(String.valueOf(post.getTimeCreated()));
                   // time.setVisibility(View.GONE);
                   // likes.setText(String.valueOf(post.getNumLikes()));
                    comments.setText(String.valueOf(post.getNumComments()));
                    caption.setText(post.getPostText().toUpperCase());

                    postCommentLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(getContext(), PostActivity.class);
                            intent.putExtra(Constants.EXTRA_POST,post);
                            startActivity(intent);
                        }
                    });
                    isDisliked(post.getPostId(),dislike,like);
                    nrDislikes(dislikes,post.getPostId(),post);

                    dislike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(dislike.getTag().equals("dislike")){
                                FirebaseDatabase.getInstance().getReference().child("DisLikes").child(post.getPostId())
                                        .child(firebaseUser.getUid()).setValue(true);
                            }
                            else {
                                FirebaseDatabase.getInstance().getReference().child("DisLikes").child(post.getPostId())
                                        .child(firebaseUser.getUid()).removeValue();
                            }
                        }
                    });
                    isLiked(post.getPostId(),like);
                    nrLikes(likes,post.getPostId(),post);
                    like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(like.getTag().equals("like")){
                                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                                        .child(firebaseUser.getUid()).setValue(true);
                            }
                            else {
                                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                                        .child(firebaseUser.getUid()).removeValue();
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editProdct(Post post){

    }

    private void removeProduct(Post post) {

        SharedPreferences.Editor editor=getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("postId",post.getPostId());
        editor.apply();
        ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EditPostFragment()).commit();
    }


    private void isDisliked(String postId, final ImageView imageView, final ImageView imageViewl){

        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("DisLikes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.dislikec);
                    imageView.setTag("disliked");
                    imageViewl.setVisibility(View.GONE);
                }
                else {
                    imageView.setImageResource(R.drawable.dilike);
                    imageView.setTag("dislike");
                    imageViewl.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void nrDislikes(final TextView dislikes, String postId,final Post post){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("DisLikes").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dislikes.setText(dataSnapshot.getChildrenCount()+"dislikes");
                FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.getValue(User.class);
                        addNotifications(post,user,"disliked");

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
    private void isLiked(String postId, final ImageView imageView){

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.alreadylike);
                    imageView.setTag("liked");

                }
                else {
                    imageView.setImageResource(R.drawable.likedash);
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
                        addNotifications(post,user,"liked");

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

    public void addNotifications(final Post post, final User user, final String string){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // User user=dataSnapshot.getValue(User.class);
                Notifications notifications=new Notifications();
                notifications.setUser(user);
                // notifications.setUser_id(user);
                //  notifications.setPost_id(postId);
                notifications.setNotificationId(user.getuId()+post.getPostId());
                notifications.setPost(post);
                notifications.setText(/*user.getUser()+*/string+post.getUser().getUser()+"'s post  ");
                // notifications.setPost(true);
                //  FirebaseDatabase.getInstance().getReference("Notifications")
                FirebaseUtils.getNotificationsRef(notifications.getNotificationId()).setValue(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onBackPressed() {

        // Logic here...
        //switch ()
        ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        return true;
    }
}
