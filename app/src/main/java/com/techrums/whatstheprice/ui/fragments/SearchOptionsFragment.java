package com.techrums.whatstheprice.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.ui.activities.HomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchOptionsFragment extends Fragment {

private TextView users,products;
    public SearchOptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search_options, container, false);
        users=view.findViewById(R.id.members_option);
        products=view.findViewById(R.id.products_option);
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).getFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProductSearchFragment()).commit();

            }
        });

        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SearchFragment()).commit();

            }
        });


        return view;
    }

}
