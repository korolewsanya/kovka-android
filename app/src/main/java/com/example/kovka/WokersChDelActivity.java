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
    // создание строк для хранения наших значений из полей редактирования.
    private String nom1, spec1, name1, tel1, email1, adres1, data1, proch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wokers_ch_del);

        nom = (EditText) findViewById(R.id.nom);
        spec = (EditText) findViewById(R.id.spec);
        name = (EditText) findViewById(R.id.name);
        tel = (EditText) findViewById(R.id.tel);
        email = (EditText) findViewById(R.id.email);
        adres = (EditText) findViewById(R.id.adres);
        data = (EditText) findViewById(R.id.data);
        proch = (EditText) findViewById(R.id.proch);

        Intent intent = getIntent();
        String idi = intent.getStringExtra("idi");
        String spec2 = intent.getStringExtra("spec");
        String name2 = intent.getStringExtra("name");
        String tel2 = intent.getStringExtra("tel");
        String email2 = intent.getStringExtra("email");
        String adres2 = intent.getStringExtra("adres");
        String data2 = intent.getStringExtra("data");
        String proch2 = intent.getStringExtra("proch");

        nom.setText(idi);
        spec.setText(spec2);
        name.setText(name2);
        tel.setText(tel2);
        email.setText(email2);
        adres.setText(adres2);
        data.setText(data2);
        proch.setText(proch2);
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

                // проверка текстовых полей, если они пусты или нет.
                if (TextUtils.isEmpty(data1)) {
                    data.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(spec1)) {
                    spec.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(nom1)) {
                    nom.setError("Пожалуйста, заполител это поле");
                }
                else if (TextUtils.isEmpty(name1)) {
                    name.setError("Пожалуйста, заполител это поле");
                }
                else if (TextUtils.isEmpty(tel1)) {
                    tel.setError("Пожалуйста, заполител это поле");
                }
                else if (TextUtils.isEmpty(email1)) {
                    email.setError("Пожалуйста, заполител это поле");
                }
                else if (TextUtils.isEmpty(adres1)) {
                    adres.setError("Пожалуйста, заполител это поле");
                }
                else {
                    // метод вызова для изменения данных.
                    addDataToDatabase(nom1, spec1, name1, tel1, email1, adres1, data1, proch1);
                    Intent intent = new Intent(this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
                startActivity(intent);
                Toast toast = Toast.makeText(WokersChDelActivity.this, "Удалёно", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String nom1, String spec1, String name1, String tel1, String email1, String adres1, String data1, String proch1) {

        // URL для размещения наших данных
        String url = Config.URL_CHANGE + "change_wokersApp.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(WokersChDelActivity.this);

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
                    Toast toast = Toast.makeText(WokersChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG);
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
                Toast.makeText(WokersChDelActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
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
                params.put("spec", spec1);
                params.put("name", name1);
                params.put("tel", tel1);
                params.put("email", email1);
                params.put("adres", adres1);
                params.put("date", data1);
                params.put("proch", proch1);

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
        String url = Config.URL_DELETE + "delete_wokersApp.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(WokersChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // в строке ниже передается наш ответ на объект json.
                    JSONObject jsonObject = new JSONObject(response);
                    // в строке ниже мы проверяем, является ли ответ нулевым или нет.
                    if (jsonObject.getString("courseName") == null) {
                        // отображение всплывающего сообщения, если мы получим ошибку
                        Toast.makeText(WokersChDelActivity.this, "Please enter valid id.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(WokersChDelActivity.this, "Fail to get course" + error, Toast.LENGTH_SHORT).show();
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

                //В строке ниже мы передаем пару ключей и значений нашим параметрам.
                params.put("id", courseId);

                // наконец-то мы возвращаем наши параметры
                return params;
            }
        };
        // линия ниже - сделать
        // запрос объекта json.
        queue.add(request);
    }
}