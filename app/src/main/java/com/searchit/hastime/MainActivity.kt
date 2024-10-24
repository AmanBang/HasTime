package com.searchit.hastime

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.searchit.hastime.Room.AppDatabase
import com.searchit.hastime.Room.Dao.SelectedDateDao
import com.searchit.hastime.Room.Entity.SelectedDateEntity
import com.searchit.hastime.Worker.MyWorker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room database and DAO
         db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()

        enableEdgeToEdge()

        setContent {
            MyApp()
        }

        val workRequest = PeriodicWorkRequestBuilder<MyWorker>(15, TimeUnit.MINUTES)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,  // Can also use BackoffPolicy.LINEAR for linear delays
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,  // Minimum delay (e.g., 10 seconds)
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "MyPeriodicWork",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

    @Composable
    fun MyApp() {
        val context = LocalContext.current // Get the current context
        var selectedDate by remember { mutableStateOf<Calendar?>(null) }
        var remainingTime by remember { mutableStateOf("") }

        // Material Theme and UI Elements
        MaterialTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Button to Open Date Picker
                Button(onClick = {
                    showDatePickerDialog(context) { date ->
                        selectedDate = date
                    }
                }) {
                    Text(text = "Select Date")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display the Selected Date
                Text(
                    text = selectedDate?.let { "Selected Date: ${formatDate(it.time)}" } ?: "No date selected",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Button to Calculate Remaining Time
                Button(onClick = { remainingTime = calculateRemainingTime(selectedDate) }) {
                    Text(text = "Calculate Remaining Time")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Remaining Time
                Text(
                    text = remainingTime,
                    fontSize = 18.sp
                )
            }
        }
    }

    private fun saveDateToDatabase(dateInMillis: Long) {
        lifecycleScope.launch {
            val dataDao = db.selectedDateDao();
            dataDao.insert(SelectedDateEntity(dateInMillis = dateInMillis))
        }
    }

    private fun showDatePickerDialog(context: Context, onDateSelected: (Calendar) -> Unit) {
        val currentDate = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                onDateSelected(selectedDate)
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(date)
    }

    private fun calculateRemainingTime(selectedDate: Calendar?): String {
        if (selectedDate == null) return "Please select a date first."

        // Set the future date to 00:00 of that day
        val selectedCalendar = Calendar.getInstance().apply {
            timeInMillis = selectedDate.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val currentTime = Calendar.getInstance().timeInMillis
        Log.i(TAG, "current Date: $currentTime")

        val futureTimeInMillis = selectedCalendar.timeInMillis
        Log.i(TAG, "Future Date: $currentTime")

        //Save to database
        saveDateToDatabase(futureTimeInMillis)

        val diff = futureTimeInMillis - currentTime
        Log.i(TAG, "difference: $diff")

        // Ensure we are looking at a future date
        return if (diff > 0) {
            val daysLeft = diff / (1000 * 60 * 60 * 24)
            val hoursLeft = (diff / (1000 * 60 * 60)) % 24
            val minutesLeft = (diff / (1000 * 60)) % 60
            "$daysLeft days, $hoursLeft hours, $minutesLeft minutes left"
        } else {
            "Selected date is in the past"
        }
    }
}
