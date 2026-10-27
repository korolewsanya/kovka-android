package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class FinUprActivity extends AppCompatActivity {
    private static final String URL = Config.API_BASE + "finUprApp.php";
    ListView listView;
    ArrayList<JSONObject> infoList;
    private String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_upr);

        adminCod = getIntent().getStringExtra("cod");

        listView = findViewById(R.id.listView);
        loadJSONFromURL(URL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nom = infoList.get(position).optString("id");
                String date = infoList.get(position).optString("date");
                String dohod = infoList.get(position).optString("dohod");
                String rashod = infoList.get(position).optString("rashod");
                String prib = infoList.get(position).optString("prib");
                Intent intent = new Intent(getApplicationContext(), FinChDelActivity.class);
                intent.putExtra("nom", nom);
                intent.putExtra("date", date);
                intent.putExtra("dohod", dohod);
                intent.putExtra("rashod", rashod);
                intent.putExtra("prib", prib);
                intent.putExtra("admin_cod", adminCod);
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
                            JSONArray jsonArray = object.getJSONArray("fin");
                            ArrayList<JSONObject> listItems = getArrayListFromJSONArray(jsonArray);
                            infoList = listItems;
                            ListAdapter adapter = new FinAdapter(getApplicationContext(), R.layout.row_fin, R.id.date, listItems);
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
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cod", adminCod);
                return params;
            }
        };
        NetworkUtils.configureTimeout(stringRequest);
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
        getMenuInflater().inflate(R.menu.fin_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.save_fin:
                Intent intent = new Intent(this, FinSaveActivity.class);
                intent.putExtra("admin_cod", adminCod);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}