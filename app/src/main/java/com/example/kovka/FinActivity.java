package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class FinActivity extends AppCompatActivity {

    private String adminCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin);

        adminCod = getIntent().getStringExtra("cod");

        WebView browser = findViewById(R.id.webBrowser);
        browser.loadUrl(Config.API_BASE + "finApp.php");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tab_fin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.tab_fin:
                Intent intent = new Intent(this, FinUprActivity.class);
                intent.putExtra("cod", adminCod);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}