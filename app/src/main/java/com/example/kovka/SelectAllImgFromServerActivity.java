package com.example.kovka;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


public class SelectAllImgFromServerActivity extends AppCompatActivity implements ImageAdapter.OnImageActionListener{
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ImageAdapter adapter;
    private List<ImageModel> imageList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_all_img_from_server);

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
                    Toast.makeText(SelectAllImgFromServerActivity.this, "Нет изображений", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(SelectAllImgFromServerActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDelete(ImageModel image, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление")
                .setMessage("Удалить " + image.getName() + "?")
                .setPositiveButton("Да", (dialog, which) -> deleteImage(image, position))
                .setNegativeButton("Нет", null)
                .show();
    }

    private void deleteImage(ImageModel image, int position) {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        apiService.deleteImage(image.getName(), new ApiService.SimpleCallback() {
            @Override
            public void onSuccess(ApiResponse response) {
                progressBar.setVisibility(ProgressBar.GONE);
                if (response.isSuccess()) {
                    adapter.removeItem(position);
                    Toast.makeText(SelectAllImgFromServerActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SelectAllImgFromServerActivity.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(SelectAllImgFromServerActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRename(ImageModel image, int position) {
        showRenameDialog(image, position);
    }

    private void showRenameDialog(ImageModel image, int position) {
        EditText input = new EditText(this);
        input.setText(image.getName());

        new AlertDialog.Builder(this)
                .setTitle("Переименовать")
                .setView(input)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        renameImage(image, newName, position);
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void renameImage(ImageModel image, String newName, int position) {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        apiService.renameImage(image.getName(), newName, new ApiService.SimpleCallback() {
            @Override
            public void onSuccess(ApiResponse response) {
                progressBar.setVisibility(ProgressBar.GONE);
                if (response.isSuccess() && response.getNewName() != null) {
                    ImageModel updatedImage = new ImageModel(response.getNewName(),
                            "uploads/" + response.getNewName());
                    adapter.updateItem(position, updatedImage);
                    Toast.makeText(SelectAllImgFromServerActivity.this, "Переименовано", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SelectAllImgFromServerActivity.this, "Ошибка переименования", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(SelectAllImgFromServerActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}