package com.searchit.hastime

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@Preview
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
            Button(onClick = { showDatePickerDialog(context) { date -> selectedDate = date } }) {
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


private fun showDatePickerDialog(context: Context, onDateSelected: (Calendar) -> Unit) {
    val currentDate = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            onDateSelected(selectedDate)

            // Save selected date to SharedPreferences
            val sharedPrefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            with(sharedPrefs.edit()) {
                putLong("selectedDateInMillis", selectedDate.timeInMillis)
                apply()
            }
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

    val currentTime = Calendar.getInstance().time
    val futureDate = selectedDate.time
    val diff = futureDate.time - currentTime.time

    return if (diff > 0) {
        val daysLeft = diff / (1000 * 60 * 60 * 24)
        val hoursLeft = (diff / (1000 * 60 * 60)) % 24
        val minutesLeft = (diff / (1000 * 60)) % 60
        "$daysLeft days, $hoursLeft hours, $minutesLeft minutes left"
    } else {
        "Selected date is in the past"
    }
}

