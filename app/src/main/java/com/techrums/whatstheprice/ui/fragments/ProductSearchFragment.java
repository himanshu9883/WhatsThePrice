package com.techrums.whatstheprice.ui.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.adapters.ProductsAdapter;
import com.techrums.whatstheprice.models.Post;

import java.util.ArrayList;
import java.util.List;


public class ProductSearchFragment extends Fragment {
    private RecyclerView recyclerViewProduct;
    private List<Post> mPosts;
    private ProductsAdapter productsAdapter;
    EditText search_bar_product;



    public ProductSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_product_search, container, false);
        recyclerViewProduct=view.findViewById(R.id.search_recyclerView_product1);
        recyclerViewProduct.setHasFixedSize(true);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(getActivity()));
        search_bar_product=view.findViewById(R.id.search_bar_product1);
        mPosts=new ArrayList<>();
        productsAdapter=new ProductsAdapter(getActivity(),mPosts);
        recyclerViewProduct.setAdapter(productsAdapter);




        search_bar_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProducts(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    private void searchProducts(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("product").
                startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPosts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    mPosts.add(post);

                }
                productsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



        private void readProducts() {
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(search_bar_product.getText().toString().equals("")){
                        mPosts.clear();
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Post post=snapshot.getValue(Post.class);
                            mPosts.add(post);

                        }
                        productsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }



