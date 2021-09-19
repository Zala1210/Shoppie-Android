package com.example.shoppie;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class additemactivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE =101 ;
    ImageView imageViewAdd;
    EditText inputImageName, inputImagePrice;
    TextView textViewProgress;
    ProgressBar progressBaritem;
    Button btnUpload;
    DatabaseReference Dataref;
    StorageReference Storageref;

    Uri imageUri;
    boolean isImageAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additemactivity);


        imageViewAdd = findViewById(R.id.imageViewAdd);
        inputImageName = findViewById(R.id.inputImageName);
        inputImagePrice = findViewById(R.id.inputImagePrice);
        textViewProgress = findViewById(R.id.textViewProgress);
        progressBaritem = findViewById(R.id.progressBaritem);
        btnUpload = findViewById(R.id.btnUpload);


        textViewProgress.setVisibility(View.GONE);
        progressBaritem.setVisibility(View.GONE);

        Dataref = FirebaseDatabase.getInstance().getReference().child("Shoe");
        Storageref = FirebaseStorage.getInstance().getReference().child("ImageUrl");


        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODE_IMAGE);

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ImageName = inputImageName.getText().toString();
                final String Price = inputImagePrice.getText().toString();

                if(isImageAdded!=false && ImageName != null && Price != null){
                    uploadImage(ImageName,Price);

                }

            }
        });

    }

    private void uploadImage(final String imageName,final String Price) {
        textViewProgress.setVisibility(View.VISIBLE);
        progressBaritem.setVisibility(View.VISIBLE);

        final String key= Dataref.push().getKey();
        Storageref.child(key+".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Storageref.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap= new HashMap();
                        hashMap.put("ShoeName", imageName);
                        hashMap.put("Price", Price);
                        hashMap.put("ImageUrl", uri.toString());
                        Dataref.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(),shopactivity.class));


                            }
                        });


                    }
                });




            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress= (taskSnapshot.getBytesTransferred()*100)/taskSnapshot.getTotalByteCount();
                progressBaritem.setProgress((int) progress);
                textViewProgress.setText(progress + " %");

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_IMAGE && data!=null){
            imageUri=data.getData();
            isImageAdded=true;
            imageViewAdd.setImageURI(imageUri);


        }
    }
}