package com.example.shoppie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.proto.TargetGlobal;

import java.util.HashMap;
import java.util.Map;

public class registeractivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mname,mphone,memail,mpassword;
    Button mregbutton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeractivity);

        mname = findViewById(R.id.name);
        mphone = findViewById(R.id.phone);
        memail = findViewById(R.id.email);
        mpassword = findViewById(R.id.password);
        mregbutton = findViewById(R.id.regbutton);

        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

       //If user is already logged in send him to home
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),homeactivity.class));
        }


        mregbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = memail.getText().toString().trim();
                final String password = mpassword.getText().toString().trim();
                final String name = mname.getText().toString();
                final String phone = mphone.getText().toString();


                if(TextUtils.isEmpty(email)){
                    memail.setError("Email is Required!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mpassword.setError("Password is Required!");
                    return;
                }
                if(password.length() < 6){
                    mpassword.setError("Password must be at least 6 characters!");
                }

                progressBar.setVisibility(View.VISIBLE);

                //register user to firebase
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            //send email verification link

                            FirebaseUser fuser = mAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(registeractivity.this,"Verification Email has been sent!",Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure: Email not sent " + e.getMessage());

                                }
                            });
                            Toast.makeText(registeractivity.this, "User created", Toast.LENGTH_SHORT).show();
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Name",name);
                            user.put("Phone",phone);
                            user.put("Email",email);
                            user.put("Password",password);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User Profile is created for "+ userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),loginactivity.class));
                        }else{
                            Toast.makeText(registeractivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}