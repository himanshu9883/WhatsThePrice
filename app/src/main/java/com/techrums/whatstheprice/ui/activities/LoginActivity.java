package com.techrums.whatstheprice.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.techrums.whatstheprice.R;

public class LoginActivity extends AppCompatActivity {

    private Button Loginbutton;
    private EditText userEmail,userPassword;
    private TextView NeedNewAccountLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        NeedNewAccountLink=(TextView)findViewById(R.id.CreateAccountLink);
        Loginbutton=(Button)findViewById(R.id.loginButton);
        userEmail=(EditText) findViewById(R.id.emaillogin);
        userPassword=(EditText)findViewById(R.id.passwordlogin);
        mAuth=FirebaseAuth.getInstance();
        loadingbar=new ProgressDialog(this);



        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUsertoRegisterActivity();
            }
        });
        Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowingUsertoLogin();
            }
        });



    }

    private void sendUsertoRegisterActivity() {
        Intent registerinIntent=new Intent(LoginActivity.this, RegisterActivity.class);
        registerinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerinIntent);
    }


    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            currentUser.getUid();
            sendUsertoHomeActivity();

        }

    }

    private void AllowingUsertoLogin() {
        String email= userEmail.getText().toString();
        String password=userPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "please write your email", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"please write your password",Toast.LENGTH_SHORT);
        }
        else{
            loadingbar.setTitle("logging you in");
            loadingbar.setMessage("please wait while we are logging you in");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        sendUsertoHomeActivity();
                        Toast.makeText(LoginActivity.this, "You're logged in", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                    else {
                        String message=task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error occurred"+message, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                }
            });
        }
    }


    private void sendUsertoHomeActivity() {
        Intent mainIntent=new Intent(LoginActivity.this,HomeActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}