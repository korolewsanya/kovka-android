package com.example.kovka;

public class Config {
    // Базовый URL (меняешь здесь один раз)
    private static final String BASE_URL = "http://192.168.1.156/Kovka_git/kovka/";

    // Составные пути
    public static final String API_BASE = BASE_URL + "Kovka_CRM_Api/";
    public static final String IMG_BASE = BASE_URL + "img/";

    // Пути для создания, замены, удаленя
    public static final String URL_CREATE = API_BASE + "create/";
    public static final String URL_CHANGE = API_BASE + "change/";
    public static final String URL_DELETE = API_BASE + "delete/";


    // Если нужно для других классов
    public static String getImageUrl(String filename) {
        return IMG_BASE + filename;
    }
}
