package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ZpSaveActivity extends AppCompatActivity {
    private EditText date, spec, name, nachis, poluch;
    // создание строк для хранения наших значений из полей редактирования.
    private String date1, spec1, name1, nachis1, poluch1;
    //Вставка времени и даты
    String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale("ru")).format(Calendar.getInstance().getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zp_save);

        date = findViewById(R.id.date2);
        spec = findViewById(R.id.spec2);
        name = findViewById(R.id.name2);
        nachis = findViewById(R.id.nachis2);
        poluch = findViewById(R.id.poluch2);

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

        switch(id){
            case R.id.sozd:
                date1 = date.getText().toString();
                spec1 = spec.getText().toString();
                name1 = name.getText().toString();
                nachis1 = nachis.getText().toString();
                poluch1 = poluch.getText().toString();

                // validating the text fields if empty or not.
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
                    // Вызов метода для добавления данных
                    addDataToDatabase(date1, spec1, name1, nachis1, poluch1);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Метод для добавдения в БД Новые заказы
    private void addDataToDatabase(String date1, String spec1, String name1, String nachis1, String poluch1) {

        // URL для размещения наших данных
        String url = Config.URL_CREATE + "create_zpApp.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(ZpSaveActivity.this);

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
                    Toast toast = Toast.makeText(ZpSaveActivity.this, "Добавлено", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ZpSaveActivity.this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(ZpSaveActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
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
}