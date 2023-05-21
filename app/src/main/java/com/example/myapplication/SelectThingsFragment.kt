package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.databinding.ActivityTabbedThingsBinding
import com.example.myapplication.databinding.FragmentSelectThingsBinding


class SelectThingsFragment : Fragment() {

    private lateinit var tabBinding: ActivityTabbedThingsBinding
    private lateinit var binding: FragmentSelectThingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSelectThingsBinding.inflate(inflater, container, false)
        tabBinding = ActivityTabbedThingsBinding.inflate(inflater, container, false)

        val addNotificationIcon = binding.addNotification

        // Handle click events for the add notification layout
        addNotificationIcon.setOnClickListener {
            tabBinding.firstHeading.text = "Select an Action"
            binding.notificationText.visibility = View.VISIBLE
            addNotificationIcon.visibility = View.GONE
        }

        binding.notificationText.setOnClickListener {
            val intent = Intent(context, RoutineInputs::class.java)
            intent.putExtra("Notification-Set", true)
            startActivity(intent)
        }

        return binding.root
    }

}