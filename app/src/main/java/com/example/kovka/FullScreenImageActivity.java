package com.example.kovka;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class FullScreenImageActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URL = "image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView photoView = findViewById(R.id.photoView);
        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(photoView);
        } else {
            Toast.makeText(this, "URL изображения не передан", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}