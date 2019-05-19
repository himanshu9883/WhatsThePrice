package com.techrums.whatstheprice.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.models.Post;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.utils.Constants;
import com.techrums.whatstheprice.utils.FirebaseUtils;


import static android.app.Activity.RESULT_OK;

public class PostCreateDialog extends DialogFragment implements View.OnClickListener {

    private static final int RC_PHOTO_PICKER=1;
    private static final int Gallery_Pick = 1;
    private Post mPost;
    private ProgressDialog mProgressDialog;
    private Uri mSelectedUri;
    private ImageView mPostDisplay;

    private View mRootView;
   /* private ImageView selectpic;
    private ImageView sendpic;*/


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        mPost=new Post();
        mProgressDialog=new ProgressDialog(getContext());
        mRootView=getActivity().getLayoutInflater().inflate(R.layout.create_post_dialog,null);
        mPostDisplay=(ImageView)mRootView.findViewById(R.id.post_dialog_display);

        mRootView.findViewById(R.id.post_dialog_send_imageView).setOnClickListener(this);
        mRootView.findViewById(R.id.post_dialog_select_imageView).setOnClickListener(this);


        builder.setView(mRootView);



        return builder.create();
    }

    @Override
    public void onClick(View view) {
        //  Intent intent=new Intent(Intent.A)
        switch (view.getId()){
            case R.id.post_dialog_send_imageView:
                sendPost();
                break;
            case R.id.post_dialog_select_imageView:
                selectImage();
                break;






        }
    }

    private void sendPost() {
        mProgressDialog.setMessage("Sending Post..");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();


        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getEmail().replace(".",","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TextView postDialogTextview=(TextView)mRootView.findViewById(R.id.post_dialog_editText);
                        TextView postDialogPrice=(TextView)mRootView.findViewById(R.id.post_dialog_price);

                        User user=dataSnapshot.getValue(User.class);
                        //final String postId= FirebaseUtils.getUid();
                        final String postId=(FirebaseAuth.getInstance().getCurrentUser().getUid()+mSelectedUri.getLastPathSegment());
                        String text=postDialogTextview.getText().toString();
                        if(TextUtils.isEmpty(text)){
                            Toast.makeText(getActivity(),"Write something about what you bought",Toast.LENGTH_SHORT).show();
                        }
                        String price=postDialogPrice.getText().toString();
                        if(TextUtils.isEmpty(price)){
                            Toast.makeText(getActivity(),"Write the price",Toast.LENGTH_SHORT).show();
                        }
                        TextView postDialogProduct=(TextView)mRootView.findViewById(R.id.post_dialog_product);
                        String product=postDialogProduct.getText().toString();
                        if(TextUtils.isEmpty(product)){
                            Toast.makeText(getActivity(),"What's your product?",Toast.LENGTH_SHORT).show();
                        }
                        TextView postDialogShoppingPlace=(TextView)mRootView.findViewById(R.id.post_dialog_place);
                        String shoppingPlace=postDialogShoppingPlace.getText().toString();
                        if(TextUtils.isEmpty(shoppingPlace)){
                            Toast.makeText(getActivity(),"Where did you buy it from?",Toast.LENGTH_SHORT).show();
                        }



                        mPost.setUser(user);
                        mPost.setNumComments(0);
                        mPost.setNumLikes(0);
                        mPost.setNumDislikes(0);
                        mPost.setTimeCreated(System.currentTimeMillis());
                        mPost.setPostId(postId);
                        mPost.setPostText(text);
                        mPost.setProduct(product);
                        mPost.setPrice(Long.valueOf(price));
                        mPost.setShoppingPlace(shoppingPlace);


                        if(mSelectedUri!=null){
                            FirebaseUtils.getImageRef()
                                    .child(mSelectedUri.getLastPathSegment())
                                    .putFile(mSelectedUri).addOnSuccessListener(getActivity(),new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String url= Constants.POST_IMAGES+"/"+mSelectedUri.getLastPathSegment();
                                    mPost.setPhotoImageUri(url);
                                  //  addToMyPostList(FirebaseUtils.getUid());
                                    addToMyPostList(postId);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        mProgressDialog.dismiss();

                    }
                });
    }

    private void addToMyPostList(String postId) {
        FirebaseUtils.getPostRef().child(postId)
                .setValue(mPost);
        FirebaseUtils.getMyPostRef().child(postId).setValue(true)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        dismiss();

                    }
                });

        FirebaseUtils.addToMyRecord(Constants.POST_KEY,postId);

    }

    private void selectImage() {

        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);

        // startActivityForResult(galleryIntent,RC_PHOTO_PICKER);

       /* Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);*/
        startActivityForResult(Intent.createChooser(galleryIntent,"Complete action using"),RC_PHOTO_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_PHOTO_PICKER){
            if(requestCode==RESULT_OK|data!=null){
                mSelectedUri=data.getData();
                mPostDisplay.setImageURI(mSelectedUri);
            }
        }
    }





}