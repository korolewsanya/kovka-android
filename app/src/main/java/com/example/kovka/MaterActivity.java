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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MaterActivity extends AppCompatActivity {
    private static final String JSON_URL = Config.API_BASE + "materApp.php";// UTF-8
    ListView listView;
    ArrayList<JSONObject> infoList;
    String manager;
    Bundle arguments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mater);

        arguments = getIntent().getExtras();
        if(arguments!=null) {
            manager = arguments.get("manager").toString();
        }

        listView = (ListView) findViewById(R.id.listView);
        loadJSONFromURL(JSON_URL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nom = infoList.get(position).optString("id");
                String date = infoList.get(position).optString("date");
                String name = infoList.get(position).optString("name");
                String kup = infoList.get(position).optString("kup");
                String izras = infoList.get(position).optString("izras");
                String ost = infoList.get(position).optString("ost");
                String prise = infoList.get(position).optString("prise");
                String itogo = infoList.get(position).optString("itogo");
                Intent intent = new Intent(getApplicationContext(),MaterChDelActivity.class);

                intent.putExtra("id", nom);
                intent.putExtra("date", date);
                intent.putExtra("name", name);
                intent.putExtra("kup", kup);
                intent.putExtra("izras", izras);
                intent.putExtra("ost", ost);
                intent.putExtra("prise", prise);
                intent.putExtra("itogo", itogo);

                if(manager!=null) {
                    intent.putExtra("manager", manager);
                }
                startActivity(intent);
            }
        });
    }

    private void  loadJSONFromURL(String url){
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ListView.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray("mater");
                            ArrayList<JSONObject> listItems = getArrayListFromJSONArray(jsonArray);
                            infoList = listItems;
                            ListAdapter adapter = new MaterAdapter(getApplicationContext(),R.layout.row_mater,R.id.id,listItems);
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
        getMenuInflater().inflate(R.menu.fin_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.save_fin:
                Intent intent = new Intent(this, MaterSaveActivity.class);
                if(arguments!=null) {
                    intent.putExtra("manager", manager);
                }
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}