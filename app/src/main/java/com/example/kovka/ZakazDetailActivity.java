package com.example.kovka;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ZakazDetailActivity extends AppCompatActivity {
    private EditText id, data, izdelie, image, dlina, shirina, visota, prise, proces, coment	;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaz_detail);
        id = findViewById(R.id.id);
        data = findViewById(R.id.data);
        izdelie = findViewById(R.id.izdelie);
        image = findViewById(R.id.image);
        dlina = findViewById(R.id.dlina);
        shirina = findViewById(R.id.shirina);
        visota = findViewById(R.id.visota);
        prise = findViewById(R.id.prise);
        proces = findViewById(R.id.proces);
        coment = findViewById(R.id.coment);

        Intent intent = getIntent();
        String idi2 = intent.getStringExtra("idi");
        String date2 = intent.getStringExtra("date");
        String izdelie2 = intent.getStringExtra("izdelie");
        String image2 = intent.getStringExtra("image");
        String dlina2 = intent.getStringExtra("dlina");
        String shirina2 = intent.getStringExtra("shirina");
        String visota2 = intent.getStringExtra("visota");
        String prise2 = intent.getStringExtra("prise");
        String proces2 = intent.getStringExtra("proces");
        String coment2 = intent.getStringExtra("coment");

        id.setText(idi2);
        data.setText(date2);
        izdelie.setText(izdelie2);
        image.setText(image2);
        dlina.setText(dlina2);
        shirina.setText(shirina2);
        visota.setText(visota2);
        prise.setText(prise2);
        proces.setText(proces2);
        coment.setText(coment2);

        WebView browser=findViewById(R.id.webBrowser);
        browser.loadUrl(Config.API_BASE + "img.php");
    }
}