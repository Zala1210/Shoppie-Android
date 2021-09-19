package com.example.shoppie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class profileactivity extends AppCompatActivity {
    TextView pname,pemail,pphone;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String userId;
    ImageView profileImage;
    Button pprofpic;
    Button presetpass;
    FirebaseUser user;
    StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileactivity);
        pphone = findViewById(R.id.yourphone);
        pemail = findViewById(R.id.youremail);
        pname = findViewById(R.id.yourname);
        profileImage = findViewById(R.id.imageView);
        pprofpic = findViewById(R.id.profpic);
        presetpass = findViewById(R.id.resetpass);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = mStorageRef.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });


        userId = mAuth.getCurrentUser().getUid();
        user = mAuth.getCurrentUser();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                pphone.setText(documentSnapshot.getString("Phone"));
                pemail.setText(documentSnapshot.getString("Email"));
                pname.setText(documentSnapshot.getString("Name"));



            }
        });
//RESET PASSWORD
        presetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText resetPassword = new EditText(view.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter the new Password! Must be over 6 characters!");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //reset the password
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(profileactivity.this,"Password reset successfully",Toast.LENGTH_SHORT).show();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profileactivity.this,"Password reset failed" + e.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        }) ;
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //close the dialog if no is pressed!
                    }
                });
                passwordResetDialog.create().show();
            }
        });

//CHANGE PROFILE PICTURE
        pprofpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery
                //request code acts like id

                Intent i = new Intent(view.getContext(),editprofileactivity.class);

                i.putExtra("Name", pname.getText().toString());
                i.putExtra("Email",pemail.getText().toString());
                i.putExtra("Phone",pphone.getText().toString());


                startActivity(i);

                //Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(openGalleryIntent,1000);
            }
        });





    }




    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),loginactivity.class));
        finish();
    }
}