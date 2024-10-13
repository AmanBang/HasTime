package com.searchit.hastime.Room.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_date")
data class SelectedDateEntity(
    @PrimaryKey val id: Int = 1, // Singleton pattern, only one row needed
    val dateInMillis: Long

)
