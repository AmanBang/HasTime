package com.searchit.hastime.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.searchit.hastime.Room.Dao.EventLogDao
import com.searchit.hastime.Room.model.EventLog
import com.searchit.hastime.Room.model.MyEntity


@Database(entities = [MyEntity::class, EventLog::class], version = 2)
abstract class MyDatabase : RoomDatabase() {
    abstract fun eventLogDao(): EventLogDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "my_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}