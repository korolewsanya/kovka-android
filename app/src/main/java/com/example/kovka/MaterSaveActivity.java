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

public class MaterSaveActivity extends AppCompatActivity {
    private EditText date, name, kup, izras, ost, prise, itogo;
    private String date1, name1, kup1, izras1, ost1, prise1, itogo1;
    String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru")).format(Calendar.getInstance().getTime());

    String manager;
    String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mater_save);

        manager = getIntent().getStringExtra("manager");
        adminCod = getIntent().getStringExtra("admin_cod");

        date = findViewById(R.id.date2);
        name = findViewById(R.id.name2);
        kup = findViewById(R.id.kup2);
        izras = findViewById(R.id.izras2);
        ost = findViewById(R.id.ost2);
        prise = findViewById(R.id.prise2);
        itogo = findViewById(R.id.itogo2);

        date.setText(today);
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
                date1 = date.getText().toString();
                name1 = name.getText().toString();
                kup1 = kup.getText().toString();
                izras1 = izras.getText().toString();
                ost1 = ost.getText().toString();
                prise1 = prise.getText().toString();
                itogo1 = itogo.getText().toString();

                if (TextUtils.isEmpty(date1)) { date.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(name1)) { name.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(kup1)) { kup.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(izras1)) { izras.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(ost1)) { ost.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(prise1)) { prise.setError("Пожалуйста, заполните это поле"); }
                else if (TextUtils.isEmpty(itogo1)) { itogo.setError("Пожалуйста, заполните это поле"); }
                else {
                    addDataToDatabase(date1, name1, kup1, izras1, ost1, prise1, itogo1);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String date1, String name1, String kup1, String izras1, String ost1, String prise1, String itogo1) {
        String url = Config.URL_CREATE + "create_materApp.php";
        RequestQueue queue = Volley.newRequestQueue(MaterSaveActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast toast = Toast.makeText(MaterSaveActivity.this, "В таблице Материалы создана новая запись", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (manager != null) {
                            Intent intent = new Intent(MaterSaveActivity.this, ManagerZadActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("cod", adminCod);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MaterSaveActivity.this, AdminActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("cod", adminCod);
                            startActivity(intent);
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MaterSaveActivity.this, "Не удалось получить ответ = " + error, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("admin_cod", adminCod); // <-- авторизация
                params.put("date", date1);
                params.put("name", name1);
                params.put("kup", kup1);
                params.put("izras", izras1);
                params.put("ost", ost1);
                params.put("prise", prise1);
                params.put("itogo", itogo1);
                return params;
            }
        };
        queue.add(request);
    }
}