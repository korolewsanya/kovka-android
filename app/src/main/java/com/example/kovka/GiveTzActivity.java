package com.example.kovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GiveTzActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener{
    //Объявление счетчика
    private Spinner spinner;

    //ArrayList для элементов Spinner
    private ArrayList<String> workers;

    //JSON Array
    private JSONArray result;

    EditText tz;
    private String cod, class_work, name, prof;
    TextView cd;
    TextView cw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_tz_activity);

        tz = (EditText)findViewById(R.id.tz);
        //Инициализация ArrayList
        workers = new ArrayList<String>();

        //Initializing Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        cd = (TextView) findViewById(R.id.cod);
        cw = (TextView) findViewById(R.id.class_work);

        //Добавление прослушивателя выбранного элемента в наш счетчик
        //Поскольку мы реализовали класс Spinner.OnItemSelectedListener для этого класса, мы передаем его в setOnItemSelectedListener
        spinner.setOnItemSelectedListener(this);

        //Этот метод будет извлекать данные из URL-адреса.
        getData();
    }
    private void getData(){
        //Creating a string request
        StringRequest stringRequest = new StringRequest(ConfigSpinner.DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                           // Анализ полученной строки Json в объект JSON
                            j = new JSONObject(response);

                            //Сохранение массива строк JSON в нашем массиве JSON
                            result = j.getJSONArray(ConfigSpinner.JSON_ARRAY);

                            getWorkers(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Создание очереди запросов
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Добавление запроса в очередь
        requestQueue.add(stringRequest);
    }

    private void getWorkers(JSONArray j){
        //Обход всех элементов массива json
        for(int i=0;i<j.length();i++){
            try {
                //Получение объекта JSON
                JSONObject json = j.getJSONObject(i);

                //Добавление имени студента в список массива
                workers.add(json.getString(ConfigSpinner.TAG_SPEC)+": "+json.getString(ConfigSpinner.TAG_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Настройка адаптера для отображения элементов в счетчике
        spinner.setAdapter(new ArrayAdapter<String>(GiveTzActivity.this, android.R.layout.simple_spinner_dropdown_item, workers));
    }

    private String getClass_work(int position){
        String course="";
        try {
            JSONObject json = result.getJSONObject(position);
            course = json.getString(ConfigSpinner.TAG_CLASS_WORK);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return course;
    }

    private String getCod(int position){
        String course="";
        try {
            JSONObject json = result.getJSONObject(position);
            course = json.getString(ConfigSpinner.TAG_COD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return course;
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        // Получаем выбранный объект
        String item = (String)adapterView.getItemAtPosition(position);
        String[] worker = item.split("\\s*(\\s|,|!|:)\\s*");
         prof = worker[0];
         name = worker[1];
         cd.setText(getCod(position));
         cw.setText(getClass_work(position));
         cod = cd.getText().toString();
         class_work = cw.getText().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        cd.setText("");
        cw.setText("");
    }

    //Метод для добавдения в БД Новые заказы
    private void addDataToDatabase(String tz1, String data1, String cod1, String prof1, String class_work1, String name1) {

        // URL для размещения наших данных
        String url = Config.URL_CREATE + "create_tz_workersApp.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(GiveTzActivity.this);

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
                    Toast toast = Toast.makeText(GiveTzActivity.this, "Тех.задание успешно отправлено", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(GiveTzActivity.this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(GiveTzActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
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
                params.put("tz", tz1);
                params.put("date", data1);
                params.put("cod", cod1);
                params.put("prof", prof1);
                params.put("class_work", class_work1);
                params.put("name", name1);

                // наконец мы возвращаем наши параметры.
                return params;
            }
        };
        // ниже строки, чтобы сделать
        //  запрос объекта json.
        queue.add(request);
    }

    public void onMyButtonClick(View view) {
         String tz1 = tz.getText().toString();
         String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale("ru")).format(Calendar.getInstance().getTime());

        addDataToDatabase(tz1, date, cod, prof, class_work, name);
    }
}