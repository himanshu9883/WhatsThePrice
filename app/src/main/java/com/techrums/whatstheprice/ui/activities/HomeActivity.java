package com.techrums.whatstheprice.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.ui.fragments.HomeFragment;
import com.techrums.whatstheprice.ui.fragments.NotificationsFragment;
import com.techrums.whatstheprice.ui.fragments.ProfileFragment;
import com.techrums.whatstheprice.ui.fragments.SearchFragment;
import com.techrums.whatstheprice.ui.fragments.SearchOptionsFragment;
import com.techrums.whatstheprice.ui.fragments.SettingsFragment;
import com.techrums.whatstheprice.utils.BaseActivity;
import com.techrums.whatstheprice.utils.FirebaseUtils;


import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private ActionBar mToolbar;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mDatabase;
    String current_user_Id;
    private FirebaseUser mFirebaseUser;

    private ImageView mDisplayImageView;
    private TextView mNameTextView,mEmailTextView;
    private ValueEventListener mUserValueEventListener;
    private DatabaseReference mUserRef;

    private android.support.v7.app.ActionBar mtoolbar;


    private Button logoutTemp;
    // private BottomNavigationView bottomNavigationView;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference UserRef,postsRef;
    private CircleImageView NavProfileImage;
    private TextView profile_user_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         //       WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
      /*  Bundle intent=getIntent().getExtras();
        if(intent!=null){
            String publisher=intent.getString("publisherid");
            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",publisher);
            editor.apply();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

        }*/





       /*logoutTemp= findViewById(R.id.logouttemp);

        logoutTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                sendUserToLoginActivity();

            }
        });*/

        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        mAuth=FirebaseAuth.getInstance();

        final FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null) {
            current_user_Id = user.getUid();
        }
        else{
            sendUserToMainActivity();
        }


        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser()==null){
                    startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
                }
            }
        };
        init();
        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        loadFragment(new HomeFragment());

        mFirebaseUser=mAuth.getCurrentUser();
        mtoolbar=getSupportActionBar();
        bottomNavigationView= findViewById(R.id.bottom_navigation);
       /* bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.ic_home:
                        Intent intent = new Intent(HomeActivity.this, com.techrums.mycart.MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.ic_search:Toast.makeText(HomeActivity.this,"search",Toast.LENGTH_SHORT);
                        break;
                    case R.id.ic_add:Toast.makeText(HomeActivity.this,"search",Toast.LENGTH_SHORT);
                        break;
                    case R.id.ic_notifications:Toast.makeText(HomeActivity.this,"notification",Toast.LENGTH_SHORT);
                        break;
                    case R.id.ic_profile:sendUsertoProfileActivity();
                        break;


                }
                return false;
            }
        });*/

        // mtoolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        //setSupportActionBar(mtoolbar);
        //getSupportActionBar().setTitle("Home");




    }

    private void init() {
        if(mFirebaseUser!=null){
            mUserRef= FirebaseUtils.getUserRef(mFirebaseUser.getEmail().replace(".",","));

        }
    }

    private void sendUserToLoginActivity() {
        Intent loginintent=new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(loginintent);
        finish();
    }


    private void sendUserToMainActivity() {
        Intent mainintent=new Intent(HomeActivity.this,EditProfileActivity.class);
        startActivity(mainintent);
    }

   /* private void sendUsertoProfileActivity() {
        Intent profileintent=new Intent(HomeActivity.this,EditProfileActivity);
        startActivity(profileintent);
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        if(mUserRef!=null){
            mUserRef.addValueEventListener(mUserValueEventListener);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener!=null){
            mAuth.removeAuthStateListener(mAuthStateListener);
            if(mUserRef!=null){
                mUserRef.removeEventListener(mUserValueEventListener);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment=null;
        switch (menuItem.getItemId()){
            case R.id.ic_home:
                fragment=new HomeFragment();
                break;
            case R.id.ic_search:Toast.makeText(HomeActivity.this,"search",Toast.LENGTH_SHORT);
                fragment=new SearchFragment();
                break;
            /*case R.id.ic_add:Toast.makeText(HomeActivity.this,"search",Toast.LENGTH_SHORT);
            fragment=new SimilarProductFragment();

                break;*/
            case R.id.ic_notifications:Toast.makeText(HomeActivity.this,"notification",Toast.LENGTH_SHORT);
                fragment=new NotificationsFragment();

                break;
            case R.id.ic_profile:fragment=new SettingsFragment();
                break;
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment!=null){
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack("tag").commit();

            return true;
        }
        return false;
    }
    @Override
    public void onBackPressed(){

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
    }


   /* @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }*/
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_)
    }*/
}
