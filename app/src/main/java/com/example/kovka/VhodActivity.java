package com.example.kovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VhodActivity extends AppCompatActivity {

    private static final String URL_VHOD = Config.API_BASE + "vhodApp.php";

    TextView textView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vhod);

        textView = findViewById(R.id.tv);
        editText = findViewById(R.id.etVhod);

        AppCompatButton btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vhod(v);
            }
        });

        TextView tvPrivacyLink = findViewById(R.id.tvPrivacyLink);
        tvPrivacyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String privacyUrl = Config.SITE_BASE + "privacy.php";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl));
                startActivity(browserIntent);
            }
        });
    }

    public void vhod(View view) {
        // Проверка чекбокса согласия
        CheckBox cbAgree = findViewById(R.id.cbAgree);
        if (!cbAgree.isChecked()) {
            textView.setText("!!!   Необходимо согласие с политикой конфиденциальности   !!!");
            textView.setTextColor(Color.RED);
            return;
        }

        String et2 = editText.getText().toString().trim();

        if (et2.isEmpty()) {
            textView.setText("!!!   Введите код доступа   !!!");
            textView.setTextColor(Color.RED);
            return;
        }

        checkCodeOnServer(et2);
    }

    private void checkCodeOnServer(final String cod) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, URL_VHOD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            boolean success = json.optBoolean("success", false);

                            if (!success) {
                                textView.setText("!!!   Код доступа введён не верно   !!!");
                                textView.setTextColor(Color.RED);
                                return;
                            }

                            JSONObject user = json.getJSONObject("user");
                            int classWork = user.getInt("class_work");
                            String prof = user.optString("prof", "");

                            // Логируем вход через cod.php (как было)
                            addDataToDatabase(cod);

                            Intent intent;
                            switch (classWork) {
                                case 1:
                                    intent = new Intent(VhodActivity.this, AdminActivity.class);
                                    break;
                                case 2:
                                    intent = new Intent(VhodActivity.this, DizActivity.class);
                                    break;
                                case 4:
                                    intent = new Intent(VhodActivity.this, MenedgerActivity.class);
                                    break;
                                case 3:
                                case 5:
                                case 6:
                                default:
                                    intent = new Intent(VhodActivity.this, OtherSpecialistsActivity.class);
                                    break;
                            }
                            intent.putExtra("cod", cod);
                            intent.putExtra("prof", prof);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VhodActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = NetworkUtils.getErrorMessage(error);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cod", cod);
                return params;
            }
        };

        NetworkUtils.configureTimeout(request);
        queue.add(request);
    }

    private void addDataToDatabase(String cod1) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();
            return;
        }

        String url = Config.API_BASE + "cod.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "RESPONSE IS " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VhodActivity", "Ошибка при сохранении кода: Ошибка соединения.");
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cod", cod1);
                return params;
            }
        };

        NetworkUtils.configureTimeout(request);
        queue.add(request);
    }
}