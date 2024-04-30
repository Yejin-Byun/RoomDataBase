package com.example.roomdatabase.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Student::class],
    exportSchema = false, version = 1
)
abstract class MyDatabase : RoomDatabase() {
    abstract fun getMyDao(): MyDAO
    // Migration 부분
    companion object {

        private var INSTANCE: MyDatabase? = null
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
            }
        }

        // 싱글톤 패턴으로 INSTANCE 하나만 return 함
        fun getDatabase(context: Context): MyDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder( // school_database 라는 이름의 db 이름로 return함
                    context, MyDatabase::class.java, "school_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
            }
            return INSTANCE as MyDatabase
        }

    }
}