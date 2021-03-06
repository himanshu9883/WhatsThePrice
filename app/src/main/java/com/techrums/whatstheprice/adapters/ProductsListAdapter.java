package com.techrums.whatstheprice.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.techrums.whatstheprice.GlideApp;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.ui.fragments.ProductFragment;

import java.util.List;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ViewHolder> {
    private Context mContext;
    private List<Post> mPosts;

    public ProductsListAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.products_item,viewGroup,false);
        return new ProductsListAdapter.ViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Post post=mPosts.get(i);

        viewHolder.product.setText(post.getProduct());
        viewHolder.shoppingPlace.setText(post.getShoppingPlace());
        viewHolder.price.setText(String.valueOf(post.getPrice()));
        StorageReference storageReference= FirebaseStorage.getInstance().getReference(post.getPhotoImageUri());
        GlideApp.with(mContext).load(storageReference).fitCenter().centerCrop().centerInside().into(viewHolder.foto);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postId",post.getPostId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProductFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView foto;
        public TextView shoppingPlace,price,product;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foto=(ImageView)itemView.findViewById(R.id.fotolist);
            price=(TextView)itemView.findViewById(R.id.pricelist);
            shoppingPlace=(TextView)itemView.findViewById(R.id.placelist);
            product=(TextView)itemView.findViewById(R.id.productlist);
        }
    }
}
