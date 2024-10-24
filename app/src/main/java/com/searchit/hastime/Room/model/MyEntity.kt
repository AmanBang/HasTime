package com.searchit.hastime.Room.model


import androidx.room.Entity
import androidx.room.PrimaryKey

///NOT USED IT
@Entity(tableName = "my_data")
data class MyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dataField: String,
    val timestamp: Long
)
