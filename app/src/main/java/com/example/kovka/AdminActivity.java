package com.example.kovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.img:
                Intent intent3 = new Intent(this, UploadImgToServerActivity.class);
                startActivity(intent3);
                return true;

            case R.id.img2:
                Intent intent4 = new Intent(this, SelectImgFromServerActivity.class);
                startActivity(intent4);
                return true;

            case R.id.img3:
                Intent intent5 = new Intent(this, SelectAllImgFromServerActivity.class);
                startActivity(intent5);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void zakaz(View view) {
        Intent intent = new Intent(this, ZakazActivity.class);
        startActivity(intent);
    }

    public void mater(View view) {
        Intent intent = new Intent(this, MaterActivity.class);
        startActivity(intent);
    }

    public void zp(View view) {
        Intent intent = new Intent(this, ZpActivity.class);
        startActivity(intent);
    }

    public void rashod(View view) {
        Intent intent = new Intent(this, RashodActivity.class);
        startActivity(intent);
    }

    public void fin(View view) {
        Intent intent = new Intent(this, FinActivity.class);
        startActivity(intent);
    }

    public void wokers(View view) {
        Intent intent = new Intent(this, WokersActivity.class);
        startActivity(intent);
    }

    public void otchet(View view) {
        Intent intent = new Intent(this, WorkingProcessActivity.class);
        startActivity(intent);
    }

    public void dos(View view) {
        Intent intent = new Intent(this, DostupActivity.class);
        startActivity(intent);
    }
}