package com.example.shoppie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class editprofileactivity extends AppCompatActivity {


    public static final String TAG = "TAG";
    EditText changename,changeemail,changephone;
    ImageView profilePicture;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    Button msavebutton;
    FirebaseUser user;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofileactivity);



        Intent data = getIntent();
        final String Name = data.getStringExtra("Name");
        String Email = data.getStringExtra("Email");
        String Phone = data.getStringExtra("Phone");

        changename = findViewById(R.id.changename);
        changeemail = findViewById(R.id.changeemail);
        changephone = findViewById(R.id.changephone);
        profilePicture = findViewById(R.id.imageView);
        msavebutton = findViewById(R.id.savebutton);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = mStorageRef.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePicture);
            }
        });



        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);


            }
        });
        msavebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(changename.getText().toString().isEmpty() ||changeemail.getText().toString().isEmpty() || changephone.getText().toString().isEmpty()){
                    Toast.makeText(editprofileactivity.this,"One or more fields are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String Email = changeemail.getText().toString();
                user.updateEmail(Email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = fStore.collection("users").document(user.getUid());
                        Map<String,Object> edited = new HashMap<>();
                        edited.put("Email",Email);
                        edited.put("Name",changename.getText().toString());
                        edited.put("Phone",changephone.getText().toString());
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(editprofileactivity.this,"Profile Updated.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),profileactivity.class));
                                finish();
                            }
                        });


                        Toast.makeText(editprofileactivity.this,"Email is changed.", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(editprofileactivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });



        changename.setText(Name);
        changeemail.setText(Email);
        changephone.setText(Phone);


        Log.d(TAG, "onCreate "+ Name + " " + Email + " " + Phone);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);



            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to firebase storage
        final StorageReference fileRef = mStorageRef.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePicture);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image failed to upload.",Toast.LENGTH_SHORT).show();


            }
        });

    }
}