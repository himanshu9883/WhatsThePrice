package com.techrums.whatstheprice.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;

import com.techrums.whatstheprice.R;

public class OptionsDialog extends DialogFragment  {
    private ImageView save,profile_visit,edit;
    private View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        view=getActivity().getLayoutInflater().inflate(R.layout.options_dialog,null);
        save=(ImageView)view.findViewById(R.id.saveOption);
        profile_visit=(ImageView)view.findViewById(R.id.profileVisit);
        edit=(ImageView)view.findViewById(R.id.editOption);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPost();
            }
        });

        profile_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        builder.setView(view);

        return builder.create();
    }

    private void editPost() {

    }

    private void savePost() {

    }

}
