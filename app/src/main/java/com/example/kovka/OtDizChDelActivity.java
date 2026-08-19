package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OtDizChDelActivity extends AppCompatActivity {
    private EditText nom, data, tz, otchet;
    private ImageView reportImageView;
    private String nom1, data1, tz1, otchet1, cod1, imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ot_diz_ch_del);

        TextView textView = findViewById(R.id.tz);
        textView.setText("Отчёт");

        nom = findViewById(R.id.nom);
        data = findViewById(R.id.data);
        tz = findViewById(R.id.teh_zd);
        otchet = findViewById(R.id.order);
        reportImageView = findViewById(R.id.report_image);

        Intent intent = getIntent();
        String idi = intent.getStringExtra("idi");
        String data2 = intent.getStringExtra("data");
        String tz2 = intent.getStringExtra("tz");
        String job2 = intent.getStringExtra("job");
        imageName = intent.getStringExtra("image");

        Bundle arguments = getIntent().getExtras();
        cod1 = arguments.getString("cod");

        nom.setText(idi);
        data.setText(data2);
        tz.setText(tz2);
        otchet.setText(job2);

        // Загружаем фото, если оно есть
        if (imageName != null && !imageName.isEmpty()) {
            String imageUrl = "http://192.168.1.156/Kovka_git/kovka/img/" + imageName;
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(null)
                    .into(reportImageView);
            reportImageView.setVisibility(View.VISIBLE);
        } else {
            reportImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.zakaz_ch_del, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.change_z:
                nom1 = nom.getText().toString();
                data1 = data.getText().toString();
                tz1 = tz.getText().toString();
                otchet1 = otchet.getText().toString();

                if (TextUtils.isEmpty(otchet1)) {
                    otchet.setError("Заполните отчёт");
                    return true;
                }
                if (TextUtils.isEmpty(nom1)) {
                    nom.setError("Нет ID");
                    return true;
                }
                addDataToDatabase(nom1, data1, otchet1);
                return true;

            case R.id.del_z:
                getCourseDetails2(nom.getText().toString());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String nom1, String data1, String otchet1) {
        String url = Config.URL_CHANGE + "change_ot_dizApp.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("CHANGE_RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(OtDizChDelActivity.this, "Изменения сохранены", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(OtDizChDelActivity.this, VhodActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("cod", cod1);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(OtDizChDelActivity.this, "Ошибка: " + "Ошибка соединения. Попробуйте позже.", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", nom1);
                params.put("date", data1);
                params.put("otchet", otchet1);
                return params;
            }
        };
        queue.add(request);
    }

    private void getCourseDetails2(String courseId) {
        String url = Config.URL_DELETE + "delete_ot_dizApp.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            Toast.makeText(OtDizChDelActivity.this, "Отчёт удалён", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(OtDizChDelActivity.this, VhodActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("cod", cod1);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(OtDizChDelActivity.this, "Ошибка: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(OtDizChDelActivity.this, "Ошибка ответа сервера", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(OtDizChDelActivity.this, "Ошибка соединения. Попробуйте позже.", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", courseId);
                return params;
            }
        };
        queue.add(request);
    }
}