package com.example.myapplication

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val favoritesFragment = FavoritesFragment()
    private val thingsFragment = ThingsFragment()
    private val ideasFragment = IdeasFragment()
    private val routinesFragment = RoutinesFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val selectedFragment = intent.getStringExtra("SELECTED_FRAGMENT")
        if (selectedFragment == "routines") {
            replaceFragment(routinesFragment)
        } else {
            replaceFragment(favoritesFragment)
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Favorites -> replaceFragment(favoritesFragment)
                R.id.Things -> replaceFragment(thingsFragment)
                R.id.Routines -> replaceFragment(routinesFragment)
                R.id.Ideas -> replaceFragment(ideasFragment)
                R.id.Settings -> replaceFragment(settingsFragment)
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.my_layout, fragment)
            transaction.commit()
        }
    }
    private fun deleteDatabaseFile(databaseName: String) {
        val context: Context = applicationContext
        val databasePath = context.getDatabasePath(databaseName)
        if (databasePath.exists()) {
            if (databasePath.delete()) {
                Log.d(TAG, "Database file deleted")
            } else {
                Log.e(TAG, "Failed to delete database file")
            }
        } else {
            Log.d(TAG, "Database file does not exist")
        }
    }
}