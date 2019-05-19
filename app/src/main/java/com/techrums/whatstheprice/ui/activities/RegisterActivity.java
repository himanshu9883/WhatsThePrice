package com.techrums.whatstheprice.ui.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.utils.BaseActivity;
import com.techrums.whatstheprice.utils.FirebaseUtils;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG ="1" ;
    private EditText emailreg,passwordreg,confirmPassword,username;
    private Button signUp,googleSignInButton;
    private FirebaseAuth mAuth;
    private String currentUserid;
    private ProgressDialog loadingbar;
    String email,password,confirmPasswordReg,Username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailreg=(EditText)findViewById(R.id.emailreg);
        passwordreg=(EditText)findViewById(R.id.passwordReg);
        signUp=(Button)findViewById(R.id.signUp);
        confirmPassword=(EditText)findViewById(R.id.confirmpasswordReg);
        loadingbar=new ProgressDialog(this);
        googleSignInButton=(Button)findViewById(R.id.button_google);
        username=(EditText)findViewById(R.id.emailname);

        mAuth=FirebaseAuth.getInstance();


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });




        googleSignInButton.setOnClickListener(this);
    }

    private void createNewAccountwithGoogle() {
        Intent signInIntent= Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);

      /*  Intent googleRegIntent=new Intent(RegisterActivity.this,HomeActivity.class);
        startActivity(googleRegIntent);*/
    }


    private void createNewAccount() {


        email=emailreg.getText().toString();
        password=passwordreg.getText().toString();
        confirmPasswordReg=confirmPassword.getText().toString();
        Username=username.getText().toString();

        if(TextUtils.isEmpty(Username)){
            Toast.makeText(RegisterActivity.this,"Please enter your name ",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this,"Please enter your email ",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this,"Please enter your password ",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPasswordReg)){
            Toast.makeText(RegisterActivity.this,"Please confirm your password ",Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPasswordReg)){
            Toast.makeText(RegisterActivity.this,"Password doesn't match  ",Toast.LENGTH_SHORT).show();
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

                    User mUser=new User();
                    mUser.setEmail(email);
                    //mUser(account.getDisplayName());
                  //  mUser.setPhotoUrl();
                    mUser.setuId(mAuth.getCurrentUser().getUid());

                    mUser.setUser(Username);

                    FirebaseUtils.getUserRef(email.replace(".",","))
                            .setValue(mUser, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    mFirebaseUser=mAuth.getCurrentUser();
                                    finish();
                                }
                            });
                    //mUser.setUser();
                    // sendUsertoEditProfileActivity();

                    loadingbar.dismiss();

                } else {
                    String message=task.getException().getMessage().toString();

                    Toast.makeText(RegisterActivity.this, "Authentication failed."+message,
                            Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            sendUsertoHomeActivity();

        }


    }



    private void sendUsertoEditProfileActivity() {
        Intent homeintent=new Intent(RegisterActivity.this,EditProfileActivity.class);


        homeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(homeintent);
        finish();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_google:
                createNewAccountwithGoogle();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==RC_SIGN_IN){
                GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if(result.isSuccess()){

                    GoogleSignInAccount account=result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                }
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                }
                else{
                    dismissProgressDialog();
                }
            }
            else{
                dismissProgressDialog();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    final User user=new User();

                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                    User puser=snapshot.getValue(User.class);

                                    if(puser.getEmail().equals(account.getEmail())) {
                                        user.setUser(puser.getUser());
                                        if (puser.getSex() != null) {
                                            user.setSex(puser.getSex());

                                        }
                                        if (puser.getBio() != null) {
                                            user.setBio(puser.getBio());

                                        }
                                        user.setEmail(puser.getEmail());
                                        user.setPhotoUrl(puser.getPhotoUrl());
                                        user.setuId(puser.getuId());
                                        FirebaseUtils.getUserRef(account.getEmail().replace(".", ","))
                                                .setValue(user, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        mFirebaseUser = mAuth.getCurrentUser();
                                                        finish();
                                                    }
                                                });

                                    }
                                    else{
                                        String photoUrl=null;
                                        if(account.getPhotoUrl()!=null){
                                            user.setPhotoUrl(account.getPhotoUrl().toString());
                                        }
                                        // user.setGender(person.getGender());
                                        //user.setBirthday(person.getBirthday());
                                        user.setEmail(account.getEmail());
                                        user.setUser(account.getDisplayName());
                                        user.setuId(mAuth.getCurrentUser().getUid());
                                        //account.getGrantedScopes()
                                        FirebaseUtils.getUserRef(account.getEmail().replace(".",","))
                                                .setValue(user, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        mFirebaseUser=mAuth.getCurrentUser();
                                                        finish();
                                                    }
                                                });
                                    }
                                }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    sendUsertoHomeActivity();
                }else{
                    dismissProgressDialog();
                }
            }
        });
    }
    private void sendUsertoHomeActivity() {
        Intent homeintent=new Intent(RegisterActivity.this,HomeActivity.class);


        homeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(homeintent);
        finish();

    }
}
