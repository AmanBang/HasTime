package com.searchit.hastime.Util


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.searchit.hastime.Room.MyDatabase
import com.searchit.hastime.Room.model.EventLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NetworkUtil : BroadcastReceiver() {
    private val TAG = "NetworkUtil"
    override fun onReceive(context: Context, intent: Intent) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected == true

        val eventType = if (isConnected) "INTERNET_ON" else "INTERNET_OFF"
        val currentTimeMillis = System.currentTimeMillis()

// Format time as HH:mm:ss
        val timeFormated = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val formattedTime = timeFormated.format(Date(currentTimeMillis))

        val currentDate = Date()

// Format date as "yyyy-MM-dd" (Year-Month-Day)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        val RTime = formattedDate + " | " + formattedTime
        Log.i(TAG, "onReceive: Event :" + eventType + "| "+ RTime)
        val eventLog = EventLog(eventType = eventType, timestamp = RTime)

        CoroutineScope(Dispatchers.IO).launch {
            MyDatabase.getDatabase(context).eventLogDao().insertEventLog(eventLog)
        }
    }
}