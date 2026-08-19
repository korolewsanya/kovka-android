package com.example.kovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ImgChDelActivity extends AppCompatActivity {
    private EditText id, image, tags;
    private String idi1, image1, tags1, category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_ch_del);

        id = findViewById(R.id.id);
        image = findViewById(R.id.image);
        tags = findViewById(R.id.tags);

        Intent intent = getIntent();
        String idi2 = intent.getStringExtra("id");
        String image2 = intent.getStringExtra("path");
        String tags2 = intent.getStringExtra("tags");

        id.setText(idi2);
        image.setText(image2);
        tags.setText(tags2);
        category = intent.getStringExtra("category");

        ImageView imageView = findViewById(R.id.imageView2);
        Glide.with(this)
                .load(getIntent().getStringExtra("image"))
                .into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_zakaz_ch_del, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int nom = item.getItemId();

        switch (nom) {
            case R.id.change:
                idi1 = id.getText().toString();
                image1 = image.getText().toString();
                tags1 = tags.getText().toString();

                if (TextUtils.isEmpty(image1)) {
                    image.setError("Пожалуйста, заполните это поле");
                } else if (TextUtils.isEmpty(tags1)) {
                    tags.setError("Пожалуйста, заполните это поле");
                } else {
                    addDataToDatabase(idi1, image1, tags1, category);
                    // Переход на другую активность или всплывающее сообщение
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                return true;

            case R.id.del:
                deleteProduct(id.getText().toString(), category);
                Intent intent = new Intent(this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Toast toast = Toast.makeText(ImgChDelActivity.this, "Удалено", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String idi1, String image1, String tags1, String category) {
        String url = Config.URL_CHANGE + "change_imgApp.php";

        RequestQueue queue = Volley.newRequestQueue(ImgChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.e("TAG", "RESPONSE IS " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            Toast.makeText(ImgChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ImgChDelActivity.this, "Ошибка: " + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(ImgChDelActivity.this, "Ошибка сети: " + error, Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", idi1);
                params.put("image", image1);
                params.put("izdelie", tags1);   // здесь должно быть izdelie
                params.put("category", category);
                return params;
            }
        };
        queue.add(request);
    }
    private void deleteProduct(String productId, String category) {
        String url = Config.URL_DELETE + "delete_imgApp.php";

        RequestQueue queue = Volley.newRequestQueue(ImgChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.e("TAG", "DELETE RESPONSE: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            Toast.makeText(ImgChDelActivity.this,
                                    "Удалено: " + jsonObject.getString("message"),
                                    Toast.LENGTH_LONG).show();
                            // Переход после успешного удаления
                            Intent intent = new Intent(ImgChDelActivity.this, AdminActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ImgChDelActivity.this,
                                    "Ошибка: " + jsonObject.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(ImgChDelActivity.this,
                        "Ошибка сети: " + error, Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", productId);
                params.put("category", category);
                return params;
            }
        };
        queue.add(request);
    }
}