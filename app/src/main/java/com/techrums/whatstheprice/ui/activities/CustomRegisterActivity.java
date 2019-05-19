package com.techrums.whatstheprice.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.techrums.whatstheprice.GlideApp;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.utils.Constants;
import com.techrums.whatstheprice.utils.FirebaseUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomRegisterActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER =1 ;
    private EditText emailreg,passwordreg,confirmPassword,username;
    private Button signUp;
    private FirebaseAuth mAuth;
    private String currentUserid;
    private ProgressDialog loadingbar;
    String email,password,confirmPasswordReg,Username;
    private CircleImageView profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_register);

        emailreg=(EditText)findViewById(R.id.emailCusreg);
        passwordreg=(EditText)findViewById(R.id.passwordCusReg);
        signUp=(Button)findViewById(R.id.customsignUp);
        confirmPassword=(EditText)findViewById(R.id.confirmpasswordRegcus);
        loadingbar=new ProgressDialog(this);
        username=(EditText)findViewById(R.id.emailCusname);
        profileImage=(CircleImageView)findViewById(R.id.customregprofilePic);
        mAuth=FirebaseAuth.getInstance();


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });


    }
    private void createNewAccount() {


        email=emailreg.getText().toString();
        password=passwordreg.getText().toString();
        confirmPasswordReg=confirmPassword.getText().toString();
        Username=username.getText().toString();

        if(TextUtils.isEmpty(Username)){
            Toast.makeText(CustomRegisterActivity.this,"Please enter your name ",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(email)){
            Toast.makeText(CustomRegisterActivity.this,"Please enter your email ",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(CustomRegisterActivity.this,"Please enter your password ",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPasswordReg)){
            Toast.makeText(CustomRegisterActivity.this,"Please confirm your password ",Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPasswordReg)){
            Toast.makeText(CustomRegisterActivity.this,"Password doesn't match  ",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingbar.setTitle("Creating Your Account");
            loadingbar.setMessage("please wait while we are creating your account");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            AllowAccessToUser();
        }


    }

    private void AllowAccessToUser() {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {



                    FirebaseUser user = mAuth.getCurrentUser();

                    final User mUser=new User();
                    mUser.setEmail(email);
                    //mUser(account.getDisplayName());
                    mUser.setuId(mAuth.getCurrentUser().getUid());

                    mUser.setUser(Username);
                    FirebaseUtils.getUserRef(email).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String image=dataSnapshot.child(mUser.getPhotoUrl()).getValue().toString();
                                Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImage) ;

                            }
                            else{
                                Toast.makeText(CustomRegisterActivity.this,"No profile pic",Toast.LENGTH_SHORT).show();
                        }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    // mUser.setPhotoUrl();



                    loadingbar.dismiss();

                } else {
                    String message=task.getException().getMessage().toString();

                    Toast.makeText(CustomRegisterActivity.this, "Authentication failed."+message,
                            Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();

                }
            }
        });
    }




    private void selectImage() {

        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);


        startActivityForResult(Intent.createChooser(galleryIntent,"Complete action using"),RC_PHOTO_PICKER);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
       // User user;
        if(requestCode==RC_PHOTO_PICKER&&resultCode==RESULT_OK&&data!=null){

            Uri ImageUri=data.getData();

        }
           /* if(requestCode==RESULT_OK){
                Uri resultUri=.getUri();
                StorageReference storageReference= FirebaseStorage.getInstance().getReference()
                        .child("profile Images").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpeg");
                storageReference.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(CustomRegisterActivity.this,"Loaded",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }*/


        }



}
