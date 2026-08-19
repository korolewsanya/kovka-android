package com.example.kovka;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkingProcessActivity extends AppCompatActivity {
    private static final String JSON_URL = Config.API_BASE + "working_processApp.php";
    private static final String UPDATE_URL = Config.API_BASE + "update_working_process.php";
    private static final String DELETE_URL = Config.API_BASE + "delete_working_process.php";

    ListView listView;
    ArrayList<JSONObject> infoList;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_process);

        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        loadJSONFromURL(JSON_URL);
    }

    private void loadJSONFromURL(String url) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray("working_process");
                            infoList = getArrayListFromJSONArray(jsonArray);
                            ListAdapter adapter = new WorkingProcessAdapter(getApplicationContext(),
                                    R.layout.working_process, R.id.date, infoList);
                            listView.setAdapter(adapter);

                            // Обработчик клика по элементу списка
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    JSONObject selected = infoList.get(position);
                                    final String recordId = selected.optString("id");
                                    final String currentText = selected.optString("tz");
                                    final String imageFileName = selected.optString("image"); // имя файла из колонки image

                                    // Диалог выбора действия
                                    AlertDialog.Builder builder = new AlertDialog.Builder(WorkingProcessActivity.this);
                                    builder.setTitle("Действие с заданием");
                                    builder.setItems(new String[]{"Изменить", "Удалить", "Посмотреть изображение"},
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {
                                                        showEditDialog(recordId, currentText);
                                                    } else if (which == 1) {
                                                        showDeleteConfirmation(recordId);
                                                    } else if (which == 2) {
                                                        if (imageFileName != null && !imageFileName.isEmpty()) {
                                                            showImageDialog(imageFileName);
                                                        } else {
                                                            Toast.makeText(WorkingProcessActivity.this,
                                                                    "Для этого задания нет изображения", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            });
                                    builder.show();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(WorkingProcessActivity.this, "Ошибка данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(WorkingProcessActivity.this, "Ошибка загрузки: " + "Ошибка соединения. Попробуйте позже.", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


        //  URL папки с изображениями
        private void showImageDialog(String imageFileName) {
            // URL папки с изображениями
            String baseUrl = Config.IMG_BASE;
            String fullUrl = baseUrl + imageFileName;

            // Открываем FullScreenImageActivity вместо диалога
            Intent intent = new Intent(WorkingProcessActivity.this, FullScreenImageActivity.class);
            intent.putExtra("image_url", fullUrl);
            startActivity(intent);
        }

    private void showEditDialog(final String id, final String oldText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактировать задание");
        final EditText input = new EditText(this);
        input.setText(oldText);
        builder.setView(input);
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newText = input.getText().toString().trim();
                if (!newText.isEmpty()) {
                    updateTask(id, newText);
                } else {
                    Toast.makeText(WorkingProcessActivity.this, "Поле не может быть пустым", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void updateTask(final String id, final String newText) {
        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (!json.getBoolean("error")) {
                                Toast.makeText(WorkingProcessActivity.this, "Задание обновлено", Toast.LENGTH_SHORT).show();
                                // Обновить список
                                loadJSONFromURL(JSON_URL);
                            } else {
                                Toast.makeText(WorkingProcessActivity.this, "Ошибка: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WorkingProcessActivity.this, "Ошибка соединения", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("tz", newText);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void showDeleteConfirmation(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление");
        builder.setMessage("Вы действительно хотите удалить это задание?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTask(id);
            }
        });
        builder.setNegativeButton("Нет", null);
        builder.show();
    }

    private void deleteTask(final String id) {
        StringRequest request = new StringRequest(Request.Method.POST, DELETE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (!json.getBoolean("error")) {
                                Toast.makeText(WorkingProcessActivity.this, "Задание удалено", Toast.LENGTH_SHORT).show();
                                loadJSONFromURL(JSON_URL);
                            } else {
                                Toast.makeText(WorkingProcessActivity.this, "Ошибка: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WorkingProcessActivity.this, "Ошибка соединения", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray) {
        ArrayList<JSONObject> aList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                aList.add(jsonArray.getJSONObject(i));
            }
        } catch (JSONException js) {
            js.printStackTrace();
        }
        return aList;
    }

    public static String EncodingToUTF8(String response) {
        try {
            byte[] code = response.getBytes("ISO-8859-1");
            return new String(code, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.working_process, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.give_tz) {
            startActivity(new Intent(this, GiveTzActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}