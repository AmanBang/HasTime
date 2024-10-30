package com.searchit.hastime.Worker


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.searchit.hastime.Room.MyDatabase
import com.searchit.hastime.Room.model.EventLog
import com.searchit.hastime.service.MyForegroundService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {

            Log.i("MyWorker", "doWork: Started")

            // Start Foreground Service
            val serviceIntent = Intent(applicationContext, MyForegroundService::class.java)
            applicationContext.startService(serviceIntent)
            Log.i("MyWorker", "doWork: foreground")

            // Fetch Event Logs and Sync to Firebase
            val database = MyDatabase.getDatabase(applicationContext)
            val eventLogs: List<EventLog> = database.eventLogDao().getAllEventLogs()
            Log.i("MyWorker", "doWork: database")

            if (eventLogs.isNotEmpty()) {
                // Check if the device is connected to the internet
                if (isInternetAvailable(applicationContext)) {
                    // Attempt to sync event logs to Firebase
                    Log.i("MyWorker", "doWork: Internet Available")

                    val uploadSuccess = uploadToFirebase(eventLogs)

                    if (uploadSuccess) {
                        // Clear Event Logs after successful upload
                        database.eventLogDao().clearEventLogs()
                        Result.success()
                    } else {
                        // Retry the job if upload fails (without clearing the logs)
                        Result.retry()
                    }
                } else {
                    // No internet, retry the job later
                    Result.retry()
                }
            } else {
                // No logs to upload
                Result.success()
            }

            // Clear Event Logs after sync
            database.eventLogDao().clearEventLogs()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Method to upload logs to Firebase
    private suspend fun uploadToFirebase(eventLogs: List<EventLog>): Boolean {
        return try {
            Log.i("uploadToFirebase", "In Firebase")
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            val firebaseDatabase = FirebaseDatabase.getInstance()
            val ref = firebaseDatabase.getReference("users/$userId/logs")
            Log.i("uploadToFirebase", "got the Ref")

            for (event in eventLogs) {
                val logEntry = mapOf(
                    "eventType" to event.eventType,
                    "timestamp" to event.timestamp
                )
                ref.push().setValue(logEntry).await()
            }
            Log.i("uploadToFirebase", "Pushed")

            // If all logs are uploaded successfully
            true
        } catch (e: Exception) {
            // If there's an error during upload, return false
            e.printStackTrace()
            false
        }
    }
}