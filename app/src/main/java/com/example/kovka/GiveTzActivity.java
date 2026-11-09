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

public class GiveTzActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {
    private Spinner spinner;
    private ArrayList<String> workers;
    private JSONArray result;

    EditText tz;
    private String cod, class_work, name, prof;
    TextView cd;
    TextView cw;

    private String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_tz_activity);

        adminCod = getIntent().getStringExtra("admin_cod");

        tz = findViewById(R.id.tz);
        workers = new ArrayList<>();

        spinner = findViewById(R.id.spinner);
        cd = findViewById(R.id.cod);
        cw = findViewById(R.id.class_work);

        spinner.setOnItemSelectedListener(this);

        getData();
    }

    private void getData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigSpinner.DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("SPINNER_RAW", "Ответ сервера: " + response);
                        try {
                            JSONObject j = new JSONObject(response);

                            // ИЗМЕНЕНО: если сервер вернул error — показываем сообщение,
                            // а не пытаемся распарсить отсутствующий массив "dostup"
                            if (j.optBoolean("error")) {
                                Toast.makeText(GiveTzActivity.this,
                                        j.optString("message", "Ошибка данных"),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            result = j.getJSONArray(ConfigSpinner.JSON_ARRAY);
                            getWorkers(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(GiveTzActivity.this, "Ошибка данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SPINNER_RAW", "Ошибка: " + error.toString());
                        Toast.makeText(GiveTzActivity.this, "Ошибка загрузки списка сотрудников", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("admin_cod", adminCod);
                Log.d("SPINNER_RAW", "Отправляю cod = [" + adminCod + "]");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getWorkers(JSONArray j) {
        workers.clear(); // ИЗМЕНЕНО: на случай повторной загрузки
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                workers.add(json.getString(ConfigSpinner.TAG_SPEC) + ": " + json.getString(ConfigSpinner.TAG_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spinner.setAdapter(new ArrayAdapter<String>(GiveTzActivity.this, android.R.layout.simple_spinner_dropdown_item, workers));
    }

    private String getClass_work(int position) {
        String course = "";
        try {
            JSONObject json = result.getJSONObject(position);
            course = json.getString(ConfigSpinner.TAG_CLASS_WORK);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return course;
    }

    // ИЗМЕНЕНО: Метод getCod заменен на getUserId, так как сервер больше не отдает коды (только хеши)
    private String getUserId(int position) {
        String userId = "";
        try {
            JSONObject json = result.getJSONObject(position);
            userId = json.getString(ConfigSpinner.TAG_ID); // Берем ID пользователя
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userId;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String item = (String) adapterView.getItemAtPosition(position);
        String[] worker = item.split("\\s*(\\s|,|!|:)\\s*");
        // ИЗМЕНЕНО: защита от неожиданного формата строки
        if (worker.length >= 2) {
            prof = worker[0];
            name = worker[1];
        } else if (worker.length == 1) {
            prof = worker[0];
            name = "";
        } else {
            prof = "";
            name = "";
        }

        cd.setText(getUserId(position));
        cw.setText(getClass_work(position));

        cod = cd.getText().toString(); // Здесь лежит ID (число), а не код доступа
        class_work = cw.getText().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        cd.setText("");
        cw.setText("");
    }

    private void addDataToDatabase(String tz1, String data1, String cod1, String prof1, String class_work1, String name1) {
        String url = Config.URL_CREATE + "create_tz_workersApp.php";
        RequestQueue queue = Volley.newRequestQueue(GiveTzActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        boolean success = false;
                        String message = "Ошибка отправки";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = !jsonObject.optBoolean("error", true);
                            message = jsonObject.optString("message",
                                    success ? "Тех.задание успешно отправлено" : "Ошибка отправки");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast toast = Toast.makeText(GiveTzActivity.this, message, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        // ИЗМЕНЕНО: возвращаемся только при успехе,
                        // иначе пользователь остаётся на форме, чтобы исправить данные.
                        if (success) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GiveTzActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
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
                params.put("tz", tz1);
                params.put("date", data1);
                params.put("cod", cod1); // Сюда уходит ID выбранного сотрудника
                params.put("prof", prof1);
                params.put("class_work", class_work1);
                params.put("name", name1);
                return params;
            }
        };
        queue.add(request);
    }

    public void onMyButtonClick(View view) {
        String tz1 = tz.getText().toString().trim();

        // ИЗМЕНЕНО: базовая валидация перед отправкой
        if (tz1.isEmpty()) {
            Toast.makeText(this, "Введите текст тех.задания", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cod == null || cod.isEmpty()) {
            Toast.makeText(this, "Выберите сотрудника", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru"))
                .format(Calendar.getInstance().getTime());

        addDataToDatabase(tz1, date, cod, prof, class_work, name);
    }
}