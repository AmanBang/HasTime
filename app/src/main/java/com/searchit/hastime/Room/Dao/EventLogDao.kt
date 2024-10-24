package com.searchit.hastime.Room.Dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.searchit.hastime.Room.model.EventLog

@Dao
interface EventLogDao {
    @Insert
    suspend fun insertEventLog(eventLog: EventLog)

    @Query("SELECT * FROM event_logs")
    suspend fun getAllEventLogs(): List<EventLog>

    @Query("DELETE FROM event_logs")
    suspend fun clearEventLogs()
}
