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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ZakazSaveActivity extends AppCompatActivity {
    private EditText data, izdelie, image, dlina, shirina, visota, prise, pay, proces, name, tel, email, coment;
    private String data1, izdelie1, image1, dlina1, shirina1, visota1, prise1, pay1, proces1, name1, tel1, email1, coment1;
    String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru")).format(Calendar.getInstance().getTime());
    String manager;
    String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaz_save);

        manager = getIntent().getStringExtra("manager");
        adminCod = getIntent().getStringExtra("admin_cod");

        data = findViewById(R.id.data);
        izdelie = findViewById(R.id.izdelie);
        image = findViewById(R.id.image);
        dlina = findViewById(R.id.dlina);
        shirina = findViewById(R.id.shirina);
        visota = findViewById(R.id.visota);
        prise = findViewById(R.id.prise);
        pay = findViewById(R.id.pay);
        proces = findViewById(R.id.proces);
        name = findViewById(R.id.name);
        tel = findViewById(R.id.tel);
        email = findViewById(R.id.email);
        coment = findViewById(R.id.coment);

        data.setText(today);
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
                data1 = data.getText().toString();
                izdelie1 = izdelie.getText().toString();
                image1 = image.getText().toString();
                dlina1 = dlina.getText().toString();
                shirina1 = shirina.getText().toString();
                visota1 = visota.getText().toString();
                prise1 = prise.getText().toString();
                pay1 = pay.getText().toString();
                proces1 = proces.getText().toString();
                name1 = name.getText().toString();
                tel1 = tel.getText().toString();
                email1 = email.getText().toString();
                coment1 = coment.getText().toString();

                if (TextUtils.isEmpty(data1)) {
                    data.setError("Пожалуйста, заполните это поле");
                } else if (TextUtils.isEmpty(izdelie1)) {
                    izdelie.setError("Пожалуйста, заполните это поле");
                } else if (TextUtils.isEmpty(prise1)) {
                    prise.setError("Пожалуйста, заполните это поле");
                } else if (TextUtils.isEmpty(pay1)) {
                    pay.setError("Пожалуйста, заполните это поле");
                } else if (TextUtils.isEmpty(tel1)) {
                    tel.setError("Пожалуйста, заполните это поле");
                } else if (TextUtils.isEmpty(email1)) {
                    email.setError("Пожалуйста, заполните это поле");
                } else {
                    addDataToDatabase(data1, izdelie1, image1, dlina1, shirina1, visota1, prise1, pay1, proces1, name1, tel1, email1, coment1);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String data1, String izdelie1, String image1, String dlina1, String shirina1, String visota1, String prise1, String pay1, String proces1, String name1, String tel1, String email1, String coment1) {
        String url = Config.URL_CREATE + "create_zakazApp.php";
        RequestQueue queue = Volley.newRequestQueue(ZakazSaveActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast toast = Toast.makeText(ZakazSaveActivity.this, "Заказ успешно создан", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (manager != null) {
                            Intent intent = new Intent(getApplicationContext(), ManagerZadActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("cod", adminCod);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("cod", adminCod);
                            startActivity(intent);
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ZakazSaveActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
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
                params.put("date", data1);
                params.put("izdelie", izdelie1);
                params.put("image", image1);
                params.put("dlina", dlina1);
                params.put("shirina", shirina1);
                params.put("visota", visota1);
                params.put("prise", prise1);
                params.put("pay", pay1);
                params.put("proces", proces1);
                params.put("name", name1);
                params.put("tel", tel1);
                params.put("email", email1);
                params.put("coment", coment1);
                return params;
            }
        };
        queue.add(request);
    }
}