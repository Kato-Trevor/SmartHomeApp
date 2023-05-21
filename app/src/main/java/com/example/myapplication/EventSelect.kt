package com.example.myapplication

import android.Manifest
import android.app.AlertDialog

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.widget.ProgressBar

import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.myapplication.databinding.ActivityEventSelectBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class EventSelect : AppCompatActivity() {

    private lateinit var binding: ActivityEventSelectBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventSelectBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContentView(binding.root)

        //The time is Time
        binding.text1.setOnClickListener {
            val intent = Intent(this, RoutineInputs::class.java)
            intent.putExtra("Time-Set", true)
            startActivity(intent)
            finish()
        }
        //It's sunset at Location
        binding.text2.setOnClickListener {
            getLastLocation(18, 50)
        }
        //It's sunrise at Location
        binding.text3.setOnClickListener {
            getLastLocation(6, 42)
        }
        //It's 15 minutes before sunrise at Location
        binding.text4.setOnClickListener {
            getLastLocation(6, 27)
        }
        //It's 15 minutes after sunrise at Location
        binding.text5.setOnClickListener {
            getLastLocation(6, 57)
        }
        //It's 15 minutes before sunset at Location
        binding.text6.setOnClickListener {
            getLastLocation(18, 35)
        }
        //It's 15 minutes after sunset at Location
        binding.text7.setOnClickListener {
            getLastLocation(7, 5)
        }


        //making the specific text bold in the event options
        makeBold(binding.text1, "Time");
        makeBold(binding.text2, "Location");
        makeBold(binding.text3, "Location");
        makeBold(binding.text4, "15");
        makeBold(binding.text4, "Location");
        makeBold(binding.text5, "15");
        makeBold(binding.text5, "Location");
        makeBold(binding.text6, "15");
        makeBold(binding.text6, "Location");
        makeBold(binding.text7, "15");
        makeBold(binding.text7, "Location");

        binding.backArrow.setOnClickListener {
            finish()
        }
    }

    private fun makeBold(textView: TextView, textBold: String) {
        val text = textView.text
        val startIndex = text.indexOf(textBold)
        val endIndex = startIndex + textBold.length;
        if (text is Spannable) {
            text.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex, // Start index
                endIndex, // End index
                Spannable.SPAN_INCLUSIVE_INCLUSIVE // Flags
            )
        } else {
            textView.text = SpannableString.valueOf(text).apply {
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndex, // Start index
                    endIndex, // End index
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE // Flags
                )
            }
        }
    }

    private fun getTime(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar.timeInMillis
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
                Toast.makeText(this, "Please click option again", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Please allow your location to continue", Toast.LENGTH_SHORT).show()
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

    private fun getLastLocation(hour: Int, minute: Int) {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this,"Please check your connection", Toast.LENGTH_SHORT).show()
                    } else {
                        displayProcessingDialog()
                        val lon = location.longitude
                        val lat = location.latitude
                        val intent = Intent(this, RoutineInputs::class.java)
                        intent.putExtra("Location-Set", true)
                        intent.putExtra("Location-Time", getTime(hour, minute))
                        intent.putExtra("Latitude", lat)
                        intent.putExtra("Longitude", lon)
                        intent.putExtra("Country", getCountryName(lat, lon))
                        intent.putExtra("City", getCityName(lat, lon))
                        startActivity(intent)
                        finish()
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
        try {
            val address = geoCoder.getFromLocation(lat, lon, 1)
            countryName = address?.get(0)?.countryName
        }catch(e: Exception){
            Toast.makeText(this, "Please check your connection", Toast.LENGTH_SHORT).show()
        }
        return countryName
    }
    private var processingDialog: AlertDialog? = null

    private fun displayProcessingDialog() {
        val myBuilder = AlertDialog.Builder(this)
        myBuilder.setCancelable(false)

        val dialogLayout = layoutInflater.inflate(R.layout.location_processing_dialog, null)
        myBuilder.setView(dialogLayout)

        val progressBar = dialogLayout.findViewById<ProgressBar>(R.id.progress_bar_location)
        progressBar.isIndeterminate = true
        progressBar.animate()

        processingDialog = myBuilder.create()
        processingDialog?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        processingDialog?.dismiss()
    }

}