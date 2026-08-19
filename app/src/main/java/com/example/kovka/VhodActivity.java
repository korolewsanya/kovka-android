package com.example.kovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VhodActivity extends AppCompatActivity {
    private static final String JSON_URL = Config.API_BASE + "vhodApp.php";// UTF-8

    ArrayList<JSONObject> infoList;
    TextView textView;
    List<Person> productList;
    Person personAdmin;
    Person personDiz;
    Person personSvar;
    Person personMenedger;
    Person personColor;
    Person personCar;
    EditText editText;

    private String cod1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vhod);

        textView = (TextView) findViewById(R.id.tv);
        editText = (EditText) findViewById(R.id.etVhod);

        loadJSONFromURL(JSON_URL);
    }

    private void loadJSONFromURL(String url) {
        // ПРОВЕРКА 1: Если интернета нет - показываем сообщение и выходим из метода
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();
            return; // ПРЕРЫВАЕМ ВЫПОЛНЕНИЕ, НЕ ДЕЛАЕМ ЗАПРОС
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // ПРОВЕРКА 2: Конвертируем в UTF-8 для корректного отображения
                            String utf8Response = EncodingToUTF8(response);
                            if (utf8Response == null) {
                                Toast.makeText(VhodActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject object = new JSONObject(utf8Response);
                            JSONArray jsonArray = object.getJSONArray("dostup");
                            ArrayList<JSONObject> listItems = getArrayListFromJSONArray(jsonArray);
                            infoList = listItems;

                            // ПРОВЕРКА 3: Убеждаемся, что массив содержит достаточно элементов
                            if (infoList.size() >= 6) {
                                String admin = infoList.get(0).toString();
                                String diz = infoList.get(1).toString();
                                String svar = infoList.get(2).toString();
                                String menedger = infoList.get(3).toString();
                                String color = infoList.get(4).toString();
                                String car = infoList.get(5).toString();

                                Gson g = new Gson();
                                personAdmin = g.fromJson(admin, Person.class);
                                personDiz = g.fromJson(diz, Person.class);
                                personSvar = g.fromJson(svar, Person.class);
                                personMenedger = g.fromJson(menedger, Person.class);
                                personColor = g.fromJson(color, Person.class);
                                personCar = g.fromJson(car, Person.class);
                            } else {
                                Toast.makeText(VhodActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VhodActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // ПРОВЕРКА 4: Обработка ошибки через NetworkUtils (без изменения сообщения)
                        String errorMessage = NetworkUtils.getErrorMessage(error);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

        // ПРОВЕРКА 5: Настройка таймаута через NetworkUtils (чтобы запрос не висел вечно)
        NetworkUtils.configureTimeout(stringRequest);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray) {
        ArrayList<JSONObject> aList = new ArrayList<JSONObject>();
        try {
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    aList.add(jsonArray.getJSONObject(i));
                }
            }
        } catch (JSONException js) {
            js.printStackTrace();
        }
        return aList;
    }

    public static String EncodingToUTF8(String response) {
        try {
            byte[] code = response.toString().getBytes("ISO-8859-1");
            response = new String(code, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    public void vhod(View view) {
        // ПРОВЕРКА 6: Защита от NullPointerException - проверяем, загрузились ли данные
        if (personAdmin == null || personDiz == null || personSvar == null ||
                personMenedger == null || personColor == null || personCar == null) {
            textView.setText("Проверьте настройки соединения и перезапустите приложение");
            textView.setTextColor(Color.RED);
            return;
        }

        // ПРОВЕРКА 7: Защита от NullPointerException - проверяем cod на null
        String admin = personAdmin.cod != null ? personAdmin.cod : "";
        String admin2 = admin.trim();
        String diz = personDiz.cod != null ? personDiz.cod : "";
        String diz2 = diz.trim();
        String svar = personSvar.cod != null ? personSvar.cod : "";
        String svar2 = svar.trim();
        String menedger = personMenedger.cod != null ? personMenedger.cod : "";
        String menedger2 = menedger.trim();
        String color = personColor.cod != null ? personColor.cod : "";
        String color2 = color.trim();
        String car = personCar.cod != null ? personCar.cod : "";
        String car2 = car.trim();

        String et = editText.getText().toString();
        String et2 = et.trim();

        // ПРОВЕРКА 8: Проверка на пустой ввод
        if (et2.isEmpty()) {
            textView.setText("!!!   Введите код доступа   !!!");
            textView.setTextColor(Color.RED);
            return;
        }

        // ЛОГИКА ОСТАЛАСЬ ТОЙ ЖЕ, ТОЛЬКО ДОБАВЛЕНЫ ПРОВЕРКИ
        boolean accessGranted = false; // Флаг для отслеживания успешного входа

        if (new String(admin2).equals(et2)) {
            cod1 = editText.getText().toString();
            addDataToDatabase(cod1);
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra("cod", cod1);
            startActivity(intent);
            accessGranted = true;
        }

        if (!accessGranted && new String(diz2).equals(et2)) {
            cod1 = editText.getText().toString();
            addDataToDatabase(cod1);
            Intent intent = new Intent(this, DizActivity.class);
            intent.putExtra("cod", cod1);
            startActivity(intent);
            accessGranted = true;
        }

        if (!accessGranted && new String(svar2).equals(et2)) {
            cod1 = editText.getText().toString();
            addDataToDatabase(cod1);
            Intent intent = new Intent(this, OtherSpecialistsActivity.class);
            intent.putExtra("cod", cod1);
            startActivity(intent);
            accessGranted = true;
        }

        if (!accessGranted && new String(menedger2).equals(et2)) {
            cod1 = editText.getText().toString();
            addDataToDatabase(cod1);
            Intent intent = new Intent(this, MenedgerActivity.class);
            intent.putExtra("cod", cod1);
            startActivity(intent);
            accessGranted = true;
        }

        if (!accessGranted && new String(color2).equals(et2)) {
            cod1 = editText.getText().toString();
            addDataToDatabase(cod1);
            Intent intent = new Intent(this, OtherSpecialistsActivity.class);
            intent.putExtra("cod", cod1);
            startActivity(intent);
            accessGranted = true;
        }

        if (!accessGranted && new String(car2).equals(et2)) {
            cod1 = editText.getText().toString();
            addDataToDatabase(cod1);
            Intent intent = new Intent(this, OtherSpecialistsActivity.class);
            intent.putExtra("cod", cod1);
            startActivity(intent);
            accessGranted = true;
        }

        if (!accessGranted) {
            textView.setText("!!!   Код доступа введён не верно   !!!");
            textView.setTextColor(Color.RED);
        }
    }

    //Метод для добавдения в БД Новые заказы
    private void addDataToDatabase(String cod1) {
        // ПРОВЕРКА 9: Проверка интернета перед отправкой
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();
            return;
        }

        // URL для размещения наших данных
        String url = Config.API_BASE + "cod.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // ПРОВЕРКА 10: Логируем ошибку, но не показываем пользователю
                        // чтобы не смущать, так как вход уже выполнен
                        Log.e("VhodActivity", "Ошибка при сохранении кода: " + "Ошибка соединения. Попробуйте позже.");
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cod", cod1);
                return params;
            }
        };

        // ПРОВЕРКА 11: Настройка таймаута через NetworkUtils
        NetworkUtils.configureTimeout(request);

        queue.add(request);
    }
}