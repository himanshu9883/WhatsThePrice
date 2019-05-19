package com.techrums.whatstheprice.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.techrums.whatstheprice.GlideApp;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.models.Comment;
import com.techrums.whatstheprice.models.Notifications;
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.ui.activities.Backable;
import com.techrums.whatstheprice.ui.activities.CommentActivity;
import com.techrums.whatstheprice.ui.activities.HomeActivity;
import com.techrums.whatstheprice.ui.activities.PostActivity;
import com.techrums.whatstheprice.ui.dialogs.PostCreateDialog;
import com.techrums.whatstheprice.utils.Constants;
import com.techrums.whatstheprice.utils.FirebaseUtils;
import com.techrums.whatstheprice.SampleGlideModule;

import java.util.HashMap;

import static com.bumptech.glide.Glide.with;


public class HomeFragment extends Fragment implements Backable {

    private View mRootView;
    private FirebaseRecyclerAdapter<Post,PostHolder> mPostAdapter;
    private RecyclerView mPostRecyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    public HomeFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView= inflater.inflate(R.layout.fragment_home,container,false);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        FloatingActionButton fab=(FloatingActionButton)mRootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostCreateDialog dialog=new PostCreateDialog();
                dialog.show(getFragmentManager(),null);

            }
        });
        init();
        return mRootView;
    }

    private void init() {
        mPostRecyclerView=(RecyclerView)mRootView.findViewById(R.id.recycler_view_post);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mPostRecyclerView.setLayoutManager(linearLayoutManager);

        setUPAdapter();
        mPostRecyclerView.setAdapter(mPostAdapter);
    }

    private void setUPAdapter() {
        mPostAdapter=new FirebaseRecyclerAdapter<Post, PostHolder>(Post.class,
                R.layout.row_post,
                PostHolder.class, FirebaseUtils.getPostRef()) {

            @Override
            protected void populateViewHolder(final PostHolder viewHolder, final Post model, int position) {
                viewHolder.setNumComments(String.valueOf(model.getNumComments()));
                viewHolder.setNumLikes(String.valueOf(model.getNumLikes()));
                viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
                viewHolder.setUsername(String.valueOf(model.getUser().getUser()));
                viewHolder.setPostPrice(String.valueOf(model.getPrice()));
                viewHolder.setPostPlace(String.valueOf(model.getShoppingPlace()));
                viewHolder.setPostProduct(String.valueOf(model.getProduct()));
                viewHolder.setUsername_caption(String.valueOf(model.getUser().getUser()));

                if(model.getPostText()!=null){
                    viewHolder.setPostText(String.valueOf(model.getPostText()));
                    viewHolder.postTextTextView.setVisibility(View.VISIBLE);


                }
                else {
                    viewHolder.postTextTextView.setVisibility(View.GONE);
                }

                GlideApp.with(getActivity())
                        .load(model.getUser().getPhotoUrl())
                        .into(viewHolder.postOwnerDisplayImageView);


               /* viewHolder.options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("postProduct",model.getProduct());
                        editor.apply();

                    }
                });*/

                if(model.getPhotoImageUri()!=null){

                    viewHolder.postDisplayImageView.setVisibility(View.VISIBLE);
                    StorageReference storageReference=FirebaseStorage.getInstance().getReference(model.getPhotoImageUri());

                  GlideApp.with(getActivity()).load(storageReference).centerCrop().fitCenter().into(viewHolder.postDisplayImageView);

                }else {
                    viewHolder.postDisplayImageView.setImageBitmap(null);
                    viewHolder.postDisplayImageView.setVisibility(View.GONE);
                }
                if(model.getUser().getuId().equals(firebaseUser.getUid())){
                    viewHolder.trash.setVisibility(View.VISIBLE);
                    viewHolder.trash.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editPost(model);
                        }
                    });
                }
                else{
                  /*  viewHolder.trash.setImageResource(R.drawable.ic_save_black_24dp);
                    viewHolder.trash.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            savePhoto();
                        }
                    });*/
                  viewHolder.trash.setVisibility(View.GONE);
                }

                isLiked(model.getPostId(),viewHolder.like);
                nrLikes(viewHolder.postNumLikesTextView,model.getPostId(),model);

                viewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(viewHolder.like.getTag().equals("like")){
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getPostId())
                                    .child(firebaseUser.getUid()).setValue(true);
                        }
                        else {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getPostId())
                                    .child(firebaseUser.getUid()).removeValue();
                        }
                    }
                });


                isDisliked(model.getPostId(),viewHolder.dislike,viewHolder.like,viewHolder.postNumLikesTextView,model);
                nrDislikes(viewHolder.dislikes,model.getPostId(),model);

                viewHolder.dislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(viewHolder.dislike.getTag().equals("dislike")){
                            FirebaseDatabase.getInstance().getReference().child("DisLikes").child(model.getPostId())
                                    .child(firebaseUser.getUid()).setValue(true);
                        }
                        else {
                            FirebaseDatabase.getInstance().getReference().child("DisLikes").child(model.getPostId())
                                    .child(firebaseUser.getUid()).removeValue();
                        }
                    }
                });


                viewHolder.postCommentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getContext(), PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST,model);
                        startActivity(intent);
                    }
                });
                viewHolder.postDisplayImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                        editor.putString("postId",model.getPostId());
                        editor.apply();
                        ((HomeActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProductFragment()).commit();

                    }
                });

                viewHolder.postPlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                        editor.putString("place",model.getShoppingPlace());
                        editor.apply();
                        ((HomeActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SimilarPlaceFragment()).commit();

                    }
                });
                viewHolder.postProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                        editor.putString("product",model.getProduct());
                        editor.apply();
                        ((HomeActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SimilarProductFragment()).commit();

                    }
                });

                viewHolder.postOwnerDisplayImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                        editor.putString("profilePic",model.getUser().getPhotoUrl());
                        editor.apply();
                        ((HomeActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DisplayPicFragment()).commit();

                    }
                });
                viewHolder.options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });

                viewHolder.postOwnerUsernameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                        editor.putString("profileid",model.getUser().getEmail());
                        editor.apply();
                        ((HomeActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();


                    }
                });
            }



        };
    }

    private void savePhoto() {
    }

    private void editPost(Post post) {
        SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("postId",post.getPostId());
        editor.apply();

        ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProductFragment()).commit();
    }

    public void addNotifications(final Post post,final User user,final String string){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Notifications notifications=new Notifications();
                notifications.setUser(user);
                notifications.setNotificationId(user.getuId()+post.getPostId());
                notifications.setPost(post);
                notifications.setText(/*user.getUser()+*/"liked "+post.getUser().getUser()+"'s post  ");

                FirebaseUtils.getNotificationsRef(notifications.getNotificationId()).setValue(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void onLikeClick(final Post post) {
        //check for error postUser
        FirebaseUtils.getPostLikedRef(post.getPostId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            //User liked
                            //check for getPostRef()
                            FirebaseUtils.getPostRef()
                                    .child(post.getPostId())
                                    .child(Constants.NUM_LIKES_KEY)
                                    .runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            long num = (long) mutableData.getValue();
                                            mutableData.setValue(num - 1);
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            FirebaseUtils.getPostLikedRef(post.getPostId())
                                                    .setValue(null);
                                        }
                                    });

                        //  Post post=new Post();

                            //Post.getUser and Post.getuId addition in parameters

                        } else {

                            FirebaseUtils.getPostRef()
                                    .child(post.getPostId())
                                    .child(Constants.NUM_LIKES_KEY)
                                    .runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {

                                            long num = (long) mutableData.getValue();

                                            mutableData.setValue(num + 1);
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            FirebaseUtils.getPostLikedRef(post.getPostId()).setValue(true);
                                        }

                                    });
                            FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user=dataSnapshot.getValue(User.class);
                                 //   addNotifications(post,user,"liked");

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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

    private void isDisliked(final String postId, final ImageView imageView, final ImageView imageViewl, final TextView likes, final Post post){

        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("DisLikes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.dislikec);
                    imageView.setTag("disliked");
                    imageViewl.setVisibility(View.GONE);

                    FirebaseDatabase.getInstance().getReference().child("Likes").child(firebaseUser.getUid()).removeValue();
                    nrLikes(likes,postId,post);

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

    /*private void nrComment(final TextView commentstotal, final Post post){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("comments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("post").equals(post)){
                    Constants.NUM_COMMENTS_KEY+1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    @Override
    public boolean onBackPressed() {

        // Logic here...

     //   ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        return true;
    }




    public static class PostHolder extends RecyclerView.ViewHolder {

        ImageView postOwnerDisplayImageView;
        ImageView postDisplayImageView;
        TextView postOwnerUsernameTextView;
        TextView postTimeCreatedTextView;
        TextView postTextTextView;
        LinearLayout postLikeLayout;
        LinearLayout postCommentLayout;
        TextView postNumLikesTextView;
        TextView postNumCommentTextView;
        TextView postProduct;
        TextView postPlace;
        TextView postPrice;
        TextView username_caption,dislikes;
        ImageView like,dislike,trash,options;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            options=(ImageView)itemView.findViewById(R.id.options);
            trash=(ImageView)itemView.findViewById(R.id.trash);
            dislike=(ImageView)itemView.findViewById(R.id.tv_dislike);
            dislikes=(TextView)itemView.findViewById(R.id.tv_dislikes);
            postOwnerDisplayImageView=(ImageView)itemView.findViewById(R.id.tv_post_owner_display);
            postOwnerUsernameTextView=(TextView)itemView.findViewById(R.id.tv_post_username);
            postDisplayImageView=(ImageView)itemView.findViewById(R.id.tv_post_display);
            postTimeCreatedTextView=(TextView)itemView.findViewById(R.id.post_time);
            postPlace=(TextView)itemView.findViewById(R.id.post_display_place);
            postProduct=(TextView)itemView.findViewById(R.id.post_display_product);
            postPrice=(TextView)itemView.findViewById(R.id.post_display_price);
            username_caption=(TextView)itemView.findViewById(R.id.caption_user);
            postTextTextView=(TextView)itemView.findViewById(R.id.tv_post_text);
            postLikeLayout=(LinearLayout)itemView.findViewById(R.id.like_layout);
            postNumLikesTextView=(TextView)itemView.findViewById(R.id.tv_likes);
            postCommentLayout=(LinearLayout)itemView.findViewById(R.id.comment_layout);
            postNumCommentTextView=(TextView)itemView.findViewById(R.id.tv_comments);
            like=(ImageView)itemView.findViewById(R.id.tv_like);

        }

            public void setUsername(String username){
                postOwnerUsernameTextView.setText(username);}

            public void setTime(CharSequence time){
                postTimeCreatedTextView.setText(time);}

            public void setNumLikes(String numLikes){
                postNumLikesTextView.setText(numLikes);}
            public void setPostText(String postText){
                postTextTextView.setText(postText);}
            public void setNumComments(String numComments){
                postNumCommentTextView.setText(numComments);}

            public void setPostProduct(String product){
            postProduct.setText(product);
            }
            public void setUsername_caption(String captionUser){
            username_caption.setText(captionUser);
            }

            public void setPostPlace(String place){
                postPlace.setText(place);
            }

            public void setPostPrice(String price){
            postPrice.setText(price);
            }

    }


}



