package com.searchit.hastime.Room.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.searchit.hastime.Room.Entity.SelectedDateEntity

@Dao
interface SelectedDateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(selectedDate: SelectedDateEntity)

    @Query("SELECT * FROM selected_date WHERE id = 1")
    suspend fun getSelectedDate(): SelectedDateEntity?
}