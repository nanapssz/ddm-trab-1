package com.example.belleza;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private ImageView imgBannerHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgBannerHome = findViewById(R.id.imgBannerHome);

        imgBannerHome.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DetalhesProdutoActivity.class);
            startActivity(intent);
        });
    }
}