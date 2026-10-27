package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DostupSaveActivity extends AppCompatActivity {
    private EditText class_work, prof, name, cod;
    private String class_work1, prof1, name1, cod1;
    private String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dostup_save);

        adminCod = getIntent().getStringExtra("admin_cod");

        class_work = findViewById(R.id.class_work2);
        prof = findViewById(R.id.prof2);
        name = findViewById(R.id.name2);
        cod = findViewById(R.id.cod2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_zakaz_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sozd:
                class_work1 = class_work.getText().toString();
                prof1 = prof.getText().toString();
                name1 = name.getText().toString();
                cod1 = cod.getText().toString();

                if (TextUtils.isEmpty(class_work1)) {
                    class_work.setError("Пожалуйста, заполните это поле");
                } else if (TextUtils.isEmpty(prof1)) {
                    prof.setError("Пожалуйста, заполните это поле");
                } else if (TextUtils.isEmpty(name1)) {
                    name.setError("Пожалуйста, заполните это поле");
                } else if (TextUtils.isEmpty(cod1)) {
                    cod.setError("Пожалуйста, заполните это поле");
                } else {
                    addDataToDatabase(class_work1, prof1, name1, cod1);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String class_work1, String prof1, String name1, String cod1) {
        String url = Config.URL_CREATE + "create_dostupApp.php";
        RequestQueue queue = Volley.newRequestQueue(DostupSaveActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast toast = Toast.makeText(DostupSaveActivity.this, "Добавлено", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(DostupSaveActivity.this, AdminActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("cod", adminCod);
                        startActivity(intent);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DostupSaveActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("admin_cod", adminCod);
                params.put("class_work", class_work1);
                params.put("prof", prof1);
                params.put("name", name1);
                params.put("cod", cod1);
                return params;
            }
        };
        NetworkUtils.configureTimeout(request);
        queue.add(request);
    }
}