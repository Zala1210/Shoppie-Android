package com.example.shoppie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import static com.example.shoppie.ImagesActivity.EXTRA_NAME;
import static com.example.shoppie.ImagesActivity.EXTRA_PRICE;
import static com.example.shoppie.ImagesActivity.EXTRA_URL;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(EXTRA_URL);
        String price = intent.getStringExtra(EXTRA_PRICE);
        String shoeName = intent.getStringExtra(EXTRA_NAME);

        ImageView imageView = findViewById(R.id.image_view_upload);
        TextView  textViewPrice = findViewById(R.id.text_view_price);
        TextView textViewName = findViewById(R.id.text_view_name);

        Picasso.get().load(imageUrl).fit().centerInside().into(imageView);
        textViewPrice.setText(price);
        textViewName.setText(shoeName);


    }

    public void buy(View view) {
        Toast.makeText(this,"Shoe bought!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(),ImagesActivity.class));
    }
}