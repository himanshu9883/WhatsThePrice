package com.techrums.whatstheprice.ui.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.adapters.ThingsAdapter;
import com.techrums.whatstheprice.models.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SimilarProductFragment extends Fragment {

    String product;
    TextView productset;
    RecyclerView similarRecycler;
    ThingsAdapter thingsAdapter;
    List<Post> postList;

    public SimilarProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_similar_product, container, false);
        // Inflate the layout for this fragment
        productset=(TextView)view.findViewById(R.id.productset);
        similarRecycler=(RecyclerView)view.findViewById(R.id.similarRecyclerView);
        SharedPreferences prefs=getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        product=prefs.getString("product","none");
        productset.setText(product);
        postList=new ArrayList<>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext()) ;
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        similarRecycler.setLayoutManager(linearLayoutManager);
        similarRecycler.setHasFixedSize(true);
        similarRecycler.setVisibility(View.VISIBLE);
        thingsAdapter=new ThingsAdapter(getActivity(),postList);
        similarRecycler.setAdapter(thingsAdapter);
        showProducts();
        return view;
    }

    private void showProducts() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    if(post.getProduct().equals(product)){
                        postList.add(post);
                    }
                    Collections.reverse(postList);
                    thingsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
