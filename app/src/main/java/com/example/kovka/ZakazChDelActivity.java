package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
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

public class ZakazChDelActivity extends AppCompatActivity {
    private EditText id, data, izdelie, image, dlina, shirina, visota, prise, pay, proces, name, tel, email, coment	;
    // создание строк для хранения наших значений из полей редактирования.
    private String idi1, data1, izdelie1, image1, dlina1, shirina1, visota1, prise1, pay1, proces1, name1, tel1, email1, coment1;
    String manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaz_ch_del);

        id = findViewById(R.id.id);
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

        Intent intent = getIntent();
        String idi2 = intent.getStringExtra("idi");
        String date2 = intent.getStringExtra("date");
        String izdelie2 = intent.getStringExtra("izdelie");
        String image2 = intent.getStringExtra("image");
        String dlina2 = intent.getStringExtra("dlina");
        String shirina2 = intent.getStringExtra("shirina");
        String visota2 = intent.getStringExtra("visota");
        String prise2 = intent.getStringExtra("prise");
        String pay2 = intent.getStringExtra("pay");
        String proces2 = intent.getStringExtra("proces");
        String name2 = intent.getStringExtra("name");
        String tel2 = intent.getStringExtra("tel");
        String email2 = intent.getStringExtra("email");
        String coment2 = intent.getStringExtra("coment");

         if(intent.getStringExtra("manager")!=null){
        manager = intent.getStringExtra("manager");
         }

        id.setText(idi2);
        data.setText(date2);
        izdelie.setText(izdelie2);
        image.setText(image2);
        dlina.setText(dlina2);
        shirina.setText(shirina2);
        visota.setText(visota2);
        prise.setText(prise2);
        pay.setText(pay2);
        proces.setText(proces2);
        name.setText(name2);
        tel.setText(tel2);
        email.setText(email2);
        coment.setText(coment2);

       //Задержка открытия браузера.
        //Необходимо для того чтобы успели обновится данные в DB img
        int DELAY = 1000;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                WebView browser = findViewById(R.id.webBrowser);
                browser.loadUrl(Config.API_BASE + "img.php");
            }
        }, DELAY);
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
                idi1 = id.getText().toString();
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

                // проверка текстовых полей, если они пусты или нет.
                if (TextUtils.isEmpty(izdelie1)) {
                    izdelie.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(prise1)) {
                    prise.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(pay1)) {
                    pay.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(tel1)) {
                    tel.setError("Пожалуйста, заполител это поле");
                } else if (TextUtils.isEmpty(data1)) {
                    data.setError("Пожалуйста, заполител это поле");
                } else if (email1.isEmpty()) {
                    email.setError("Пожалуйста, заполител это поле");
                }
                else {
                    // вызывающий метод для изменения данных
                    addDataToDatabase(idi1, data1, izdelie1, image1, dlina1, shirina1, visota1, prise1, pay1, proces1, name1, tel1, email1,coment1);
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
                Toast toast = Toast.makeText(ZakazChDelActivity.this, "Заказ удален", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String idi1, String data1, String izdelie1, String image1, String dlina1, String shirina1, String visota1, String prise1, String pay1, String proces1,String name1, String tel1, String email1, String coment1) {

        String url = Config.URL_CHANGE + "change_zakazApp.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(ZakazChDelActivity.this);

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
                    Toast toast = Toast.makeText(ZakazChDelActivity.this, "Изменения в заказе сохранены", Toast.LENGTH_LONG);
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
                Toast.makeText(ZakazChDelActivity.this, "Не удалось получить ответ = " + error, Toast.LENGTH_SHORT).show();
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
                params.put("id", idi1);
                params.put("date", data1);
                params.put("izdelie", izdelie1);
                params.put("image", image1);
                params.put("dlina", dlina1);
                params.put("shirina", shirina1);
                params.put("visota", visota1);
                params.put("prise", prise1);
                params.put("pay", pay1);
                params.put("proces", proces1);
                params.put("pay", pay1);
                params.put("name", name1);
                params.put("tel", tel1);
                params.put("email", email1);
                params.put("coment", coment1);

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
        String url = Config.URL_DELETE + "delete_zakazApp.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(ZakazChDelActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // в строке ниже передается наш ответ на объект json.
                    JSONObject jsonObject = new JSONObject(response);
                    // в строке ниже мы проверяем, является ли ответ нулевым или нет.
                    if (jsonObject.getString("courseName") == null) {
                        // отображение всплывающего сообщения, если мы получим ошибку
                        Toast.makeText(ZakazChDelActivity.this, " ", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ZakazChDelActivity.this, "Fail to get course" + error, Toast.LENGTH_SHORT).show();
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