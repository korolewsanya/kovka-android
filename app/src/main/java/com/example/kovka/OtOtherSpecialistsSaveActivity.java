package com.example.kovka;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OtOtherSpecialistsSaveActivity extends AppCompatActivity {
    EditText data;
    EditText tz;
    EditText ot;

    private String data1, tz1, ot1, cod1, prof1, class_work1, name1, image1;
    String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale("ru")).format(Calendar.getInstance().getTime());

    private EditText imgEditText;
    private ImageView selectedImageView;

    private Uri selectedImageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ot_other_specialists_save);

        data = (EditText) findViewById(R.id.data);
        tz = (EditText) findViewById(R.id.teh_zd);
        ot = (EditText) findViewById(R.id.order);

        Intent intent = getIntent();
        String tz2 = intent.getStringExtra("tz");
        prof1 = intent.getStringExtra("prof");
        class_work1 = intent.getStringExtra("class_work");
        name1 = intent.getStringExtra("name");
        Bundle arguments = getIntent().getExtras();
        cod1 = arguments.get("cod").toString();

        tz.setText(tz2);
        data.setText(today);

        imgEditText = findViewById(R.id.img);
        selectedImageView = findViewById(R.id.selected_image);
        Button selectPhotoButton = findViewById(R.id.select_photo_button);

        selectPhotoButton.setOnClickListener(v -> {
            // Открываем выбор изображений (только image/*)
            selectImageLauncher.launch("*/*");
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tz_sohranit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sohr:
                data1 = data.getText().toString();
                tz1 = tz.getText().toString();
                ot1 = ot.getText().toString();
                image1 = imgEditText.getText().toString();

                // Валидация текстовых полей
                if (TextUtils.isEmpty(data1)) {
                    data.setError("Пожалуйста, заполните это поле");
                } else if (TextUtils.isEmpty(ot1)) {
                    ot.setError("Пожалуйста, заполните это поле");
                } else {
                    // Обработка поля image1: если пусто или только пробелы — ставим «нет изображения»
                    if (TextUtils.isEmpty(image1) || image1.trim().isEmpty()) {
                        image1 = "нет изображения";
                    }
                    // Сохраняем данные в базу
                    addDataToDatabase(data1, tz1, ot1, cod1, prof1, class_work1, name1, image1, selectedImageUri);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
    //Метод для добавдения в БД Отчёты
    private void addDataToDatabase(String data1, String tz1, String ot1, String cod1,
                                   String prof1, String class_work1, String name1,
                                   String image1, Uri imageUri) {

        String url = Config.URL_CREATE + "create_ot_dizApp.php";
        RequestQueue queue = Volley.newRequestQueue(OtOtherSpecialistsSaveActivity.this);

        // Текстовые параметры (ВСЕ ваши исходные поля)
        Map<String, String> params = new HashMap<>();
        params.put("date", data1);
        params.put("tz", tz1);
        params.put("ot", ot1);
        params.put("cod", cod1);
        params.put("prof", prof1);
        params.put("class_work", class_work1);
        params.put("name", name1);
        params.put("image", image1);   // <-- ваше строковое имя файла

        // Файл изображения (если выбран)
        Map<String, MultipartRequest.DataPart> dataParts = null;
        if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] fileBytes = readBytes(inputStream);
                String fileName = getFileName(imageUri);  // используем ваш метод
                String mimeType = getContentResolver().getType(imageUri);
                if (mimeType == null) mimeType = "image/jpeg";

                dataParts = new HashMap<>();
                // ключ "image_file" – серверный скрипт будет искать файл в $_FILES['image_file']
                dataParts.put("image_file", new MultipartRequest.DataPart(fileBytes, fileName, mimeType));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(OtOtherSpecialistsSaveActivity.this, "Ошибка чтения файла", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        MultipartRequest request = new MultipartRequest(
                url,
                params,
                dataParts,
                response -> {
                    Log.e("TAG", "RESPONSE IS " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(OtOtherSpecialistsSaveActivity.this, "Отчёт успешно создан", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Переход
                    Intent intent = new Intent(OtOtherSpecialistsSaveActivity.this, OtherSpecialistsActivity.class);
                    intent.putExtra("cod", cod1);
                    intent.putExtra("class_work", class_work1);
                    intent.putExtra("prof", prof1);
                    intent.putExtra("name", name1);
                    intent.putExtra("name", image1);   // ваша строка
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                },
                error -> {
                    Toast.makeText(OtOtherSpecialistsSaveActivity.this,
                            "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }

    // Вспомогательный метод для чтения InputStream в byte[]
    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    /**
     * Извлекает имя файла из Uri (например, "IMG_20230501.jpg").
     */
    private String getFileName(Uri uri) {
        String name = "";
        // Используем try-with-resources для курсора
        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (displayNameIndex >= 0) {
                    name = cursor.getString(displayNameIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (name.isEmpty()) {
            // Запасной вариант: последний сегмент пути
            name = uri.getLastPathSegment();
            if (name == null) name = "unknown";
        }
        return name;
    }


    // Лаунчер для выбора изображения
    private final ActivityResultLauncher<String> selectImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    // Отображаем выбранное изображение
                    selectedImageView.setImageURI(uri);
                    selectedImageView.setVisibility(View.VISIBLE);

                    // Получаем имя файла и вставляем в EditText
                    String fileName = getFileName(uri);
                    imgEditText.setText(fileName);

                    // Сохраняем URI для последующей загрузки файла
                    selectedImageUri = uri;
                }
            });
}