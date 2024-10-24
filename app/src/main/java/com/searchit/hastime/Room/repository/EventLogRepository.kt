package com.searchit.hastime.Room.repository

import com.searchit.hastime.Room.Dao.EventLogDao
import com.searchit.hastime.Room.model.EventLog


class EventLogRepository(private val eventLogDao: EventLogDao) {

    suspend fun insertEventLog(eventLog: EventLog) {
        eventLogDao.insertEventLog(eventLog)
    }

    suspend fun getAllEventLogs(): List<EventLog> {
        return eventLogDao.getAllEventLogs()
    }

    suspend fun clearEventLogs() {
        eventLogDao.clearEventLogs()
    }
}
