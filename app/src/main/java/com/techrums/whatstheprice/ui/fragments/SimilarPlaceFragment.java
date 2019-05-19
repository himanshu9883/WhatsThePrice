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

/**
 * A simple {@link Fragment} subclass.
 */
public class SimilarPlaceFragment extends Fragment {

    String place;
    RecyclerView similarRecycler;
    TextView placeset;
    ThingsAdapter thingsAdapter;
    List<Post> postList;
    public SimilarPlaceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_similar_place, container, false);
        placeset=(TextView)view.findViewById(R.id.placeset);
        similarRecycler=(RecyclerView)view.findViewById(R.id.similarRecyclerView1);
        SharedPreferences prefs=getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        place=prefs.getString("place","none");
        placeset.setText(place);
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
                    if(post.getShoppingPlace().equals(place)){
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
