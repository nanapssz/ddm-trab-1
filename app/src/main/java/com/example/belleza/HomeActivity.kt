package com.example.belleza

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var imgBannerHome: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        imgBannerHome = findViewById(R.id.imgBannerHome)

        imgBannerHome.setOnClickListener {
            val intent = Intent(this, DetalhesProdutoActivity::class.java)
            startActivity(intent)
        }
    }
}