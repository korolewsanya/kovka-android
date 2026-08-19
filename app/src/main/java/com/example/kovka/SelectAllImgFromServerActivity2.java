package com.example.kovka;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SelectAllImgFromServerActivity2 extends AppCompatActivity implements ImageAdapter.OnImageActionListener {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ImageAdapter adapter;
    private List<ImageModel> imageList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_all_img_from_server2);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageAdapter(imageList, this);
        recyclerView.setAdapter(adapter);

        apiService = ApiService.getInstance(this);

        loadImages();
    }

    private void loadImages() {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        apiService.getImages(new ApiService.ImageListCallback() {
            @Override
            public void onSuccess(List<ImageModel> images) {
                progressBar.setVisibility(ProgressBar.GONE);
                imageList.clear();
                imageList.addAll(images);
                adapter.notifyDataSetChanged();

                if (imageList.isEmpty()) {
                    Toast.makeText(SelectAllImgFromServerActivity2.this, "Нет изображений", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(SelectAllImgFromServerActivity2.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDelete(ImageModel image, int position) {
        // Пустой метод - удаление отключено
    }

    @Override
    public void onRename(ImageModel image, int position) {
        // Пустой метод - переименование отключено
    }
}