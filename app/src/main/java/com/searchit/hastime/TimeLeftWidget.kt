package com.searchit.hastime

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import androidx.compose.ui.graphics.Canvas
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.searchit.hastime.Room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import androidx.work.WorkerParameters
import kotlinx.coroutines.withContext
/**
 * Implementation of App Widget functionality.
 */
class TimeLeftWidget : AppWidgetProvider() {

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        if (!isRunning) {
            isRunning = true
//            handler.post(updateRunnable(context))
            scheduleDailyWidgetUpdate(context)
        }

//        CoroutineScope(Dispatchers.IO).launch {


//            val remainingTime = calculateRemainingTime(context)
//            // Update each widget instance with the fetched data
//            for (appWidgetId in appWidgetIds) {
//                updateAppWidget(context, appWidgetManager, appWidgetId, remainingTime)
//            }
//        }
    }


    // Define the worker inline
//    class DailyWidgetUpdateWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
//
//        private lateinit var db: AppDatabase
//
//        override suspend fun doWork(): Result {
//            return withContext(Dispatchers.IO) {
//                // Fetch data and update the widget
//                val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
//                val thisWidget = ComponentName(applicationContext, TimeLeftWidget::class.java)
//                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
//
//                // Fetch remaining time from your source
//                val remainingTime = calculateRemainingTime(applicationContext)
//
//                // Update each widget instance with the fetched data
//                for (appWidgetId in appWidgetIds) {
//                    updateAppWidget(applicationContext, appWidgetManager, appWidgetId, remainingTime)
//                }
//
//                Result.success()
//            }
//        }
//
//        private suspend fun calculateRemainingTime(context: Context): String {
//            // Initialize Room database
//            db = Room.databaseBuilder(
//                context,
//                AppDatabase::class.java, "database-name"
//            ).build()
//            val dataDao = db.selectedDateDao();
//
//            // Fetch the selected date from Room
//            val selectedDateEntity = dataDao.getSelectedDate() // This should be a suspend function
//
//            if (selectedDateEntity == null) return "No date set"
//
//            val selectedDateInMillis = selectedDateEntity.dateInMillis
//            val currentTime = Calendar.getInstance().timeInMillis
//            val diff = selectedDateInMillis - currentTime
//
//            return if (diff > 0) {
//                val daysLeft = diff / (1000 * 60 * 60 * 24)
//                val hoursLeft = (diff / (1000 * 60 * 60)) % 24
//                val minutesLeft = (diff / (1000 * 60)) % 60
//                "$daysLeft days, $hoursLeft hours, $minutesLeft minutes left"
//            } else {
//                "Date is in the past"
//            }
//        }
//
//    }

//    private fun updateRunnable(context: Context): Runnable {
//        return object : Runnable {
//            override fun run() {
//                // Fetch data from Room in the background
//                CoroutineScope(Dispatchers.IO).launch {
//                    val remainingTime = calculateRemainingTime(context)
//
//                    // Get widget manager and component name for updating all widget instances
//                    val appWidgetManager = AppWidgetManager.getInstance(context)
//                    val thisWidget = ComponentName(context, TimeLeftWidget::class.java)
//                    val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
//
//                    // Update each widget instance with the fetched data
//                    for (appWidgetId in appWidgetIds) {
//                        updateAppWidget(context, appWidgetManager, appWidgetId, remainingTime)
//                    }
//                }
//                // Schedule the next update in 1 second
//                handler.postDelayed(this, 1000)
//            }
//        }
//    }

    // Function to schedule daily widget update
    fun scheduleDailyWidgetUpdate(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<DailyWidgetUpdateWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

fun createGradientTextBitmap(context: Context, text: String): Bitmap {
    // Create a TextView programmatically
    val textView = TextView(context)
    textView.textSize = 64f  // Set the text size (similar to clock size)
    textView.setTypeface(Typeface.DEFAULT_BOLD)  // Use bold style

    // Apply gradient shader to the text
    val paint = textView.paint
    val width = paint.measureText(text)
    val textShader = LinearGradient(
        0f, 0f, width, textView.textSize,
        intArrayOf(
            Color.parseColor("#FFFFFF"),  // Start color (white)
            Color.parseColor("#DDDDDD")   // End color (light gray)
        ),
        null,
        Shader.TileMode.CLAMP
    )
    textView.paint.shader = textShader
    textView.text = text

    // Measure and layout the text view
    textView.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)

    // Create a bitmap from the TextView
    val bitmap = Bitmap.createBitmap(textView.width, textView.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    textView.draw(canvas)

    return bitmap
}


internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    time: String
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.time_left_widget)
    val bitmap = createGradientTextBitmap(context, time)

    // Set the bitmap to the ImageView in the widget
    views.setImageViewBitmap(R.id.remainingTimeImage, bitmap)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

