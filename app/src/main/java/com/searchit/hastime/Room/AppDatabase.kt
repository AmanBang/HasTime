package com.searchit.hastime.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.searchit.hastime.Room.Dao.SelectedDateDao
import com.searchit.hastime.Room.Entity.SelectedDateEntity

@Database(entities = [SelectedDateEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun selectedDateDao(): SelectedDateDao

}