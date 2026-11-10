package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OtDizActivity extends AppCompatActivity {
    private static final String TAG = "OT_DIZ_DEBUG";
    private static final String URL = Config.API_BASE + "ot_dizApp.php";
    ListView listView;
    ArrayList<JSONObject> infoList;
    String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ot_diz);

        Log.e(TAG, "=== OtDizActivity onCreate ===");

        adminCod = getIntent().getStringExtra("cod");
        Log.e(TAG, "Получен cod = [" + adminCod + "]");

        if (adminCod == null) {
            Log.e(TAG, "ОШИБКА: cod = null");
            Toast.makeText(this, "Ошибка: код доступа не передан", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView textView = findViewById(R.id.tz);
        textView.setText("Отчёт");
        listView = findViewById(R.id.listView);
        loadJSONFromURL(URL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject item = infoList.get(position);
                String idi = item.optString("id");
                String tz = item.optString("tz");
                String ot = item.optString("otchet");
                String data = item.optString("date");
                String image = item.optString("image");

                Intent intent = new Intent(getApplicationContext(), OtDizChDelActivity.class);
                intent.putExtra("idi", idi);
                intent.putExtra("tz", tz);
                intent.putExtra("job", ot);
                intent.putExtra("data", data);
                intent.putExtra("image", image);
                intent.putExtra("cod", adminCod);
                startActivity(intent);
            }
        });
    }

    private void loadJSONFromURL(String url) {
        Log.e(TAG, "loadJSONFromURL: URL=" + url + ", cod=[" + adminCod + "]");

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ListView.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e(TAG, "!!! ОТВЕТ СЕРВЕРА: " + response);

                        try {
                            JSONObject object = new JSONObject(response);

                            if (object.has("error") && object.getBoolean("error")) {
                                String msg = object.optString("message", "Unknown error");
                                Log.e(TAG, "Сервер вернул ошибку: " + msg);
                                Toast.makeText(getApplicationContext(), "Ошибка сервера: " + msg, Toast.LENGTH_LONG).show();
                                return;
                            }

                            JSONArray jsonArray = object.getJSONArray("ot_diz");
                            Log.e(TAG, "Количество отчетов в JSON: " + jsonArray.length());

                            if (jsonArray.length() == 0) {
                                Log.e(TAG, "ВНИМАНИЕ: Массив отчетов пустой!");
                                Toast.makeText(getApplicationContext(), "Отчетов нет", Toast.LENGTH_SHORT).show();
                            }

                            ArrayList<JSONObject> listItems = getArrayListFromJSONArray(jsonArray);
                            infoList = listItems;

                            Log.e(TAG, "listItems размер: " + listItems.size());

                            if (listItems.isEmpty()) {
                                Log.e(TAG, "СПИСОК ПУСТ. Адаптер не устанавливаем.");
                                return;
                            }

                            ListAdapter adapter = new OtAdapter(getApplicationContext(), R.layout.row_new_zakaz, R.id.nomer, listItems);
                            listView.setAdapter(adapter);

                            Log.e(TAG, "Адаптер установлен. Количество элементов: " + adapter.getCount());

                        } catch (JSONException e) {
                            Log.e(TAG, "ОШИБКА РАЗБОРА JSON: " + e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Ошибка парсинга JSON", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e(TAG, "!!! ОШИБКА VOLLEY: " + error.toString());

                        if (error.networkResponse != null) {
                            Log.e(TAG, "HTTP код ошибки: " + error.networkResponse.statusCode);
                            try {
                                String body = new String(error.networkResponse.data, "UTF-8");
                                Log.e(TAG, "Тело ответа с ошибкой: " + body);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(getApplicationContext(), "Ошибка сети. Проверьте интернет.", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cod", adminCod);
                Log.e(TAG, "getParams: " + params.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray) {
        ArrayList<JSONObject> aList = new ArrayList<>();
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
}