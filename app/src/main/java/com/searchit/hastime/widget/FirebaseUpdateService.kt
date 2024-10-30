package com.searchit.hastime.widget

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import com.google.firebase.database.*
import com.searchit.hastime.R

class FirebaseUpdateService : Service() {
    private lateinit var database: DatabaseReference

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance().getReference("widgetText")

        // Add a listener to detect changes in Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newText = snapshot.getValue(String::class.java) ?: "Default Text"

                // Update the widget with the new text
                updateWidgetText(applicationContext, newText)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error here if needed
            }
        })
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun updateWidgetText(context: Context, text: String) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetComponent = ComponentName(context, MessageWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setTextViewText(R.id.widget_text_view, text)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
