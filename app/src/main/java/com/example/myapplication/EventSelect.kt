package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import com.example.myapplication.databinding.ActivityEventSelectBinding
import com.example.myapplication.databinding.ActivityRoutineInputsBinding
import java.util.*

class EventSelect : AppCompatActivity() {

    private lateinit var binding: ActivityEventSelectBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventSelectBinding.inflate(layoutInflater)
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
            val intent = Intent(this, RoutineInputs::class.java)
            intent.putExtra("Location-Set", true)
            intent.putExtra("Location-Time", getTime(18, 50))
            startActivity(intent)
            finish()
        }
        //It's sunrise at Location
        binding.text3.setOnClickListener {
            val intent = Intent(this, RoutineInputs::class.java)
            intent.putExtra("Location-Set", true)
            intent.putExtra("Location-Time", getTime(6,42))
            startActivity(intent)
            finish()
        }
        //It's 15 minutes before sunrise at Location
        binding.text4.setOnClickListener {
            val intent = Intent(this, RoutineInputs::class.java)
            intent.putExtra("Location-Set", true)
            intent.putExtra("Location-Time", getTime(6,27))
            startActivity(intent)
            finish()
        }
        //It's 15 minutes after sunrise at Location
        binding.text5.setOnClickListener {
            val intent = Intent(this, RoutineInputs::class.java)
            intent.putExtra("Location-Set", true)
            intent.putExtra("Location-Time", getTime(6, 57))
            startActivity(intent)
            finish()
        }
        //It's 15 minutes before sunset at Location
        binding.text6.setOnClickListener {
            val intent = Intent(this, RoutineInputs::class.java)
            intent.putExtra("Location-Set", true)
            intent.putExtra("Location-Time", getTime(18,35))
            startActivity(intent)
            finish()
        }
        //It's 15 minutes after sunset at Location
        binding.text7.setOnClickListener {
            val intent = Intent(this, RoutineInputs::class.java)
            intent.putExtra("Location-Set", true)
            intent.putExtra("Location-Time", getTime(19,5))
            startActivity(intent)
            finish()
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
}