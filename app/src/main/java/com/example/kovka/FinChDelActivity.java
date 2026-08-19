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
    // создание строк для хранения наших значений из полей редактирования.
    private String nom1, date1, dohod1, rashod1, prib1;
    String nom2;

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

// Проверяем оба поля на пустоту
                if (TextUtils.isEmpty(rashodText)) {
                    rashod.setError("Введите расход");
                    return;
                }
                if (TextUtils.isEmpty(dohodText)) {
                    dohod.setError("Введите доход");
                    return;
                }
// Если поля не пустые - парсим и вычисляем
                int ras = Integer.parseInt(rashodText);
                int doh = Integer.parseInt(dohodText);
                int pribil = doh - ras;
                String pr = Integer.toString(pribil);
                prib.setText(pr);
            }
        });

        Intent intent = getIntent();
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

                // проверка текстовых полей, если они пусты или нет.
                if (TextUtils.isEmpty(date1)) {
                    date.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(dohod1)) {
                    dohod.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(rashod1)) {
                    rashod.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(prib1)) {
                    prib.setError("Пожалуйста, заполител это поле");
                }
                else {
                    // вызывающий метод для изменения данных
                    addDataToDatabase(nom1, date1, dohod1, rashod1, prib1);
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                return true;

            case R.id.del:
                getCourseDetails2(nom2);
                Intent intent = new Intent(this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Toast toast = Toast.makeText(FinChDelActivity.this, "Удалёно", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String nom1, String date1, String dohod1, String rashod1, String prib1) {

        // URL для размещения наших данных
        String url = Config.URL_CHANGE + "change_finApp.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(FinChDelActivity.this);

        // в строке ниже мы вызываем строку
        // метод запроса для отправки данных в наш API
        // здесь мы вызываем метод post.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", "RESPONSE IS " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //  показываем тост-сообщение об успехе.
                    Toast toast = Toast.makeText(FinChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(FinChDelActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                // поскольку мы передаем данные в виде закодированного URL
                // поэтому мы передаем тип содержимого ниже
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                // ниже строки мы создаем карту для хранения
                // наши значения в паре ключ-значение.
                Map<String, String> params = new HashMap<String, String>();

                // в нижней строке мы передаем наш
                // пара ключей и значений для наших параметров.
                params.put("id", nom1);
                params.put("date", date1);
                params.put("dohod", dohod1);
                params.put("rashod", rashod1);
                params.put("prib", prib1);

                // наконец мы возвращаем наши параметры.
                return params;
            }
        };
        // ниже строки, чтобы сделать
        //  запрос объекта json.
        queue.add(request);
    }
    private void getCourseDetails2(String courseId) {

        // URL для публикации наших данных
        String url = Config.URL_DELETE + "delete_finApp.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(FinChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // в строке ниже передается наш ответ на объект json.
                    JSONObject jsonObject = new JSONObject(response);
                    // в строке ниже мы проверяем, является ли ответ нулевым или нет.
                    if (jsonObject.getString("courseName") == null) {
                        // displaying a toast message if we get error
                        Toast.makeText(FinChDelActivity.this, "Please enter valid id.", Toast.LENGTH_SHORT).show();
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(FinChDelActivity.this, "Fail to get course" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                // ниже строки мы создаем карту для хранения наших значений в паре ключ-значение.
                Map<String, String> params = new HashMap<String, String>();

                // В строке ниже мы передаем пару ключей и значений нашим параметрам.
                params.put("id", courseId);

                // наконец-то мы возвращаем наши параметры.
                return params;
            }
        };
        // линия ниже - сделать
        // запрос объекта json.
        queue.add(request);
    }
}