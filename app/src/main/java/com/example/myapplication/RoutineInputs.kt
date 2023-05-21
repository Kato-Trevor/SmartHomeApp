package com.example.myapplication

import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Looper
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.myapplication.databinding.ActivityRoutineInputsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class RoutineInputs : AppCompatActivity() {

    //View Binding
    private lateinit var binding: ActivityRoutineInputsBinding

    //Shared Preferences
    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREFS_KEY = "MySharedPreferences"

    //Location
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoutineInputsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //notification
        createNotificationChannel()
        //ideas
        val routineIdea = intent.getStringExtra("routineText")
        if (routineIdea != null) {
            binding.editText.setText(routineIdea)
        }
        //time
        val timeSet = intent.getBooleanExtra("Time-Set", false)
        if (timeSet) {
            binding.conditionAdd.visibility = View.VISIBLE
            showTimeDialog()
            intent.putExtra("Time-Set", false)
        }
        //location
        val locationSet = intent.getBooleanExtra("Location-Set", false)
        if (locationSet) {
            binding.conditionAdd.visibility = View.VISIBLE
            getLastLocation()
            intent.putExtra("Location-Set", false)
        }

        val notificationSet = intent.getBooleanExtra("Notification-Set", false)
        if (notificationSet) {
            binding.conditionAdd.visibility = View.VISIBLE
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

        binding.floatingActionButton3.setOnClickListener {
            conditionDialog()
        }
        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE)

        getTimeSharedPreferences()
        getRoutineSharedPreferences()
        getNotificationSharedPreferences()
        getLocationSharedPreferences()

        binding.closeIcon.setOnClickListener {
            sharedPreferences.edit().clear().apply()
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

        myBuilder.setCancelable(false)

        val dialogLayout = layoutInflater.inflate(R.layout.processing_dialog, null)
        myBuilder.setView(dialogLayout)

        val progressBar = dialogLayout.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.isIndeterminate = true
        progressBar.animate()
        val myBuild = myBuilder.create()

        myBuild.show()

        val title = sharedPreferences.getString("routineName", null)
        val text = sharedPreferences.getString("Alert-Dialog", null)
        if (title?.isNotEmpty() == true && text?.isNotEmpty() == true) {
            scheduleNotification(title, text)
        } else {
            Toast.makeText(applicationContext, "Error creating notification", Toast.LENGTH_LONG)
                .show()
        }
        addRoutineRecord()
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

    private fun getLocationSharedPreferences() {
        val location = sharedPreferences.getString("locationPref", null)
        if (location != null) {
            binding.location.visibility = View.VISIBLE
            binding.textVisible.visibility = View.GONE
            binding.timeValue.text = "The time is $location"
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
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            time,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(this, "Notification created successfully", Toast.LENGTH_SHORT).show()
    }

    private fun showAlert(time: Long, title: String?, message: String) {
        val date = Date(time)
        val dateFormat = DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = DateFormat.getTimeFormat(applicationContext)
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
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showLocationDialog(lat: Double, lon: Double) {
        val city = getCityName(lat, lon)
        val country = getCountryName(lat, lon)
        val time = intent.getStringExtra("Location-Time")

        AlertDialog.Builder(this)
            .setTitle("Schedule")
            .setMessage(
                "Location: $city , $country" +
                        "\nTime: $time"
            )
            .setPositiveButton("Accept") { _, _ ->
                binding.location.visibility = View.VISIBLE
                binding.textVisible.visibility = View.GONE
                binding.locationValue.text = "$city, $country at $time"
                //saving the time values with shared preferences
                sharedPreferences.edit().putString("locationPref", "$city, $country").apply()
                val routine = sharedPreferences.getString("routineName", null)
                val notificationText = sharedPreferences.getString("Alert-Dialog", null)
                if (routine != null && notificationText != null) {
                    scheduleNotification(routine, notificationText)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun failed(st: String) {
        AlertDialog.Builder(this)
            .setTitle("Failure $st")
            .setMessage(
                "Failed"
            )
            .setPositiveButton("Accept") { _, _ -> }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug", "You have the permission")
            }
        }
        // Handle other permission request codes if needed
    }


    //checking uses permission
    private fun checkPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    //get user permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    //check location feature
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
                    } else {
                        showLocationDialog(location.latitude, location.longitude)
                    }
                }
            } else {
                Toast.makeText(this, "Please enable your location", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermission()
        }
    }

    private fun getCityName(lat: Double, lon: Double): String? {
        var cityName: String? = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        try {
            val address = geoCoder.getFromLocation(lat, lon, 1)
            cityName = address?.get(0)?.locality
        } catch (e: Exception) {
            Toast.makeText(this, "Please check your connection", Toast.LENGTH_SHORT).show()
        }
        return cityName
    }

    private fun getCountryName(lat: Double, lon: Double): String? {
        var countryName: String? = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lon, 1)

        countryName = address?.get(0)?.countryName
        return countryName
    }

    private fun conditionDialog() {

        val myBuilder = AlertDialog.Builder(this)
        myBuilder.setTitle("Enter condition")

        val input = EditText(this)
        myBuilder.setView(input)

        myBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()

            val inputText = input.text.toString()

            Toast.makeText(applicationContext, "Condition added", Toast.LENGTH_LONG)
                .show()
        }

        myBuilder.setNegativeButton("Close") { dialog, _ ->
            dialog.cancel()
        }
        myBuilder.show()
    }
}
