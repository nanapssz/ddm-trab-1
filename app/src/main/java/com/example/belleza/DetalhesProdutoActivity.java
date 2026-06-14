package com.example.belleza;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private ImageView btnVoltar;
    private TextView btnMenos;
    private TextView btnMais;
    private TextView txtQuantidade;

    private int quantidade = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        btnVoltar = findViewById(R.id.btnVoltar);
        btnMenos = findViewById(R.id.btnMenos);
        btnMais = findViewById(R.id.btnMais);
        txtQuantidade = findViewById(R.id.txtQuantidade);

        btnVoltar.setOnClickListener(v -> finish());

        btnMais.setOnClickListener(v -> {
            quantidade++;
            txtQuantidade.setText(String.valueOf(quantidade));
        });

        btnMenos.setOnClickListener(v -> {
            if (quantidade > 0) {
                quantidade--;
                txtQuantidade.setText(String.valueOf(quantidade));
            }
        });
    }
}