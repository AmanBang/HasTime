package com.searchit.hastime.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.searchit.hastime.R

class MessageWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateWidgetText(context, appWidgetManager, appWidgetIds)
    }

    private fun updateWidgetText(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val database = FirebaseDatabase.getInstance().getReference("widgetText")

        // Listen for changes in Firebase Realtime Database
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val text = snapshot.getValue(String::class.java) ?: "Default Text"

                // Update all widget instances
                appWidgetIds.forEach { appWidgetId ->
                    val views = RemoteViews(context.packageName, R.layout.widget_layout)
                    views.setTextViewText(R.id.widget_text_view, text)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    override fun onEnabled(context: Context) {
        // Start the service to listen for Firebase changes
        context.startService(Intent(context, FirebaseUpdateService::class.java))
    }

    override fun onDisabled(context: Context) {
        // Stop the Firebase listener service when the widget is removed
        context.stopService(Intent(context, FirebaseUpdateService::class.java))
    }
}
