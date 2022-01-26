package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class reset extends AppCompatActivity {

    private EditText res_mail;
    private Button btn;
    FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mauth = FirebaseAuth.getInstance();
        fun();
    }

    public void fun(){
        btn = findViewById(R.id.but_res);
        res_mail = findViewById(R.id.res_mail);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPass();
            }
        });

    }
    public void resetPass(){
        String mail = res_mail.getText().toString();
        if(mail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            Toast.makeText(reset.this , "Invalid details" , Toast.LENGTH_SHORT).show();
        }
        else{
            mauth.sendPasswordResetEmail(mail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(reset.this , "Check your mail" , Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(reset.this , login.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(reset.this , "Invalid details" , Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}