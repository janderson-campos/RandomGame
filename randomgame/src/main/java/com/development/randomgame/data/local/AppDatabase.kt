package com.development.randomgame.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Configuração do Banco de Dados Room para o aplicativo.
 * Define as entidades e a versão do banco de dados.
 */
@Database(entities = [GameEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    // Acesso ao DAO para operações no banco de dados
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Retorna a instância única (Singleton) do banco de dados.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "game_database"
                )
                .fallbackToDestructiveMigration() // Útil durante o desenvolvimento
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
