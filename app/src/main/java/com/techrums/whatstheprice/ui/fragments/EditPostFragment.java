package com.techrums.whatstheprice.ui.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.ui.activities.HomeActivity;
import com.techrums.whatstheprice.utils.FirebaseUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditPostFragment extends Fragment {

    private ImageView postImage,profileImage,trash;
    private TextView usernamme,time;
    private EditText Caption,ShopingPlace,Product,Price;
    private String postId;
    private FirebaseUser firebaseUser;
    private Button save;
    public EditPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_edit_post, container, false);
        trash=view.findViewById(R.id.trash_change);
        postImage=(ImageView)view.findViewById(R.id.tv_post_display_change);
        profileImage=(ImageView)view.findViewById(R.id.tv_post_owner_display_change);
        usernamme=(TextView)view.findViewById(R.id.tv_post_username_change);
        time=view.findViewById(R.id.post_time_change);
        Caption=view.findViewById(R.id.tv_post_text_change);
        save=view.findViewById(R.id.editPost);
        ShopingPlace=view.findViewById(R.id.post_display_place_change);
        Product=view.findViewById(R.id.post_display_product_change);
        Price=view.findViewById(R.id.post_display_price_change);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs=getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postId=prefs.getString("postId","none");

        showProduct();

        return view;
    }

    private void showProduct() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getActivity()==null){
                    return;
                }
                else {

                    final Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPhotoImageUri() != null) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference(post.getPhotoImageUri());

                        GlideApp.with(getActivity()).load(storageReference).fitCenter().into(postImage);
                    }
                    if(post.getUser().getPhotoUrl()!=null){
                        GlideApp.with(getActivity()).load(post.getUser().getPhotoUrl()).into(profileImage);

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

                    Caption.setText(post.getPostText().toUpperCase());
                    // time.setText(String.valueOf(post.getTimeCreated()));

                    usernamme.setText(post.getUser().getUser().toUpperCase());
                    ShopingPlace.setText(post.getShoppingPlace().toUpperCase());
                    Product.setText(String.valueOf(post.getProduct()).toUpperCase());
                    Price.setText(String.valueOf(post.getPrice()));
                    time.setText(DateUtils.getRelativeTimeSpanString(post.getTimeCreated()));
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editProduct(post);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeProduct(Post post) {
        FirebaseUtils.getPostRef().child(postId).setValue(null);
        FirebaseUtils.getMyPostRef().child(postId).setValue(null);
        ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

    }

    private void editProduct(Post post) {

        String place=ShopingPlace.getText().toString();
        String product=Product.getText().toString();
        String price=Price.getText().toString();
        Long pri=Long.valueOf(Price.getText().toString());
        String caption=Caption.getText().toString();
        post.setPostText(caption);
        post.setPrice(pri);
        post.setShoppingPlace(place);
        post.setProduct(product);

        FirebaseUtils.getPostRef().child(postId).setValue(post);

        ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
     /*   DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

}
