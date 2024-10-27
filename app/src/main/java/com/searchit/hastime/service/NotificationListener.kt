package com.searchit.hastime.service


import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationListener : NotificationListenerService() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Called when a notification is posted
        Log.d("NotificationListener", "Notification Posted: ${sbn.packageName}, ${sbn.notification?.tickerText}")

        // You can access notification details here
        val notification = sbn.notification
        val appName = sbn.packageName
        val title = notification?.extras?.getString("android.title")
        val content = notification?.extras?.getString("android.text")
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val channelId = notification?.channelId

        val postTime = sbn.postTime
        val importance = channelId?.let {
            NotificationManagerCompat.from(this).getNotificationChannel(
                it
            )?.importance
        }
        val subText = notification?.extras?.getString(Notification.EXTRA_SUB_TEXT)

        Log.d("NotificationListener", "App: $appName, Title: $title, Content: $content, SubText: $subText, Channel ID: $channelId, Post Time: $postTime, Importance: $importance")


        Log.d("NotificationListener", "Title: $title, Content: $content")

        // Store the notification in Firebase Realtime Database
        if (title != null) {
            if (content != null) {
                if (userId != null) {
                    storeNotificationInFirebase(userId,appName.toString(), title, content)
                }
            }
        }
        // You can store or process this data as needed
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Called when a notification is removed
        Log.d("NotificationListener", "Notification Removed: ${sbn.packageName}")
    }

    private fun storeNotificationInFirebase(userId: String, packageName: String, title: String, content: String) {
        // Sanitize the package name to remove invalid characters
        val sanitizedPackageName = packageName.replace(".", "_") // Replace '.' with '_'
            .replace("#", "_") // Replace '#' with '_'
            .replace("$", "_") // Replace '$' with '_'
            .replace("[", "_") // Replace '[' with '_'
            .replace("]", "_") // Replace ']' with '_'

        // Create a unique ID for the notification
        val currentTimeMillis = System.currentTimeMillis()

        val timeFormated = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val notificationId = timeFormated.format(Date(currentTimeMillis))
//        val notificationId = System.currentTimeMillis().toString() // Or use a UUID

        // Get the Firebase Database reference
        val database = FirebaseDatabase.getInstance()
        val notificationsRef = database.getReference("users/$userId/notifications") // Path includes user ID

        // Create a notification data map
        val notificationData = hashMapOf(
            "title" to title,
            "content" to content,
            "timestamp" to System.currentTimeMillis()
        )

        // Store the notification under the user ID, appending to existing notifications
        notificationsRef.child(sanitizedPackageName).child(notificationId).setValue(notificationData)
            .addOnSuccessListener {
                Log.d("NotificationListener", "Notification saved successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("NotificationListener", "Error saving notification", e)
            }
    }

}