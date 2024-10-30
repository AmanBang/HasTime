package com.searchit.hastime.service


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.searchit.hastime.R
import com.searchit.hastime.Util.NetworkUtil
import com.searchit.hastime.receiver.ScreenStateReceiver

class MyForegroundService : Service() {
    private val TAG = "MyForegroundService"
    private lateinit var screenStateReceiver: ScreenStateReceiver
    private lateinit var networkUtil: NetworkUtil
    private val handler = Handler(Looper.getMainLooper())  // Handler to schedule service stop
    private val CHANNEL_ID = "MyForegroundServiceChannel"
    private val NOTIFICATION_ID = 1 // Notification ID must be an Int

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        Log.i(TAG, "onCreate: Called it")
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for Foreground Service"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        // Register screen lock/unlock receiver
        screenStateReceiver = ScreenStateReceiver()
        val screenIntentFilter = IntentFilter(Intent.ACTION_SCREEN_ON).apply {
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenStateReceiver, screenIntentFilter)

        // Register network state receiver
        networkUtil = NetworkUtil()
        val networkIntentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkUtil, networkIntentFilter)

        // Schedule the service to stop after 15 minutes (900000 milliseconds)
        handler.postDelayed({
            stopSelf()  // Stops the service after 15 minutes
        }, (13 * 60 * 1000)+40*1000)  // 14.40 minutes in milliseconds
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Has Time")
            .setContentText("Quotes")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        // Continue running as a foreground service
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenStateReceiver)
        unregisterReceiver(networkUtil)

        // Cancel the notification when the service is destroyed
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancel(NOTIFICATION_ID) // Cancel the notification
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}