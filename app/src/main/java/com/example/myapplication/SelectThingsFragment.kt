package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityTabbedThingsBinding
import com.example.myapplication.databinding.FragmentFavoritesBinding
import com.example.myapplication.databinding.FragmentSelectThingsBinding
import com.google.android.material.tabs.TabLayout


class SelectThingsFragment : Fragment() {

    private lateinit var tabBinding: ActivityTabbedThingsBinding
    private lateinit var binding: FragmentSelectThingsBinding
    private lateinit var bindingTabbed: ActivityTabbedThingsBinding

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

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.viewPager, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

}