package com.example.kovka;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SaveImgActivity extends AppCompatActivity {

    private EditText izdelieEditText;
    private EditText fileEditText;
    private String adminCod;

    private final String[] categories = {"mangal", "lavo4ki", "kozirek", "zabor", "vorota",
            "ogradki", "reshetki", "mebel", "melo4i"};
    private final String[] categoryNames = {"Мангалы", "Лавочки", "Козырьки", "Заборы", "Ворота",
            "Оградки", "Решётки", "Мебель", "Мелочи"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_img);

        adminCod = getIntent().getStringExtra("cod");

        izdelieEditText = findViewById(R.id.izdelie);
        fileEditText = findViewById(R.id.file);
    }

    public void save(android.view.View view) {
        String izdelie = izdelieEditText.getText().toString().trim();
        String fileName = fileEditText.getText().toString().trim();

        if (izdelie.isEmpty()) {
            izdelieEditText.setError("Введите название изделия");
            return;
        }
        if (fileName.isEmpty()) {
            fileEditText.setError("Введите имя файла");
            return;
        }

        showCategoryDialog(izdelie, fileName);
    }

    private void showCategoryDialog(String izdelie, String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите категорию");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryNames);

        builder.setAdapter(adapter, (dialog, which) -> {
            String selectedCategory = categories[which];
            saveToDatabase(izdelie, fileName, selectedCategory);
            dialog.dismiss();
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void saveToDatabase(String izdelie, String fileName, String tableName) {
        String url = Config.URL_CREATE + "create_izdelieApp.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(SaveImgActivity.this, "Сохранено в " + getCategoryName(tableName), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(this, AdminActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("cod", adminCod);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SaveImgActivity.this, "Ошибка: " + obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SaveImgActivity.this, "Ошибка ответа сервера", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(SaveImgActivity.this, "Ошибка соединения. Попробуйте позже.", Toast.LENGTH_SHORT).show()) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("admin_cod", adminCod); // <-- авторизация
                params.put("table", tableName);
                params.put("izdelie", izdelie);
                params.put("image", fileName);
                params.put("dlina", "");
                params.put("shirina", "");
                params.put("visota", "");
                params.put("prise", "0");
                return params;
            }
        };
        queue.add(request);
    }

    private String getCategoryName(String tableName) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(tableName)) {
                return categoryNames[i];
            }
        }
        return tableName;
    }
}