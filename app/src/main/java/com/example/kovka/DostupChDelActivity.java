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

public class DostupChDelActivity extends AppCompatActivity {
    private EditText id, class_work, prof, name, cod;
    private String id1, class_work1, prof1, name1, cod1;
    private String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dostup_ch_del);

        id = findViewById(R.id.id2);
        class_work = findViewById(R.id.class_work2);
        prof = findViewById(R.id.prof2);
        name = findViewById(R.id.name2);
        cod = findViewById(R.id.cod2);

        Intent intent = getIntent();
        adminCod = intent.getStringExtra("admin_cod");
        String id2 = intent.getStringExtra("id");
        String class_work2 = intent.getStringExtra("class_work");
        String prof2 = intent.getStringExtra("prof");
        String name2 = intent.getStringExtra("name");
        String cod2 = intent.getStringExtra("cod");

        id.setText(id2);
        class_work.setText(class_work2);
        prof.setText(prof2);
        name.setText(name2);
        cod.setText(cod2);
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
                id1 = id.getText().toString();
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
                    addDataToDatabase(id1, class_work1, prof1, name1, cod1);
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("cod", adminCod);
                    startActivity(intent);
                }
                return true;

            case R.id.del:
                getCourseDetails2(id.getText().toString());
                Intent intent = new Intent(this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("cod", adminCod);
                startActivity(intent);
                Toast toast = Toast.makeText(DostupChDelActivity.this, "Удалено", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String id1, String class_work1, String prof1, String name1, String cod1) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();
            return;
        }
        String url = Config.URL_CHANGE + "change_dostupApp.php";
        RequestQueue queue = Volley.newRequestQueue(DostupChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast toast = Toast.makeText(DostupChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DostupChDelActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
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
                params.put("id", id1);
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

    private void getCourseDetails2(String courseId) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();
            return;
        }
        String url = Config.URL_DELETE + "delete_dostupApp.php";
        RequestQueue queue = Volley.newRequestQueue(DostupChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DostupChDelActivity.this, "Fail to get course" + error, Toast.LENGTH_SHORT).show();
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
                params.put("id", courseId);
                return params;
            }
        };
        NetworkUtils.configureTimeout(request);
        queue.add(request);
    }
}