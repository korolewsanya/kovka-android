package com.example.kovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ManagerZadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_zad);
    }

    public void zakaz(View view) {
        Intent intent = new Intent(this, SelectAllImgFromServerActivity2.class);
        intent.putExtra("manager", "manager");
        startActivity(intent);
    }

    public void otchet(View view) {
        Intent intent = new Intent(this, WorkingProcessActivity.class);
        intent.putExtra("manager", "manager");
        startActivity(intent);
    }

    public void mater(View view) {
        Intent intent = new Intent(this, MaterActivity.class);
        intent.putExtra("manager", "manager");
        startActivity(intent);
    }

    public void rashod(View view) {
        Intent intent = new Intent(this, RashodActivity.class);
        intent.putExtra("manager", "manager");
        startActivity(intent);
    }
}