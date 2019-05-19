package com.techrums.whatstheprice.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.techrums.whatstheprice.ui.fragments.ProductFragment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser mUser;
    private String product;
    public PlaceAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.product_item,viewGroup,false);
        SharedPreferences prefs=mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        product=prefs.getString("postPlace",null);
        return new PlaceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        final Post post=mPosts.get(i);

        viewHolder.product.setText(post.getProduct());
        viewHolder.place.setText(post.getShoppingPlace());

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Likes").child(post.getPostId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewHolder.likes.setText(dataSnapshot.getChildrenCount()+"likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(post.getPhotoImageUri()!=null){
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(post.getPhotoImageUri());

            GlideApp.with(mContext).load(storageReference).fitCenter().into(viewHolder.imageProduct);


        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postId",post.getPostId());
                editor.apply();
                /*Intent intent=new Intent(mContext,ProductFragment.class);
                intent.putExtra("post",post);*/
                ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProductFragment()).commit();


            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView product,place;
        private CircleImageView imageProduct;
        private TextView likes;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product=itemView.findViewById(R.id.product_search);
            imageProduct=itemView.findViewById(R.id.image_product_search);
            likes=itemView.findViewById(R.id.user_like_search);
            place=itemView.findViewById(R.id.place_similar);
        }
    }
}
