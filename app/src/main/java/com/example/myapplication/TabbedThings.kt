package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivityTabbedThingsBinding
import com.google.android.material.tabs.TabLayout


class TabbedThings : AppCompatActivity() {

    private lateinit var binding: ActivityTabbedThingsBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTabbedThingsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_tabbed_things)

        binding.backArrow.setOnClickListener {
            finish()
        }

        tabLayout = findViewById(R.id.tabLayout)

        //the viewpager manages transitions from one tab to another
        viewPager = findViewById(R.id.viewPager)

        tabLayout.addTab(tabLayout.newTab().setText("THINGS"))
        tabLayout.addTab(tabLayout.newTab().setText("SCENES"))
        tabLayout.addTab(tabLayout.newTab().setText("ROUTINES"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        //The adapter provides the content that is to be displayed for each tab
        val adapter = MyAdapter(this, supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter

        //when the user swipes between pages, the respective tab layout changes
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        //when the user selects any of the tabs on the tab layout the viewpager changes
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


    }
}