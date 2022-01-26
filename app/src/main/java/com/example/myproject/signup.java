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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private  Button btn;
    private EditText email , pass , conf_pass , name;
    private TextView t;
    private String Nickname;

    private FirebaseFirestore db;
    private Map<String , Object> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(signup.this ,home.class);
            startActivity(intent);
        }

        btn = (Button) findViewById(R.id.button);
        email = (EditText) findViewById(R.id.editTextTextPersonName);
        pass = (EditText) findViewById(R.id.editTextTextPersonName3);
        conf_pass= (EditText)findViewById(R.id.Confirm_pass);
        name = (EditText)findViewById(R.id.Name);

        t = (TextView)findViewById(R.id.Exist);

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this , login.class));
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String pass1 = pass.getText().toString();
                String con_pass1 = conf_pass.getText().toString();
                Nickname = name.getText().toString();

                int flag = mail.indexOf('@');

                if(flag == -1){
                    Toast.makeText(signup.this , "Invalid mail id" , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(signup.this , signup.class));
                }

                if(!pass1.equals(con_pass1)){
                    Toast.makeText(signup.this , "Invalid password" , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(signup.this , signup.class));
                }
                createAccount(mail , pass1);
            }
        });

    }

    public void createAccount(String email , String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    FirebaseUser user = mAuth.getCurrentUser();

                    Intent intent = new Intent(signup.this , login.class);
                    intent.putExtra("Mail" , email);
                    intent.putExtra("Name",Nickname);
                    startActivity(intent);

                } else {

                    Toast.makeText(getApplicationContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

}