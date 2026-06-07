package com.example.belleza.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.belleza.model.FavoritoEntity

@Database(entities = [FavoritoEntity::class], version = 1, exportSchema = false)
abstract class BancoDeDadosApp : RoomDatabase() {

    abstract fun favoritoDao(): FavoritoDao

    companion object {
        @Volatile
        private var INSTANCIA: BancoDeDadosApp? = null

        fun obterBancoDeDados(contexto: Context): BancoDeDadosApp {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    BancoDeDadosApp::class.java,
                    "banco_loja_belleza"
                ).build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}