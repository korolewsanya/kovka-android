package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class ZakazForDizActivity extends AppCompatActivity {
    private static final String JSON_URL = Config.API_BASE + "zakazApp.php";
    ListView listView;
    ArrayList<JSONObject> infoList;
    String userCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaz_for_diz);

        userCod = getIntent().getStringExtra("cod");

        listView = findViewById(R.id.listView);
        loadJSONFromURL(JSON_URL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idi = infoList.get(position).optString("Id");
                String date = infoList.get(position).optString("date");
                String izdelie = infoList.get(position).optString("izdelie");
                String image = infoList.get(position).optString("image");
                String dlina = infoList.get(position).optString("Dlina");
                String shirina = infoList.get(position).optString("Shirina");
                String visota = infoList.get(position).optString("Visota");
                String prise = infoList.get(position).optString("Prise");
                String proces = infoList.get(position).optString("Proces");
                String coment = infoList.get(position).optString("Coment");

                Intent intent = new Intent(getApplicationContext(), ZakazDetailActivity.class);
                intent.putExtra("idi", idi);
                intent.putExtra("date", date);
                intent.putExtra("izdelie", izdelie);
                intent.putExtra("image", image);
                intent.putExtra("dlina", dlina);
                intent.putExtra("shirina", shirina);
                intent.putExtra("visota", visota);
                intent.putExtra("prise", prise);
                intent.putExtra("proces", proces);
                intent.putExtra("coment", coment);
                startActivity(intent);

                changeImgToDatabase(image);
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
                            JSONArray jsonArray = object.getJSONArray("zakaz");
                            ArrayList<JSONObject> listItems = getArrayListFromJSONArray(jsonArray);
                            infoList = listItems;
                            ListAdapter adapter = new ZakazAdapter(getApplicationContext(), R.layout.row_new_zakaz, R.id.nomer, listItems);
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
                params.put("cod", userCod);
                return params;
            }
        };
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

    private void changeImgToDatabase(String img1) {
        String url = Config.API_BASE + "img_id.php";
        RequestQueue queue = Volley.newRequestQueue(ZakazForDizActivity.this);

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
                        Log.e("TAG", "Ошибка img_id: " + error);
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("admin_cod", userCod);
                params.put("img", img1);
                return params;
            }
        };
        queue.add(request);
    }
}