package com.example.belleza.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.belleza.R
import com.example.belleza.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val EXTRA_ABA = "aba"
        const val ABA_HOME = "home"
        const val ABA_CARRINHO = "carrinho"
        const val ABA_CONTA = "conta"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBottomNavigation()

        if (savedInstanceState == null) {
            val abaRecebida = intent.getStringExtra(EXTRA_ABA) ?: ABA_HOME
            abrirAbaInicial(abaRecebida)
        }
    }

    private fun configurarBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    trocarFragment(HomeFragment())
                    true
                }

                R.id.nav_favoritos -> {
                    ToastFavoritos()
                    false
                }

                R.id.nav_carrinho -> {
                    trocarFragment(CarrinhoFragment())
                    true
                }

                R.id.nav_conta -> {
                    trocarFragment(MinhaContaFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun abrirAbaInicial(aba: String) {
        when (aba) {
            ABA_CARRINHO -> {
                binding.bottomNavigation.selectedItemId = R.id.nav_carrinho
                trocarFragment(CarrinhoFragment())
            }

            ABA_CONTA -> {
                binding.bottomNavigation.selectedItemId = R.id.nav_conta
                trocarFragment(MinhaContaFragment())
            }

            else -> {
                binding.bottomNavigation.selectedItemId = R.id.nav_home
                trocarFragment(HomeFragment())
            }
        }
    }

    private fun trocarFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun ToastFavoritos() {
        android.widget.Toast.makeText(
            this,
            "Favoritos em desenvolvimento",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }
}