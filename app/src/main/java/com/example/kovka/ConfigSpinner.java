package com.example.kovka;

public class ConfigSpinner {
        // ИЗМЕНЕНО: Теперь список сотрудников берется из dostupApp.php (защищенного)
        public static final String DATA_URL = Config.API_BASE + "dostupApp.php";

        //Теги, используемые в строке JSON
        public static final String TAG_COD = "cod";
        public static final String TAG_CLASS_WORK = "class_work";
        public static final String TAG_SPEC = "prof";
        public static final String TAG_NAME = "name";

        //JSON имя массива
        public static final String JSON_ARRAY = "dostup";
}