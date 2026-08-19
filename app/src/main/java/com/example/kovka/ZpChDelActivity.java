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

public class ZpChDelActivity extends AppCompatActivity {
    private EditText id, date, spec, name, nachis, poluch;
    // создание строк для хранения наших значений из полей редактирования.
    private String id1, date1, spec1, name1, nachis1, poluch1;

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

                // проверка текстовых полей, если они пусты или нет
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
                    // вызывающий метод для изменеения данных
                    addDataToDatabase(id1, date1, spec1, name1, nachis1, poluch1);
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                return true;

            case R.id.del:
                getCourseDetails2(id.getText().toString());
                Intent intent = new Intent(this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                //  показываем тост-сообщение об успехе.
                Toast toast = Toast.makeText(ZpChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String id1, String date1, String spec1, String name1, String nachis1, String poluch1) {

        // URL для размещения наших данных
        String url = Config.URL_CHANGE + "change_zpApp.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(ZpChDelActivity.this);

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
                    Toast toast = Toast.makeText(ZpChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG);
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
                Toast.makeText(ZpChDelActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
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
                params.put("id", id1);
                params.put("date", date1);
                params.put("spec", spec1);
                params.put("name", name1);
                params.put("nachis", nachis1);
                params.put("poluch", poluch1);

                // наконец мы возвращаем наши параметры.
                return params;
            }
        };
        // ниже строки, чтобы сделать
        //  запрос объекта json.
        queue.add(request);
    }

    private void getCourseDetails2(String courseId) {
        String url = Config.URL_DELETE + "delete_zpApp.php";

        RequestQueue queue = Volley.newRequestQueue(ZpChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // В строке ниже мы передаем наш ответ в объект JSON.
                    JSONObject jsonObject = new JSONObject(response);
                    // on below line we are checking if the response is null or not.
                    if (jsonObject.getString("courseName") == null) {
                        // В приведенной ниже строке мы проверяем, является ли ответ нулевым или нет.
                        Toast.makeText(ZpChDelActivity.this, "Пожалуйста, введите действительный идентификатор.", Toast.LENGTH_SHORT).show();
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // метод обработки ошибок.
                Toast.makeText(ZpChDelActivity.this, "Fail to get course" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                //поскольку мы передаем данные в виде закодированного URL-адреса
                // поэтому мы передаем тип контента ниже
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", courseId);

                return params;
            }
        };
        queue.add(request);
    }
}