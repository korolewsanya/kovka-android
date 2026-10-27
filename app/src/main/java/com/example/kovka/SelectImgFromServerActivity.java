package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectImgFromServerActivity extends AppCompatActivity {
    private static final String URL_PRODUCTS = Config.API_BASE + "get_products.php";
    List<Product> productList;
    RecyclerView recyclerView;
    JSONObject product;
    private String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_img_from_server);

        adminCod = getIntent().getStringExtra("cod");

        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        loadProducts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_img, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.save:
                Intent intent = new Intent(this, SaveImgActivity.class);
                intent.putExtra("cod", adminCod);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProducts() {
        ProductsAdapter.OnStateClickListener stateClickListener = new ProductsAdapter.OnStateClickListener() {
            public void onStateClick(Product state, int position) {
                Intent intent = new Intent(SelectImgFromServerActivity.this, ImgChDelActivity.class);
                intent.putExtra("image", state.getImage());
                intent.putExtra("id", state.getId());
                intent.putExtra("tags", state.getTags());
                intent.putExtra("path", state.getPath());
                intent.putExtra("category", state.getCategory());
                intent.putExtra("admin_cod", adminCod); // <-- передаем код для изменения/удаления
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Был выбран пункт: " + state.getTags(), Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                product = array.getJSONObject(i);
                                productList.add(new Product(
                                        product.getString("path"),
                                        product.getString("tags"),
                                        product.getString("image"),
                                        product.getString("id"),
                                        product.getString("category")
                                ));
                            }
                            ProductsAdapter adapter = new ProductsAdapter(SelectImgFromServerActivity.this, productList, stateClickListener);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SelectImgFromServerActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
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
        Volley.newRequestQueue(this).add(stringRequest);
    }
}