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

public class ZpActivity extends AppCompatActivity {
    private static final String URL = Config.API_BASE + "zpApp.php";
    ListView listView;
    ArrayList<JSONObject> infoList;
    String adminCod; // ИЗМЕНЕНО: добавлена переменная для хранения кода админа

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zp);

        // ИЗМЕНЕНО: Получаем код из Intent
        adminCod = getIntent().getStringExtra("cod");

        listView = findViewById(R.id.listView);
        loadJSONFromURL(URL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nom = infoList.get(position).optString("id");
                String date = infoList.get(position).optString("date");
                String spec = infoList.get(position).optString("spec");
                String name = infoList.get(position).optString("name");
                String nachis = infoList.get(position).optString("nachis");
                String poluch = infoList.get(position).optString("poluch");
                Intent intent = new Intent(getApplicationContext(),ZpChDelActivity.class);

                intent.putExtra("id", nom);
                intent.putExtra("date", date);
                intent.putExtra("spec", spec);
                intent.putExtra("name", name);
                intent.putExtra("nachis", nachis);
                intent.putExtra("poluch", poluch);
                // ИЗМЕНЕНО: Передаем код админа в следующую активити
                intent.putExtra("admin_cod", adminCod);
                startActivity(intent);
            }
        });
    }

    private void  loadJSONFromURL(String url){
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ListView.VISIBLE);

        // ИЗМЕНЕНО: Сменили метод с GET на POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject object = new JSONObject(response);
                            // ИЗМЕНЕНО: Ключ теперь "zp" (сервер отдает его)
                            JSONArray jsonArray = object.getJSONArray("zp");
                            ArrayList<JSONObject> listItems = getArrayListFromJSONArray(jsonArray);
                            infoList = listItems;
                            ListAdapter adapter = new ZpAdapter(getApplicationContext(),R.layout.row_zp,R.id.id,listItems);
                            listView.setAdapter(adapter);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Ошибка соединения. Попробуйте позже.",Toast.LENGTH_SHORT).show();
                    }
                }) {
            // ИЗМЕНЕНО: Передаем "cod" в теле запроса
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cod", adminCod);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray){
        ArrayList<JSONObject> aList = new ArrayList<JSONObject>();
        try {
            if(jsonArray!= null){
                for(int i = 0; i<jsonArray.length();i++){
                    aList.add(jsonArray.getJSONObject(i));
                }
            }
        }catch (JSONException js){
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
        switch(id){
            case R.id.save_fin:
                Intent intent = new Intent(this, ZpSaveActivity.class);
                // ИЗМЕНЕНО: Передаем код админа для создания записи
                intent.putExtra("admin_cod", adminCod);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}