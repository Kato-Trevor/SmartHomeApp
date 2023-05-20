package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivityNewRoutineBinding
import com.example.myapplication.databinding.ActivityRoutineInputsBinding

class NewRoutine : AppCompatActivity() {

    private lateinit var binding: ActivityNewRoutineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, RoutineInputs::class.java)
            startActivity(intent)
        }
        binding.backArrow.setOnClickListener {
            finish()
        }

        setUpRoutinesList()
    }

    private fun getRoutinesList(): ArrayList<RoutineModelClass> {

        //creating the instance of DatabaseHandler class
        val databaseHandler: RoutineHandler = RoutineHandler(this)

        //calling the viewRoutine method of the DatabaseHandler class to read the records
        return databaseHandler.viewRoutine()

    }

    private fun setUpRoutinesList() {
        if (getRoutinesList().size > 0) {
            binding.routinesList.visibility = View.VISIBLE

            // Set the LayoutManager that this RecyclerView will use.
            binding.routinesList.layoutManager = LinearLayoutManager(this)

            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = RoutineAdapter(this, getRoutinesList())

            //adapter instance is set to the recyclerview to inflate the items
            binding.routinesList.adapter = itemAdapter

        } else {
            binding.routinesList.visibility = View.GONE
        }
    }
}