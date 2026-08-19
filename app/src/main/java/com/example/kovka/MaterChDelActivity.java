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


public class MaterChDelActivity extends AppCompatActivity {
    private EditText id, date, name, kup, izras, ost, prise, itogo;
    // создание строк для хранения наших значений из полей редактирования.
    private String id1, date1, name1, kup1, izras1, ost1, prise1, itogo1;;
    //Вставка времени и даты
    String manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mater_ch_del);

        id = findViewById(R.id.id2);
        date = findViewById(R.id.date2);
        name = findViewById(R.id.name2);
        kup = findViewById(R.id.kup2);
        izras = findViewById(R.id.izras2);
        ost = findViewById(R.id.ost2);
        prise = findViewById(R.id.prise2);
        itogo = findViewById(R.id.itogo2);

        Intent intent = getIntent();
        String id2 = intent.getStringExtra("id");
        String date2 = intent.getStringExtra("date");
        String name2 = intent.getStringExtra("name");
        String kup2 = intent.getStringExtra("kup");
        String izras2 = intent.getStringExtra("izras");
        String ost2 = intent.getStringExtra("ost");
        String prise2 = intent.getStringExtra("prise");
        String itogo2 = intent.getStringExtra("itogo");

        if(intent.getStringExtra("manager")!=null){
            manager = intent.getStringExtra("manager");
        }

        id.setText(id2);
        date.setText(date2);
        name.setText(name2);
        kup.setText(kup2);
        izras.setText(izras2);
        ost.setText(ost2);
        prise.setText(prise2);
        itogo.setText(itogo2);
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
                name1 = name.getText().toString();
                kup1 = kup.getText().toString();
                izras1 = izras.getText().toString();
                ost1 = ost.getText().toString();
                prise1 = prise.getText().toString();
                itogo1 = itogo.getText().toString();

                // проверка текстовых полей, если они пусты или нет.
                if (TextUtils.isEmpty(date1)) {
                    date.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(name1)) {
                    name.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(kup1)) {
                    kup.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(izras1)) {
                    izras.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(ost1)) {
                    ost.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(prise1)) {
                    prise.setError("Пожалуйста, заполител это поле");
                } else if (itogo1.isEmpty()) {
                    itogo.setError("Пожалуйста, заполител это поле");
                }
                else {
                    addDataToDatabase(id1, date1, name1, kup1, izras1, ost1, prise1, itogo1);
                    if(manager!=null) {
                        Intent intent = new Intent(this, ManagerZadActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(this, AdminActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                }
                return true;

            case R.id.del:
                getCourseDetails2(id.getText().toString());
                if(manager!=null) {
                    Intent intent = new Intent(this, ManagerZadActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                Toast toast = Toast.makeText(MaterChDelActivity.this, "Удалёно", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String id1, String date1, String name1, String kup1, String izras1, String ost1, String prise1, String itogo1) {

        // URL для размещения наших данных
        String url = Config.URL_CHANGE + "change_materApp.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(MaterChDelActivity.this);

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
                    Toast toast = Toast.makeText(MaterChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG);
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
                Toast.makeText(MaterChDelActivity.this, "Не удалось получить ответ = " + error, Toast.LENGTH_SHORT).show();
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
                params.put("name", name1);
                params.put("kup", kup1);
                params.put("izras", izras1);
                params.put("ost", ost1);
                params.put("prise", prise1);
                params.put("itogo", itogo1);

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
        String url = Config.URL_DELETE + "delete_materApp.php";

        //создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(MaterChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // в строке ниже передается наш ответ на объект json
                    JSONObject jsonObject = new JSONObject(response);
                    // on below line we are checking if the response is null or not.
                    if (jsonObject.getString("courseName") == null) {
                        // отображение всплывающего сообщения, если мы получим ошибку
                        Toast.makeText(MaterChDelActivity.this, "Please enter valid id.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MaterChDelActivity.this, "Не удалось получить ответ" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
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