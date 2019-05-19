package com.techrums.whatstheprice;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.techrums.whatstheprice.ui.activities.HomeActivity;
import com.techrums.whatstheprice.ui.activities.LoginActivity;
import com.techrums.whatstheprice.ui.activities.RegisterActivity;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button joinNow,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      //  setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinNow=(Button)findViewById(R.id.joinNow);
        login=(Button)findViewById(R.id.main_login_button);
        mAuth=FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUsertoLoginActivity();
            }
        });


        joinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

    }

    private void sendUsertoLoginActivity() {
        Intent loginintent=new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginintent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            sendUsertoHomeActivity();

        }

    }

    private void sendUsertoHomeActivity() {
        Intent homeintent=new Intent(MainActivity.this, HomeActivity.class);
        startActivity(homeintent);
        finish();
    }
}