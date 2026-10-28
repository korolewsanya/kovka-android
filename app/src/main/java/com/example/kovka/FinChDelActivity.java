package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class FinChDelActivity extends AppCompatActivity {
    private EditText date, dohod, rashod, prib;
    private String nom1, date1, dohod1, rashod1, prib1;
    String nom2;
    private String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_ch_del);

        date = findViewById(R.id.date2);
        dohod = findViewById(R.id.dohod2);
        rashod = findViewById(R.id.rashod2);
        prib = findViewById(R.id.prib2);

        prib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rashodText = rashod.getText().toString();
                String dohodText = dohod.getText().toString();
                if (TextUtils.isEmpty(rashodText)) { rashod.setError("Введите расход"); return; }
                if (TextUtils.isEmpty(dohodText)) { dohod.setError("Введите доход"); return; }
                int ras = Integer.parseInt(rashodText);
                int doh = Integer.parseInt(dohodText);
                prib.setText(Integer.toString(doh - ras));
            }
        });

        Intent intent = getIntent();
        adminCod = intent.getStringExtra("admin_cod");
        nom2 = intent.getStringExtra("nom");
        String date2 = intent.getStringExtra("date");
        String dohod2 = intent.getStringExtra("dohod");
        String rashod2 = intent.getStringExtra("rashod");
        String prib2 = intent.getStringExtra("prib");

        date.setText(date2);
        dohod.setText(dohod2);
        rashod.setText(rashod2);
        prib.setText(prib2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_zakaz_ch_del, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.change:
                nom1 = nom2;
                date1 = date.getText().toString();
                dohod1 = dohod.getText().toString();
                rashod1 = rashod.getText().toString();
                prib1 = prib.getText().toString();

                if (TextUtils.isEmpty(date1)) { date.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(dohod1)) { dohod.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(rashod1)) { rashod.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(prib1)) { prib.setError("Пожалуйста, заполните это поле"); }
                else {
                    addDataToDatabase(nom1, date1, dohod1, rashod1, prib1);
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("cod", adminCod);
                    startActivity(intent);
                }
                return true;

            case R.id.del:
                getCourseDetails2(nom2);
                Intent intent = new Intent(this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("cod", adminCod);
                startActivity(intent);
                Toast toast = Toast.makeText(FinChDelActivity.this, "Удалено", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String nom1, String date1, String dohod1, String rashod1, String prib1) {
        String url = Config.URL_CHANGE + "change_finApp.php";
        RequestQueue queue = Volley.newRequestQueue(FinChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast toast = Toast.makeText(FinChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG);
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
                        Toast.makeText(FinChDelActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cod", adminCod);
                params.put("id", nom1);
                params.put("date", date1);
                params.put("dohod", dohod1);
                params.put("rashod", rashod1);
                params.put("prib", prib1);
                return params;
            }
        };
        NetworkUtils.configureTimeout(request);
        queue.add(request);
    }

    private void getCourseDetails2(String courseId) {
        String url = Config.URL_DELETE + "delete_finApp.php";
        RequestQueue queue = Volley.newRequestQueue(FinChDelActivity.this);

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
                        Toast.makeText(FinChDelActivity.this, "Fail to get course" + error, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cod", adminCod);
                params.put("id", courseId);
                return params;
            }
        };
        NetworkUtils.configureTimeout(request);
        queue.add(request);
    }
}