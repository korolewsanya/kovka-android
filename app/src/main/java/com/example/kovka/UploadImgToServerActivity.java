package com.example.kovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadImgToServerActivity extends AppCompatActivity {
    ImageView imageView;
    EditText editTextTags;
    private String adminCod;
    private Bitmap selectedBitmap;   // ИЗМЕНЕНО: храним выбранную картинку до нажатия "Загрузить"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_img_to_server);

        adminCod = getIntent().getStringExtra("cod");
        Log.d("UPLOAD_RAW", "adminCod = " + adminCod);

        // ИЗМЕНЕНО: защита от отсутствующего кода
        if (adminCod == null || adminCod.isEmpty()) {
            Toast.makeText(this, "Ошибка: код доступа не передан", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imageView = findViewById(R.id.imageView);
        editTextTags = findViewById(R.id.editTextTags);

        // ИЗМЕНЕНО: первая кнопка — выбор картинки из галереи
        findViewById(R.id.buttonUploadImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextTags.getText().toString().trim().isEmpty()) {
                    editTextTags.setError("Введите название изображения");
                    editTextTags.requestFocus();
                    Toast.makeText(UploadImgToServerActivity.this,
                            "Пожалуйста, заполните поле", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        // ИЗМЕНЕНО: отдельная кнопка "Отправить" — загрузка только по явному нажатию.
        // Если в layout есть только одна кнопка R.id.buttonUploadImage — её можно использовать
        // и как "выбрать + отправить", но лучше разделить. Ниже — вариант с отдельной кнопкой.
        View sendBtn = findViewById(R.id.buttonSendImage);
        if (sendBtn != null) {
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedBitmap == null) {
                        Toast.makeText(UploadImgToServerActivity.this,
                                "Сначала выберите изображение", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (editTextTags.getText().toString().trim().isEmpty()) {
                        editTextTags.setError("Введите название изображения");
                        editTextTags.requestFocus();
                        return;
                    }
                    uploadBitmap(selectedBitmap);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                selectedBitmap = bitmap;
                imageView.setImageBitmap(bitmap);

                // ИЗМЕНЕНО: если в layout НЕТ отдельной кнопки "Отправить" — грузим сразу.
                // Если кнопка есть — пользователь нажмёт её сам.
                if (findViewById(R.id.buttonSendImage) == null) {
                    uploadBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка чтения изображения", Toast.LENGTH_LONG).show();
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap) {
        final String tags = editTextTags.getText().toString().trim();

        // ИЗМЕНЕНО: экранируем код в URL (на случай спецсимволов в хеше)
        String url = EndPoints.UPLOAD_URL + "&admin_cod=" + Uri.encode(adminCod);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String raw = new String(response.data);
                        Log.d("UPLOAD_RAW", "Сырой ответ сервера: " + raw);

                        try {
                            JSONObject obj = new JSONObject(raw);
                            boolean error = obj.optBoolean("error", true);
                            String message = obj.optString("message",
                                    error ? "Ошибка загрузки" : "Файл успешно загружен");

                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            if (!error) {
                                // ИЗМЕНЕНО: НЕ уходим в AdminActivity — просто закрываемся
                                // и возвращаемся в DizActivity (откуда пришли).
                                setResult(RESULT_OK);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Ошибка обработки ответа: " + raw,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                "Ошибка соединения. Попробуйте позже.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tags", tags);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
}