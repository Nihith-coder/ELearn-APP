package com.example.myproject;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class home extends AppCompatActivity implements OnNavigationItemSelectedListener{

    String[] Course = {"English", "Kannada", "Hindi", "Maths", "Science", "Social"};

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle Toggle;
    public ImageView imgeng, imgkan, imghin, imgmat, imgsci, imgsoc;
    public NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        navigationView = findViewById(R.id.nav_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Course);

        AutoCompleteTextView txt = findViewById(R.id.actv);
        txt.setAdapter(adapter);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);
        Toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(Toggle);
        Toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgeng = (ImageView) findViewById(R.id.eng);
        imgeng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadeng(new fragment_eng());
            }

            private void loadeng(fragment_eng fragment) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
            }
        });

        imgkan = (ImageView) findViewById(R.id.kan);
        imgkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadkan(new fragment_kan());
            }

            private void loadkan(fragment_kan fragment) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
            }

        });

        imghin = (ImageView) findViewById(R.id.hin);
        imghin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadhin(new fragment_hin());
            }

            private void loadhin(fragment_hin fragment) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
            }

        });

        imgmat = (ImageView) findViewById(R.id.mat);
        imgmat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadmat(new fragment_mat());
            }

            private void loadmat(fragment_mat fragment) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
            }

        });

        imgsci = (ImageView) findViewById(R.id.sci);
        imgsci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadsci(new fragment_sci());
            }

            private void loadsci(fragment_sci fragment) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
            }

        });


        imgsoc = (ImageView) findViewById(R.id.soc);
        imgsoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadsoc(new fragment_soc());
            }

            private void loadsoc(fragment_soc fragment) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
            }


        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(home.this, home.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(home.this , Profile.class));
                break;
            case R.id.nav_logout:
                signOut();
                startActivity(new Intent(home.this , ELearnApp.class));
                break;
            case R.id.nav_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey, download this app!");
                startActivity(shareIntent);
                break;
            case R.id.nav_abt:
                startActivity(new Intent(home.this , About_us.class));
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (Toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
    }

    public void signOut(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(home.this , ELearnApp.class));
            }
        });
    }
}