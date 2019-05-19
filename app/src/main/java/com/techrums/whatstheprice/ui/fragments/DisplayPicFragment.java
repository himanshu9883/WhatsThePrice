package com.techrums.whatstheprice.ui.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.techrums.whatstheprice.GlideApp;
import com.techrums.whatstheprice.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayPicFragment extends Fragment {

    private ImageView imageView,imageView2;
    private String postPic;
    public DisplayPicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.profile_pic,container,false);
        imageView=view.findViewById(R.id.dp);
        imageView2=view.findViewById(R.id.dp2);
        SharedPreferences prefs=getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postPic=prefs.getString("profilePic","none");
        GlideApp.with(getContext()).load(postPic).centerCrop().into(imageView);
        GlideApp.with(getContext()).load(postPic).centerCrop().into(imageView2);


        show();
        return view;
    }

    private void show() {
        GlideApp.with(getContext()).load(postPic).centerCrop().into(imageView);

    }

}
