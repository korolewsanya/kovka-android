package com.example.kovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DizActivity extends AppCompatActivity {
    private static final String JSON_URL = Config.API_BASE + "dizApp.php";
    ListView listView;
    ArrayList<JSONObject> infoList;
    String cod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diz);

        // Безопасное получение кода
        cod = getIntent().getStringExtra("cod");
        if (cod == null) {
            Toast.makeText(this, "Ошибка: код доступа не передан", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView textView = findViewById(R.id.tz);
        textView.setText("Тех.задание дизайнера");
        listView = findViewById(R.id.listView);
        loadJSONFromURL(JSON_URL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tz = infoList.get(position).optString("tz");
                String prof = infoList.get(position).optString("prof");
                String class_work = infoList.get(position).optString("class_work");
                String name = infoList.get(position).optString("name");

                Intent intent = new Intent(getApplicationContext(), OtDizSaveActivity.class);
                intent.putExtra("tz", tz);
                intent.putExtra("cod", cod);
                intent.putExtra("prof", prof);
                intent.putExtra("class_work", class_work);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
    }

    private void loadJSONFromURL(String url) {
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ListView.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray("tz");
                            ArrayList<JSONObject> listItems = getArrayListFromJSONArray(jsonArray);
                            infoList = listItems;
                            ListAdapter adapter = new TzAdapter(getApplicationContext(), R.layout.row_new_zakaz, R.id.nomer, listItems);
                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Ошибка соединения. Попробуйте позже.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cod", cod);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.diz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.ot:
                Intent intent = new Intent(this, OtDizActivity.class);
                intent.putExtra("cod", cod);
                addDataToDatabase(cod);
                startActivity(intent);
                return true;

            case R.id.zakaz:
                Intent intent2 = new Intent(this, ZakazForDizActivity.class);
                // ИЗМЕНЕНО: Передаем код доступа
                intent2.putExtra("cod", cod);
                startActivity(intent2);
                return true;

            case R.id.img:
                Intent intent3 = new Intent(this, UploadImgToServerActivity.class);
                // ИЗМЕНЕНО: Передаем код доступа (в UploadImgToServerActivity мы получаем его как "cod")
                intent3.putExtra("cod", cod);
                startActivity(intent3);
                return true;

            case R.id.zad:
                Intent intent4 = new Intent(this, ManagerZadActivity.class);
                // ИЗМЕНЕНО: Передаем код доступа
                intent4.putExtra("cod", cod);
                startActivity(intent4);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Метод для добавления в БД cod
    private void addDataToDatabase(String cod1) {
        String url = Config.API_BASE + "cod.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cod", cod1);
                return params;
            }
        };
        queue.add(request);
    }
}