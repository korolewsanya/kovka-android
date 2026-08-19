package com.example.kovka;

import android.app.WallpaperManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telecom.Call;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.ViewModel;

public class SelectImgFromServerActivity extends AppCompatActivity {
    private static final String URL_PRODUCTS = Config.API_BASE + "get_products.php";
    List<Product> productList;
    RecyclerView recyclerView;
    JSONObject product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_img_from_server);

        //получение recyclerview из xml
        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();

        //Переворачивает список
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        //этот метод будет извлекать и анализировать json
        //чтобы отобразить его в recyclerview
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

        switch(id) {
            case R.id.save:
                Intent intent = new Intent(this, SaveImgActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProducts() {
        // определяем слушателя нажатия элемента в списке
        ProductsAdapter.OnStateClickListener stateClickListener = new ProductsAdapter.OnStateClickListener() {

            public void onStateClick(Product state, int position) {

                Intent intent = new Intent(SelectImgFromServerActivity.this,ImgChDelActivity.class);
                intent.putExtra("image",state.getImage());
                intent.putExtra("id",state.getId());
                intent.putExtra("tags",state.getTags());
                intent.putExtra("path",state.getPath());
                intent.putExtra("category", state.getCategory());
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Был выбран пункт: " + state.getTags(),
                        Toast.LENGTH_SHORT).show();
            }
        };
        /*
         * Создание строкового запроса
         * Тип запроса GET определяется первым параметром.
         * URL-адрес определяется во втором параметре.
         * Затем у нас есть прослушиватель ответов и прослушиватель ошибок.
         * В прослушивателе ответа мы получим ответ JSON в виде строки.
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //преобразование строки в объект массива json
                            JSONArray array = new JSONArray(response);

                            //обход всего объекта
                            for (int i = 0; i < array.length(); i++) {

                                //получение объекта продукта из массива JSON
                                product = array.getJSONObject(i);

                                //добавление товара в список товаров
                                productList.add(new Product(
                                        product.getString("path"),
                                        product.getString("tags"),
                                        product.getString("image"),
                                        product.getString("id"),
                                        product.getString("category")
                                ));
                            }

                            //создание объекта адаптера и установка его в recyclerview
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

                    }
                });

        //добавление нашего строкового запроса в очередь
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
