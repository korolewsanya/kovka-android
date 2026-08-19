package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ZakazActivity extends AppCompatActivity {
    private static final String JSON_URL = Config.API_BASE + "zakazApp.php";// UTF-8
    ListView listView;
    ArrayList<JSONObject> infoList;
    String manager;
    Bundle arguments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaz);

        arguments = getIntent().getExtras();
        if(arguments!=null) {
            manager = arguments.get("manager").toString();
        }

        listView = (ListView) findViewById(R.id.listView);
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
                String pay = infoList.get(position).optString("Pay");
                String proces = infoList.get(position).optString("Proces");
                String name = infoList.get(position).optString("Name");
                String tel = infoList.get(position).optString("Tel");
                String email = infoList.get(position).optString("Email");
                String coment = infoList.get(position).optString("Coment");

                Intent intent = new Intent(getApplicationContext(), ZakazChDelActivity.class);
                intent.putExtra("idi", idi);
                intent.putExtra("date", date);
                intent.putExtra("izdelie", izdelie);
                intent.putExtra("image", image);
                intent.putExtra("dlina", dlina);
                intent.putExtra("shirina", shirina);
                intent.putExtra("visota", visota);
                intent.putExtra("prise", prise);
                intent.putExtra("pay", pay);
                intent.putExtra("proces", proces);
                intent.putExtra("name", name);
                intent.putExtra("tel", tel);
                intent.putExtra("email", email);
                intent.putExtra("coment", coment);

                if(manager!=null) {
                    intent.putExtra("manager", manager);
                }
                startActivity(intent);

                changeImgToDatabase(image);
            }
        });

    }

    private void loadJSONFromURL(String url) {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ListView.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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
                        Toast.makeText(getApplicationContext(), "Ошибка соединения. Попробуйте позже.", Toast.LENGTH_SHORT).show();
                    }
                });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_zakaz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.save:
                Intent intent = new Intent(this, ZakazSaveActivity.class);
                if(arguments!=null) {
                    intent.putExtra("manager", manager);
                }
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Метод для замены названия изображения в таблице img
    private void changeImgToDatabase(String img1) {

        // URL для размещения наших данных
        String url = Config.API_BASE + "img_id.php";

        // создание новой переменной для нашей очереди запросов
        RequestQueue queue = Volley.newRequestQueue(ZakazActivity.this);

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
                    Toast toast = Toast.makeText(ZakazActivity.this, "Заказ добавлен", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(ZakazActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
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
                params.put("img", img1);

                // наконец мы возвращаем наши параметры.
                return params;
            }
        };
        // строки, чтобы сделать запрос объекта json.
        queue.add(request);
    }
}