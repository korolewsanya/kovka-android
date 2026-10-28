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

    // ИЗМЕНЕНО: Добавлена переменная для кода админа, который выдает ТЗ
    private String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_tz_activity);

        // ИЗМЕНЕНО: Получаем код админа из Intent
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
                        android.util.Log.d("SPINNER_RAW", "Ответ сервера: " + response); // <-- ДОБАВИТЬ ЭТО
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
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
                        android.util.Log.e("SPINNER_RAW", "Ошибка: " + error.toString()); // <-- ДОБАВИТЬ ЭТО
                        Toast.makeText(GiveTzActivity.this, "Ошибка загрузки списка сотрудников", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("admin_cod", adminCod);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getWorkers(JSONArray j) {
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

    private String getCod(int position) {
        String course = "";
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
        String item = (String) adapterView.getItemAtPosition(position);
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

    private void addDataToDatabase(String tz1, String data1, String cod1, String prof1, String class_work1, String name1) {
        String url = Config.URL_CREATE + "create_tz_workersApp.php";
        RequestQueue queue = Volley.newRequestQueue(GiveTzActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast toast = Toast.makeText(GiveTzActivity.this, "Тех.задание успешно отправлено", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(GiveTzActivity.this, AdminActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        // ИЗМЕНЕНО: Возвращаем код админа, чтобы не сломать цепочку
                        intent.putExtra("cod", adminCod);
                        startActivity(intent);
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
                // ИЗМЕНЕНО: Передаем admin_cod для авторизации на сервере
                params.put("admin_cod", adminCod);
                params.put("tz", tz1);
                params.put("date", data1);
                params.put("cod", cod1);
                params.put("prof", prof1);
                params.put("class_work", class_work1);
                params.put("name", name1);
                return params;
            }
        };
        queue.add(request);
    }

    public void onMyButtonClick(View view) {
        String tz1 = tz.getText().toString();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru")).format(Calendar.getInstance().getTime());

        addDataToDatabase(tz1, date, cod, prof, class_work, name);
    }
}