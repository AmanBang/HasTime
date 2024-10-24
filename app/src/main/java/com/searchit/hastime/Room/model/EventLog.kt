package com.searchit.hastime.Room.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_logs")
data class EventLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventType: String,  // e.g., "LOCK", "UNLOCK", "INTERNET_ON", "INTERNET_OFF"
    val timestamp: String     // Log the event time in milliseconds
)
