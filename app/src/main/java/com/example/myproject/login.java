package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button btn ;
    EditText t1 , t2;
    TextView link_pass , link_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
        btn = (Button) findViewById(R.id.btn);
        t1 = (EditText) findViewById(R.id.email);
        t2 = (EditText) findViewById(R.id.password);

        link_pass = (TextView)findViewById(R.id.link_pass_res);
        link_signup = (TextView)findViewById(R.id.lin_signup);

        link_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this , signup.class));
            }
        });

        link_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this , reset.class));
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = t1.getText().toString();
                String pass = t2.getText().toString();

                int flag = mail.indexOf('@');

                if(flag == -1){
                    Toast.makeText(login.this , "Invalid mail id" , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(login.this , login.class));
                }
                signIn(mail ,pass);
            }
        });

    }

    public void signIn(String email , String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null)
                                startActivity(new Intent(login.this , home.class));
                        } else {
                            Toast.makeText(login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}