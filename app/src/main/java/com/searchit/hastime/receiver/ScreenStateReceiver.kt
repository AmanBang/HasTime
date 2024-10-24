package com.searchit.hastime.receiver


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.searchit.hastime.Room.MyDatabase
import com.searchit.hastime.Room.model.EventLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreenStateReceiver : BroadcastReceiver() {
    private val TAG = "ScreenStateReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        val eventType = when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> "LOCK"
            Intent.ACTION_SCREEN_ON -> "UNLOCK"
            else -> null
        }

        if (eventType != null) {
            val currentTimeMillis = System.currentTimeMillis()

            val timeFormated = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val formattedTime = timeFormated.format(Date(currentTimeMillis))
            val currentDate = Date()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(currentDate)
            val RTime = "$formattedDate | $formattedTime"
            Log.i(TAG, "onReceive: Event :" + eventType + "| "+ RTime)
            val eventLog = EventLog(eventType = eventType, timestamp = RTime)
            CoroutineScope(Dispatchers.IO).launch {
                MyDatabase.getDatabase(context).eventLogDao().insertEventLog(eventLog)
            }
        }
    }
}