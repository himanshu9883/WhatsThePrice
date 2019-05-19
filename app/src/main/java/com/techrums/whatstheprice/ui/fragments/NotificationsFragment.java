package com.techrums.whatstheprice.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.adapters.NotificationAdapter;
import com.techrums.whatstheprice.models.Notifications;
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.ui.activities.Backable;
import com.techrums.whatstheprice.ui.activities.HomeActivity;
import com.techrums.whatstheprice.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;


public class NotificationsFragment extends Fragment implements Backable {
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    User user;
    private NotificationAdapter notificationAdapter;
    private List<Notifications> notificationsList;
    FirebaseUser firebaseUser;


    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View mRootView;

        mRootView=inflater.inflate(R.layout.fragment_notifications, container, false);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        mAuth=FirebaseAuth.getInstance();

        recyclerView=mRootView.findViewById(R.id.recycler_view_noti);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationsList=new ArrayList<>();
        notificationAdapter=new NotificationAdapter(getActivity(),notificationsList);
        recyclerView.setAdapter(notificationAdapter);

        readNotifications();

        return mRootView;
    }

    private void readNotifications() {
        user=new User();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifications");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationsList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    final Notifications notifications=snapshot.getValue(Notifications.class);

                    if(!(notifications.getUser().getuId().equals(firebaseUser.getUid()))){

                        notificationsList.add(notifications);


                     /*   DatabaseReference reference1=FirebaseDatabase.getInstance().getReference()
                               .child("Follow").child(notifications.getPost().getUser().getuId());
                        reference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.child("Followers").exists()) {
                                        if (dataSnapshot.child("Followers").child(firebaseUser.getUid()).getValue().equals(true)) {

                                        }

                                    }
                                }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/

                    }
                }


                Collections.reverse(notificationsList);
                notificationAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifications");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Notifications notifications=snapshot.getValue(Notifications.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onBackPressed() {

        // Logic here...

        ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        return true;
    }
}
