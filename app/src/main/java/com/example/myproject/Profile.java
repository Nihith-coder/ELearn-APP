package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Profile extends AppCompatActivity {

    private FirebaseAuth mauth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private FirebaseFirestore db;
    private Map<String ,Object> user;
    private String document;
    private EditText  firstname , usename , standard , dob , college , phone , phone_otp  ;
    private Button b_phone , b_otp , b_sub ,  b_add;
    private ImageView ib;
    private DatePicker dp;
    private Uri imageUri;
    String  mail;
    ProgressDialog progressDialog;


    String num , code_sys , code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mauth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        db = FirebaseFirestore.getInstance();

        user = new HashMap<>() ;

        ib = findViewById(R.id.image);
        b_phone = findViewById(R.id.verify_phone);
        b_otp = findViewById(R.id.verify_otp1);
        b_sub = findViewById(R.id.submit1);
        b_add = findViewById(R.id.add);

        firstname = findViewById(R.id.firstname);
        usename = findViewById(R.id.usename);
        dob = findViewById(R.id.Dob);
        standard = findViewById(R.id.standard);
        college = findViewById(R.id.college);
        phone = findViewById(R.id.phone1);
        phone_otp = findViewById(R.id.otp1);



        document = getIntent().getStringExtra("file");

        mail = mauth.getCurrentUser().getEmail();


        details();

    }

    public void details(){
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });

        b_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = phone.getText().toString().trim();
                if(num.isEmpty() || num.length() < 10){
                    Toast.makeText(Profile.this , "Invalid number" , Toast.LENGTH_SHORT).show();
                    phone.setText(null);
                } else {
                    phone_otp.setVisibility(View.VISIBLE);
                    b_otp.setVisibility(View.VISIBLE);
                    otpSend();
                }
            }
        });

        b_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = phone_otp.getText().toString().trim();
                if(code.isEmpty() || code.length() < 6){
                    Toast.makeText(getApplicationContext() , "Invalid otp" , Toast.LENGTH_SHORT).show();
                }
                else{
                    verifyCode(code);
                }
            }
        });


        b_sub.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                b_otp.setVisibility(View.INVISIBLE);
                phone_otp.setVisibility(View.INVISIBLE);

                user.put("First Name" , firstname.getText().toString());
                user.put("Use Name" ,usename.getText().toString());
                user.put("DOB" , dob.getText().toString());
                user.put("Standard", standard.getText().toString());
                user.put("College", college.getText().toString());
                user.put("Phone ", phone.getText().toString());

                db.collection(mail)
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(Profile.this , "Successfully updated" , Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this , "Failed to update" , Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        Button btn = (Button) findViewById(R.id.logout);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    //IMAGE PROCESS

    public void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            //ib.setImageURI(imageUri);
        }
        uploadImage();
    }

    public void uploadImage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();


        storageReference = FirebaseStorage.getInstance().getReference("images/"+ mail);


        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(Profile.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(Profile.this,"Failed to Upload",Toast.LENGTH_SHORT).show();
            }
        });
        try {
            Thread.sleep(1000);
            RetrieveImage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void RetrieveImage(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/"+mail);
        try{
            File localfile = File.createTempFile("tempfile" , ".jpg");
            storageReference.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            ib.setImageBitmap(bitmap);
                            Toast.makeText(Profile.this , "Successfully retreived" , Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Profile.this , "Failed to retrieve" , Toast.LENGTH_SHORT).show();
                }
            });

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //PHONE VALIDATION

    public void otpSend(){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mauth)
                .setPhoneNumber("+91"+num)       // Phone number to verify
                .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(Profile.this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            code_sys = s;
        }



        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String generate_code = phoneAuthCredential.getSmsCode();
            if(generate_code != null){
                verifyCode(generate_code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(Profile.this , e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    };
    private void verifyCode(String verification_code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code_sys , verification_code);

        signInUserByCredential(credential);
    }

    private void signInUserByCredential(PhoneAuthCredential credential){
        mauth.signInWithCredential(credential).addOnCompleteListener(Profile.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Profile.this , "Successfully verified" ,Toast.LENGTH_SHORT).show();
                    b_sub.setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(Profile.this , task.getException().getMessage() ,Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }

    //SIgnOut

    public void signOut(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this , ELearnApp.class));
            }
        });
    }
}