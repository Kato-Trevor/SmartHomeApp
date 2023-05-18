package com.example.myapplication

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.myapplication.databinding.ActivityRoutineInputsBinding
import java.util.*

class RoutineInputs : AppCompatActivity() {

    private lateinit var binding: ActivityRoutineInputsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREFS_KEY = "MySharedPreferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoutineInputsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //notification
        createNotificationChannel()

        val timeSet = intent.getBooleanExtra("Time-Set", false)
        if (timeSet) {
            showTimeDialog()
            intent.putExtra("Time-Set", false)
        }


        val notificationSet = intent.getBooleanExtra("Notification-Set", false)
        if (notificationSet) {
            displayInputDialog()
            intent.putExtra("Notification-Set", false)
        }

        binding.floatingActionButton.setOnClickListener {
            sharedPreferences.edit().putString("routineName", binding.editText.text.toString())
                .apply()
            val intent = Intent(this, EventSelect::class.java)
            startActivity(intent)
        }

        binding.floatingActionButton2.setOnClickListener {
            val intent = Intent(this, TabbedThings::class.java)
            startActivity(intent)
        }


        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)

        getTimeSharedPreferences()
        getRoutineSharedPreferences()
        getNotificationSharedPreferences()

        binding.closeIcon.setOnClickListener {
            finish()
        }

    }

    private fun showTimeDialog() {
        //accessing time values with Calender class
        val myCalender = Calendar.getInstance()
        val hour = myCalender.get(Calendar.HOUR_OF_DAY)
        val minutes = myCalender.get(Calendar.MINUTE)
        val am_pm = myCalender.get(Calendar.AM_PM)

        //creating a TimePickerDialog object
        val myTimeDialog = TimePickerDialog(
            this,
            R.style.TimePickerDialogStyle,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val hourText =
                    if (selectedHour == 0 || selectedHour == 12) "12" else (selectedHour % 12).toString()
                val minuteText = if (selectedMinute < 10) "0$selectedMinute" else "$selectedMinute"
                val ampm = if (am_pm == Calendar.AM) "AM" else "PM"
                val timeText = "$hourText:$minuteText $ampm"

                sharedPreferences.edit().putInt("hourValue", selectedHour).apply()
                sharedPreferences.edit().putInt("minuteValue", selectedMinute).apply()

                binding.time.visibility = View.VISIBLE
                binding.textVisible.visibility = View.GONE

                binding.timeValue.text = "The time is $timeText"
                //saving the time values with shared preferences
                sharedPreferences.edit().putString("timePref", timeText).apply()

            }, hour, minutes, false
        )
        myTimeDialog.show()
    }

    private fun displayInputDialog() {
        val myBuilder = AlertDialog.Builder(this)
        myBuilder.setTitle("Enter a value")

        val input = EditText(this)
        myBuilder.setView(input)

        myBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()

            val inputText = input.text.toString()

            //switching the layouts
            binding.notificationText.visibility = View.GONE
            binding.notification.visibility = View.VISIBLE
            binding.notificationValue.text = "Send Notification: $inputText"

            //shared preferences
            sharedPreferences.edit().putString("Alert-Dialog", inputText).apply()


            //display the processing dialog
            displayProcessingDialog(input.text.toString())
        }

        myBuilder.setNegativeButton("Close") { dialog, _ ->
            dialog.cancel()
        }

        myBuilder.show()
    }

    private fun displayProcessingDialog(inputText: String) {

        val myBuilder = AlertDialog.Builder(this)
        myBuilder.setTitle("Creating new Routine")
        myBuilder.setCancelable(false)

        val dialogLayout = layoutInflater.inflate(R.layout.processing_dialog, null)
        myBuilder.setView(dialogLayout)

        val progressBar = dialogLayout.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.isIndeterminate = true
        progressBar.animate()

        val myBuild = myBuilder.create()
        myBuild.show()

        addRoutineRecord()
        val title = sharedPreferences.getString("routineName", "Failed")
        val routineText = sharedPreferences.getString("Alert-Dialog", "failed text")

        if (title != null && routineText != null) {
            scheduleNotification(title, routineText)
        } else {
            Toast.makeText(applicationContext, "Error creating notification", Toast.LENGTH_LONG)
                .show()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            myBuild.dismiss()
        }, 5000)
    }

    private fun addRoutineRecord() {
        val myRoutine = sharedPreferences.getString("routineName", null)
        val routineText = sharedPreferences.getString("Alert-Dialog", null)
        val databaseHandler: RoutineHandler = RoutineHandler(this)

        //if routine name is given
        if (myRoutine?.isNotEmpty() == true) {

            val data =
                databaseHandler.addRoutine(RoutineModelClass(0, myRoutine, "Never", routineText))

            if (data > -1) {

                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()
                sharedPreferences.edit().clear().apply()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("SELECTED_FRAGMENT", "routines")
                startActivity(intent)
            }
        } else {
            Toast.makeText(applicationContext, "Error creating routine", Toast.LENGTH_LONG).show()
        }
    }

    private fun getTimeSharedPreferences() {
        val timeText = sharedPreferences.getString("timePref", null)
        if (timeText != null) {
            binding.time.visibility = View.VISIBLE
            binding.textVisible.visibility = View.GONE
            binding.timeValue.text = "The time is $timeText"
        }
    }

    private fun getRoutineSharedPreferences() {
        val routineText = sharedPreferences.getString("routineName", null)
        binding.editText.setText(routineText)
    }

    private fun getNotificationSharedPreferences() {
        val notificationText = sharedPreferences.getString("Alert-Dialog", null)
        if (notificationText != null) {
            binding.notificationText.visibility = View.GONE
            binding.notification.visibility = View.VISIBLE
            binding.notificationValue.text = "Send Notification: $notificationText"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.edit().clear().apply()
    }

    private fun scheduleNotification(title: String, message: String) {
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            time,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String?, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date)
            )
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    private fun getTime(): Long {
        val hour = sharedPreferences.getInt("hourValue", 0)
        val minute = sharedPreferences.getInt("minuteValue", 0)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar.timeInMillis
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Channel"
            val desc = "A description of the channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = desc
            val manager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

}