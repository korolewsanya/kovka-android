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

public class WokersSaveActivity extends AppCompatActivity {
    private EditText spec, name, tel, email, adres, data, proch;
    private String spec1, name1, tel1, email1, adres1, data1, proch1;
    String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wokers_save);

        adminCod = getIntent().getStringExtra("admin_cod");

        spec = findViewById(R.id.spec);
        name = findViewById(R.id.name);
        tel = findViewById(R.id.tel);
        email = findViewById(R.id.email);
        adres = findViewById(R.id.adres);
        data = findViewById(R.id.data);
        proch = findViewById(R.id.proch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tz_sohranit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sohr:
                spec1 = spec.getText().toString();
                name1 = name.getText().toString();
                tel1 = tel.getText().toString();
                email1 = email.getText().toString();
                adres1 = adres.getText().toString();
                data1 = data.getText().toString();
                proch1 = proch.getText().toString();

                if (TextUtils.isEmpty(spec1)) { spec.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(name1)) { name.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(tel1)) { tel.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(email1)) { email.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(adres1)) { adres.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(data1)) { data.setError("Пожалуйста, заполните это поле"); }
                else {
                    addDataToDatabase(spec1, name1, tel1, email1, adres1, data1, proch1);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String spec1, String name1, String tel1, String email1, String adres1, String data1, String proch1) {
        String url = Config.URL_CREATE + "create_wokersApp.php";
        RequestQueue queue = Volley.newRequestQueue(WokersSaveActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast toast = Toast.makeText(WokersSaveActivity.this, "Специалист добавлен", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(WokersSaveActivity.this, AdminActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("cod", adminCod);
                        startActivity(intent);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WokersSaveActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
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
                params.put("spec", spec1);
                params.put("name", name1);
                params.put("tel", tel1);
                params.put("email", email1);
                params.put("adres", adres1);
                params.put("data", data1);
                params.put("proch", proch1);
                return params;
            }
        };
        queue.add(request);
    }
}