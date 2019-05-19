package com.techrums.whatstheprice.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.adapters.ProductsAdapter;
import com.techrums.whatstheprice.adapters.UserAdapter;
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.ui.activities.Backable;
import com.techrums.whatstheprice.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements Backable {
    private RecyclerView recyclerView,recyclerViewProduct,recyclerViewtry;
    private List<User> mUsers;
    private List<Post> mPosts;
    private UserAdapter userAdapter;
    private ProductsAdapter productsAdapter;
    private ImageView users,products;

    EditText search_bar,search_bar_product;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        users=view.findViewById(R.id.users);
        products=view.findViewById(R.id.products);
        recyclerViewtry=view.findViewById(R.id.searchproduct_recyclerView);
        recyclerViewtry.setHasFixedSize(true);
        recyclerViewtry.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView=view.findViewById(R.id.search_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //Check or remove
        /*recyclerViewProduct=view.findViewById(R.id.search_recyclerView_product);
        recyclerViewProduct.setHasFixedSize(true);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(getContext()));
        search_bar_product=view.findViewById(R.id.search_bar_product);*/
        mPosts=new ArrayList<>();
        productsAdapter=new ProductsAdapter(getContext(),mPosts);
      //  recyclerViewProduct.setAdapter(productsAdapter);
        recyclerViewtry.setAdapter(productsAdapter);
        search_bar=view.findViewById(R.id.search_bar);
        mUsers=new ArrayList<>();
        userAdapter=new UserAdapter(getContext(),mUsers);
        recyclerView.setAdapter(userAdapter);

        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewtry.setVisibility(View.VISIBLE);
                search_bar.addTextChangedListener(new TextWatcher() {
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
                readProducts();

            }
        });
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewtry.setVisibility(View.GONE);

                search_bar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        searchUsers(s.toString().toLowerCase());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                readUsers();


            }
        });
        readProducts();

       /* search_bar_product.addTextChangedListener(new TextWatcher() {
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
        });*/

        readUsers();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString()/*.toLowerCase()*/);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    private void readProducts() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(search_bar.getText().toString().equals("")){
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


    private void searchUsers(String s){
        Query query= FirebaseDatabase.getInstance().getReference().child("users").orderByChild("user").
                startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren() ){
                    User user=snapshot.getValue(User.class);
                    mUsers.add(user);

                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void searchProducts(String s){
        Query query= FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("product").
                startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPosts.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren() ){
                    Post post=snapshot.getValue(Post.class);
                    mPosts.add(post);

                }
                productsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onBackPressed() {

        // Logic here...

       // ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        return true;
    }

    private void readUsers(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(search_bar.getText().toString().equals("")){
                    mUsers.clear();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        User user=snapshot.getValue(User.class);
                        mUsers.add(user);

                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
