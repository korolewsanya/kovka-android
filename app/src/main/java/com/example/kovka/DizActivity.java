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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DizActivity extends AppCompatActivity {
    private static final String JSON_URL = Config.API_BASE + "dizApp.php";// UTF-8
    ListView listView;
    ArrayList<JSONObject> infoList;
    String cod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diz);

        Bundle arguments = getIntent().getExtras();
        cod = arguments.get("cod").toString();

        TextView textView = findViewById(R.id.tz);
        textView.setText("Тех.задание дизайнера");
        listView = (ListView) findViewById(R.id.listView);
        loadJSONFromURL(JSON_URL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                infoList.get(position).optString("id");
                String tz = infoList.get(position).optString("tz");
                infoList.get(position).optString("date");
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

    private void  loadJSONFromURL(String url){
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ListView.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray("tz");
                            ArrayList<JSONObject> listItems = getArrayListFromJSONArray(jsonArray);
                            infoList = listItems;
                            ListAdapter adapter = new TzAdapter(getApplicationContext(),R.layout.row_new_zakaz,R.id.nomer,listItems);
                            listView.setAdapter(adapter);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Ошибка соединения. Попробуйте позже.",Toast.LENGTH_SHORT).show();
                    }
                });
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

    public  static  String EncodingToUTF8(String response){
        try {
            byte[] code = response.toString().getBytes("ISO-8859-1");
            response = new String(code, "UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
        return response;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.diz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.ot:
                Intent intent = new Intent(this, OtDizActivity.class);
                intent.putExtra("cod", cod);
                addDataToDatabase(cod);
                startActivity(intent);
                return true;

            case R.id.zakaz:
                Intent intent2 = new Intent(this, ZakazForDizActivity.class);
                startActivity(intent2);
                return true;

            case R.id.img:
                Intent intent3 = new Intent(this, UploadImgToServerActivity.class);
                startActivity(intent3);
                return true;

            case R.id.zad:
                Intent intent4 = new Intent(this, ManagerZadActivity.class);
                startActivity(intent4);
                return true;
    }
        return super.onOptionsItemSelected(item);
    }

    //Метод для добавдения в БД cod
    private void addDataToDatabase(String cod1) {

        // URL для размещения наших данных
        String url = Config.API_BASE + "cod.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        // в строке ниже мы вызываем строку
        // метод запроса для отправки данных в наш API
        // здесь мы вызываем метод post.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", "RESPONSE IS " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                params.put("cod", cod1);
                // наконец мы возвращаем наши параметры.
                return params;
            }
        };
        // ниже строки, чтобы сделать
        //  запрос объекта json.
        queue.add(request);
    }
}