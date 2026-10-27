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

public class ZpChDelActivity extends AppCompatActivity {
    private EditText id, date, spec, name, nachis, poluch;
    private String id1, date1, spec1, name1, nachis1, poluch1;
    // ИЗМЕНЕНО: Добавлена переменная для кода админа
    String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zp_ch_del);

        id = findViewById(R.id.id2);
        date = findViewById(R.id.date2);
        spec = findViewById(R.id.spec2);
        name = findViewById(R.id.name2);
        nachis = findViewById(R.id.nachis2);
        poluch = findViewById(R.id.poluch2);

        Intent intent = getIntent();
        // ИЗМЕНЕНО: Получаем код админа
        adminCod = intent.getStringExtra("admin_cod");

        String id2 = intent.getStringExtra("id");
        String date2 = intent.getStringExtra("date");
        String spec2 = intent.getStringExtra("spec");
        String name2 = intent.getStringExtra("name");
        String nachis2 = intent.getStringExtra("nachis");
        String poluch2 = intent.getStringExtra("poluch");

        id.setText(id2);
        date.setText(date2);
        spec.setText(spec2);
        name.setText(name2);
        nachis.setText(nachis2);
        poluch.setText(poluch2);
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
                date1 = date.getText().toString();
                spec1 = spec.getText().toString();
                name1 = name.getText().toString();
                nachis1 = nachis.getText().toString();
                poluch1 = poluch.getText().toString();

                if (TextUtils.isEmpty(date1)) {
                    date.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(spec1)) {
                    spec.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(name1)) {
                    name.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(nachis1)) {
                    nachis.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(poluch1)) {
                    poluch.setError("Пожалуйста, заполител это поле");
                }
                else {
                    addDataToDatabase(id1, date1, spec1, name1, nachis1, poluch1);
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    // ИЗМЕНЕНО: Возвращаем код админа
                    intent.putExtra("cod", adminCod);
                    startActivity(intent);
                }
                return true;

            case R.id.del:
                getCourseDetails2(id.getText().toString());
                Intent intent = new Intent(this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // ИЗМЕНЕНО: Возвращаем код админа
                intent.putExtra("cod", adminCod);
                startActivity(intent);
                Toast toast = Toast.makeText(ZpChDelActivity.this, "Удалено", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String id1, String date1, String spec1, String name1, String nachis1, String poluch1) {
        String url = Config.URL_CHANGE + "change_zpApp.php";
        RequestQueue queue = Volley.newRequestQueue(ZpChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast toast = Toast.makeText(ZpChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG);
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
                        Toast.makeText(ZpChDelActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // ИЗМЕНЕНО: Добавлена передача admin_cod
                params.put("admin_cod", adminCod);
                params.put("id", id1);
                params.put("date", date1);
                params.put("spec", spec1);
                params.put("name", name1);
                params.put("nachis", nachis1);
                params.put("poluch", poluch1);
                return params;
            }
        };
        queue.add(request);
    }

    private void getCourseDetails2(String courseId) {
        String url = Config.URL_DELETE + "delete_zpApp.php";
        RequestQueue queue = Volley.newRequestQueue(ZpChDelActivity.this);

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
                        Toast.makeText(ZpChDelActivity.this, "Ошибка соединения", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // ИЗМЕНЕНО: Добавлена передача admin_cod
                params.put("admin_cod", adminCod);
                params.put("id", courseId);
                return params;
            }
        };
        queue.add(request);
    }
}