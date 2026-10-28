package com.example.kovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class
AdminActivity extends AppCompatActivity {
    private String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        adminCod = getIntent().getStringExtra("cod");
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
                intent3.putExtra("cod", adminCod);
                startActivity(intent3);
                return true;

            case R.id.img2:
                Intent intent4 = new Intent(this, SelectImgFromServerActivity.class);
                intent4.putExtra("cod", adminCod);
                startActivity(intent4);
                return true;

            case R.id.img3:
                Intent intent5 = new Intent(this, SelectAllImgFromServerActivity.class);
                intent5.putExtra("cod", adminCod); // <-- Вот это передает код!
                startActivity(intent5);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openWithCod(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("cod", adminCod);
        startActivity(intent);
    }

    public void zakaz(View view)  { openWithCod(ZakazActivity.class); }
    public void mater(View view)  { openWithCod(MaterActivity.class); }
    public void zp(View view)     { openWithCod(ZpActivity.class); }
    public void rashod(View view) { openWithCod(RashodActivity.class); }
    public void fin(View view)    { openWithCod(FinActivity.class); }
    public void wokers(View view) { openWithCod(WokersActivity.class); }
    public void otchet(View view) { openWithCod(WorkingProcessActivity.class); }
    public void dos(View view)    { openWithCod(DostupActivity.class); }
}