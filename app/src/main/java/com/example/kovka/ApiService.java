package com.example.kovka;

//модель ответа

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiService {
    private static final String BASE_URL = Config.API_BASE;
    private static ApiService instance;
    private RequestQueue requestQueue;
    private Gson gson;

    private ApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        gson = new Gson();
    }

    public static synchronized ApiService getInstance(Context context) {
        if (instance == null) {
            instance = new ApiService(context);
        }
        return instance;
    }

    public interface ImageListCallback {
        void onSuccess(List<ImageModel> images);
        void onError(String error);
    }

    public interface SimpleCallback {
        void onSuccess(ApiResponse response);
        void onError(String error);
    }

    public void getImages(ImageListCallback callback) {
        String url = BASE_URL + "get_images.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Type listType = new TypeToken<List<ImageModel>>(){}.getType();
                        List<ImageModel> images = gson.fromJson(response, listType);
                        callback.onSuccess(images);
                    } catch (Exception e) {
                        callback.onError("Ошибка парсинга");
                    }
                },
                error -> callback.onError("Ошибка соединения. Попробуйте позже.")
        );

        requestQueue.add(request);
    }

    public void deleteImage(String filename, SimpleCallback callback) {
        String url = BASE_URL + "delete_image.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);
                        callback.onSuccess(apiResponse);
                    } catch (Exception e) {
                        callback.onError("Ошибка парсинга");
                    }
                },
                error -> callback.onError("Ошибка соединения. Попробуйте позже.")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("filename", filename);
                return params;
            }
        };

        requestQueue.add(request);
    }

    public void renameImage(String oldName, String newName, SimpleCallback callback) {
        String url = BASE_URL + "rename_image.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);
                        callback.onSuccess(apiResponse);
                    } catch (Exception e) {
                        callback.onError("Ошибка парсинга");
                    }
                },
                error -> callback.onError("Ошибка соединения. Попробуйте позже.")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("old_name", oldName);
                params.put("new_name", newName);
                return params;
            }
        };

        requestQueue.add(request);
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
