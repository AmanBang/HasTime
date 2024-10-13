package com.searchit.hastime
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.searchit.hastime.Room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

// Define the worker inline
class DailyWidgetUpdateWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private lateinit var db: AppDatabase

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            // Fetch data and update the widget
            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
            val thisWidget = ComponentName(applicationContext, TimeLeftWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

            // Fetch remaining time from your source
            val remainingTime = calculateRemainingTime(applicationContext)

            // Update each widget instance with the fetched data
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(applicationContext, appWidgetManager, appWidgetId, remainingTime)
            }

            Result.success()
        }
    }

    private suspend fun calculateRemainingTime(context: Context): String {
        // Initialize Room database
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-name"
        ).build()
        val dataDao = db.selectedDateDao();

        // Fetch the selected date from Room
        val selectedDateEntity = dataDao.getSelectedDate() // This should be a suspend function

        if (selectedDateEntity == null) return "No date set"

        val selectedDateInMillis = selectedDateEntity.dateInMillis
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
