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

public class WokersChDelActivity extends AppCompatActivity {
    private EditText nom, spec, name, tel, email, adres, data, proch;
    private String nom1, spec1, name1, tel1, email1, adres1, data1, proch1;
    String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wokers_ch_del);

        nom = findViewById(R.id.nom);
        spec = findViewById(R.id.spec);
        name = findViewById(R.id.name);
        tel = findViewById(R.id.tel);
        email = findViewById(R.id.email);
        adres = findViewById(R.id.adres);
        data = findViewById(R.id.data);
        proch = findViewById(R.id.proch);

        Intent intent = getIntent();
        adminCod = intent.getStringExtra("admin_cod");

        nom.setText(intent.getStringExtra("idi"));
        spec.setText(intent.getStringExtra("spec"));
        name.setText(intent.getStringExtra("name"));
        tel.setText(intent.getStringExtra("tel"));
        email.setText(intent.getStringExtra("email"));
        adres.setText(intent.getStringExtra("adres"));
        data.setText(intent.getStringExtra("data"));
        proch.setText(intent.getStringExtra("proch"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.zakaz_ch_del, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.change_z:
                nom1 = nom.getText().toString();
                spec1 = spec.getText().toString();
                name1 = name.getText().toString();
                tel1 = tel.getText().toString();
                email1 = email.getText().toString();
                adres1 = adres.getText().toString();
                data1 = data.getText().toString();
                proch1 = proch.getText().toString();

                if (TextUtils.isEmpty(data1)) { data.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(spec1)) { spec.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(nom1)) { nom.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(name1)) { name.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(tel1)) { tel.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(email1)) { email.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(adres1)) { adres.setError("Пожалуйста, заполните это поле"); }
                else {
                    addDataToDatabase(nom1, spec1, name1, tel1, email1, adres1, data1, proch1);
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("cod", adminCod);
                    startActivity(intent);
                    Toast toast = Toast.makeText(WokersChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                return true;

            case R.id.del_z:
                getCourseDetails2(nom.getText().toString());
                Intent intent = new Intent(this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("cod", adminCod);
                startActivity(intent);
                Toast toast = Toast.makeText(WokersChDelActivity.this, "Удалено", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String nom1, String spec1, String name1, String tel1, String email1, String adres1, String data1, String proch1) {
        String url = Config.URL_CHANGE + "change_wokersApp.php";
        RequestQueue queue = Volley.newRequestQueue(WokersChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast toast = Toast.makeText(WokersChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG);
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
                        Toast.makeText(WokersChDelActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
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
                params.put("id", nom1);
                params.put("spec", spec1);
                params.put("name", name1);
                params.put("tel", tel1);
                params.put("email", email1);
                params.put("adres", adres1);
                params.put("date", data1); // Сервер ждет "date"
                params.put("proch", proch1);
                return params;
            }
        };
        queue.add(request);
    }

    private void getCourseDetails2(String courseId) {
        String url = Config.URL_DELETE + "delete_wokersApp.php";
        RequestQueue queue = Volley.newRequestQueue(WokersChDelActivity.this);

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
                        Toast.makeText(WokersChDelActivity.this, "Ошибка соединения", Toast.LENGTH_SHORT).show();
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
        queue.add(request);
    }
}