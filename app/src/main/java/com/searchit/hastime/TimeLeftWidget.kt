package com.searchit.hastime

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import java.util.Calendar

/**
 * Implementation of App Widget functionality.
 */
class TimeLeftWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val remainingTime = calculateRemainingTime(context)
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, remainingTime)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun calculateRemainingTime(context: Context): String {
        // Retrieve the selected date from SharedPreferences or a similar persistence mechanism
        val sharedPrefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val selectedDateInMillis = sharedPrefs.getLong("selectedDateInMillis", 0L)

        if (selectedDateInMillis == 0L) return "No date set"

        val currentTime = Calendar.getInstance().timeInMillis
        val diff = selectedDateInMillis - currentTime

        return if (diff > 0) {
            val daysLeft = diff / (1000 * 60 * 60 * 24)
            val hoursLeft = (diff / (1000 * 60 * 60)) % 24
            val minutesLeft = (diff / (1000 * 60)) % 60
            "$daysLeft days, $hoursLeft hours, $minutesLeft minutes left"
        } else {
            "Date is in the past"
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    time: String
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.time_left_widget)
    views.setTextViewText(R.id.remainingTime, time)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}